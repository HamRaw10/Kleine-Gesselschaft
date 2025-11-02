package pantalla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.Iterator;
import java.lang.reflect.Method;

import controles.ControlDelJuego;
import entidades.Jugador;
import utilidades.Chat;
import utilidades.Colisiones;
import utilidades.Inventario;
import utilidades.Portal;
import utilidades.Render;
import utilidades.DebugOverlay;

public class PantallaJuego extends ScreenAdapter {

    // ===== Cámara / Viewport =====
    private OrthographicCamera camara;
    private ScreenViewport screenViewport;
    private DebugOverlay debugOverlay;
    private OrthographicCamera uiCamera; // para overlays a pantalla

    // ===== Mapas =====
    private TiledMap mapaTiled;
    private OrthogonalTiledMapRenderer mapRenderer;
    private int MAP_WIDTH, MAP_HEIGHT, TILE_SIZE_W, TILE_SIZE_H;
    private static final float UNIT_SCALE = 1f;
    private String mapaActualPath = null;

    // Portales
    private final Array<Portal> portales = new Array<>();

    // Mapa inicial (los demás los cargamos por ruta existente, sin lista blanca)
    private static final String MAPA_INICIAL = "exteriores/compras.tmx";

    // ===== Juego/UI =====
    private Jugador jugador;
    private ControlDelJuego manejo;
    private Colisiones colisiones;
    private Chat chat;
    private Inventario inventario;
    private Music musicaFondo;
    private Stage hud;          // HUD para dibujar textos/íconos
    private Label lblMonedas;   // etiqueta de monedas
    private Skin skinUI;        // reutilizamos el uiskin.json si existe
    private boolean dineroInicializado = false; // para setear los 50 una sola vez

    // ===== Fade / transición =====
    private enum TransitionState { NONE, FADING_OUT, SWITCHING, FADING_IN }
    private TransitionState transitionState = TransitionState.NONE;
    private float fadeAlpha = 0f;
    private float fadeSpeed = 2.5f;
    private String pendingMap = null;
    private float pendingSpawnX = 0f, pendingSpawnY = 0f;
    private String pendingTrans = "none";

    // Debug shapes
    private ShapeRenderer shape;

    // Spawn inicial centrado (una sola vez tras crear Jugador)
    private boolean spawnInicialHecho = false;

    @SuppressWarnings("unused")
    private final Game juego;

    public PantallaJuego(Game juego) {
        this.juego = juego;

        camara = new OrthographicCamera();
        screenViewport = new ScreenViewport(camara);
        screenViewport.apply(true);

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();

        if (Render.batch == null) Render.batch = new SpriteBatch();
        shape = new ShapeRenderer();
    }

    // ===== Utilidades =====
    private boolean existeMapa(String path) {
        return path != null && Gdx.files.internal(path).exists();
    }

    // Resuelve “centro.tmx” -> “<carpetaActual>/centro.tmx”; normaliza errores comunes
    private String canonicalizarDestino(String raw) {
        if (raw == null) return null;
        String s = raw.trim().replace('\\', '/');

        // normalizaciones comunes (ajusta a tus nombres reales)
        if (s.equalsIgnoreCase("exteriores/eduactivo.tmx") ||
            s.equalsIgnoreCase("exteriores/educativo.tmx") ||
            s.equalsIgnoreCase("exteriores/eduactiva.tmx")) {
            s = "exteriores/Eduactivo.tmx";
        }

        // si viene sin carpeta, resolver relativo al mapa actual
        if (!s.contains("/") && mapaActualPath != null && mapaActualPath.contains("/")) {
            String dir = mapaActualPath.substring(0, mapaActualPath.lastIndexOf('/') + 1);
            s = dir + s;
        }
        return s;
    }

    private MapLayer getLayerIgnoreCase(TiledMap map, String name) {
        MapLayer exact = map.getLayers().get(name);
        if (exact != null) return exact;
        for (MapLayer l : map.getLayers()) {
            if (l.getName() != null && l.getName().equalsIgnoreCase(name)) return l;
        }
        return null;
    }

    private String getPropStr(MapObject obj, String... keys) {
        // intento exacto
        for (String k : keys) {
            Object v = obj.getProperties().get(k);
            if (v != null) return v.toString();
        }
        // intento case-insensitive (Iterator fix)
        for (String want : keys) {
            Iterator<String> it = obj.getProperties().getKeys();
            while (it.hasNext()) {
                String k = it.next();
                if (k != null && k.equalsIgnoreCase(want)) {
                    Object v = obj.getProperties().get(k);
                    if (v != null) return v.toString();
                }
            }
        }
        return null;
    }

    private void recalcularZoomParaNoSalirDelMapa(float worldWidth, float worldHeight) {
        float vw = camara.viewportWidth, vh = camara.viewportHeight;
        float maxZoomPorAncho = worldWidth / vw;
        float maxZoomPorAlto  = worldHeight / vh;
        float zoomSeguro = Math.min(maxZoomPorAncho, maxZoomPorAlto);
        if (zoomSeguro <= 0f || Float.isNaN(zoomSeguro) || Float.isInfinite(zoomSeguro)) zoomSeguro = 1f;
        camara.zoom = zoomSeguro;
    }

    // ===== Portales =====
    private void cargarPortalesDesdeTiled(TiledMap map) {
        portales.clear();
        MapLayer interacciones = getLayerIgnoreCase(map, "interacciones");
        if (interacciones == null) {
            Gdx.app.log("PORTAL","No hay capa 'interacciones' en este TMX");
            return;
        }
        for (MapObject obj : interacciones.getObjects()) {
            if (!(obj instanceof RectangleMapObject)) continue;

            String tipo = getPropStr(obj, "tipo", "class", "Tipo");
            if (tipo == null || !tipo.equalsIgnoreCase("portal")) continue;

            Rectangle r = ((RectangleMapObject) obj).getRectangle();
            Portal p = new Portal();
            p.rect = new Rectangle(r);

            String tm = canonicalizarDestino(getPropStr(obj, "targetMap", "targetmap", "destino", "map"));
            String ta = getPropStr(obj, "targetArea", "area", "targetarea");
            p.targetMap  = (tm != null && !tm.isEmpty()) ? tm : null;
            p.targetArea = (ta != null && !ta.isEmpty()) ? ta : null;

            String sx = getPropStr(obj, "spawnX", "spawnx", "spawn_x");
            String sy = getPropStr(obj, "spawnY", "spawny", "spawn_y");
            try { p.spawnX = (sx != null) ? Float.parseFloat(sx) : r.x; } catch (Exception e) { p.spawnX = r.x; }
            try { p.spawnY = (sy != null) ? Float.parseFloat(sy) : r.y; } catch (Exception e) { p.spawnY = r.y; }

            String tr = getPropStr(obj, "transicion", "transition");
            p.transicion = (tr != null) ? tr : "none";

            portales.add(p);
        }
        if (portales.size == 0) Gdx.app.log("PORTAL","No se cargaron portales en este mapa.");
    }

    // ===== Carga de mapas =====
    private void notificarControlColisionesActualizadas() {
        if (manejo == null) return;
        try {
            Method m = manejo.getClass().getMethod("setColisiones", Colisiones.class);
            m.invoke(manejo, colisiones);
        } catch (Exception ignored) {
            // si no existe el setter, no pasa nada
        }
    }

    private void cargarMapaPorRuta(String tmxPath) {
        String canon = canonicalizarDestino(tmxPath);
        if (!existeMapa(canon)) {
            Gdx.app.error("MAP", "Mapa inexistente: " + canon);
            return;
        }

        if (mapRenderer != null) { mapRenderer.dispose(); mapRenderer = null; }
        if (mapaTiled != null)   { mapaTiled.dispose();  mapaTiled  = null; }

        Gdx.app.log("MAP", "Cargando mapa: " + canon);
        mapaTiled   = new TmxMapLoader().load(canon);
        mapRenderer = new OrthogonalTiledMapRenderer(mapaTiled, UNIT_SCALE);
        mapaActualPath = canon;

        MapProperties props = mapaTiled.getProperties();
        MAP_WIDTH   = props.get("width", Integer.class);
        MAP_HEIGHT  = props.get("height", Integer.class);
        TILE_SIZE_W = props.get("tilewidth", Integer.class);
        TILE_SIZE_H = props.get("tileheight", Integer.class);

        if (colisiones == null) colisiones = new Colisiones();
        colisiones.cargarDesdeMapa(mapaTiled, "colisiones", UNIT_SCALE);
        cargarPortalesDesdeTiled(mapaTiled);

        float worldW = MAP_WIDTH * TILE_SIZE_W * UNIT_SCALE;
        float worldH = MAP_HEIGHT * TILE_SIZE_H * UNIT_SCALE;
        recalcularZoomParaNoSalirDelMapa(worldW, worldH);

        // avisar al control que hay nuevas colisiones
        notificarControlColisionesActualizadas();

        // si volvemos a un mapa desde cero, permití re-centrar al jugador si hace falta
        spawnInicialHecho = false;
    }

    // ===== Ciclo de vida =====
    @Override
    public void show() {
        debugOverlay = new DebugOverlay();
        cargarMapaPorRuta(MAPA_INICIAL);

        // Cámara centrada
        float worldWidth  = MAP_WIDTH  * TILE_SIZE_W * UNIT_SCALE;
        float worldHeight = MAP_HEIGHT * TILE_SIZE_H * UNIT_SCALE;
        camara.position.set(worldWidth / 2f, worldHeight / 2f, 0f);
        camara.update();

        manejo = new ControlDelJuego(colisiones);
        manejo.setCamera(camara);
        manejo.setViewport(screenViewport);
        jugador = manejo.getJugador();

        if (Gdx.files.internal("uiskin.json").exists()) {
            Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
            if (jugador != null) {
                chat = new Chat(skin, jugador);
                inventario = new Inventario(skin, chat, jugador);
            }
        }

        // ⬇️ Crear HUD (después de configurar cámaras/viewport)
        hud = new Stage(new ScreenViewport());
// Si tenés skinUI, usamos ese, si no, creamos un Label “fallback” sin skin
        if (skinUI != null) {
            lblMonedas = new Label("Monedas: 0", skinUI);
        } else {
            // fallback simple si faltara uiskin.json
            lblMonedas = new Label("Monedas: 0", new Skin(Gdx.files.internal("uiskin.json")));
        }
        lblMonedas.setPosition(10, Gdx.graphics.getHeight() - 30); // esquina sup. izquierda
        hud.addActor(lblMonedas);


        if (Gdx.files.internal("musica1.mp3").exists()) {
            musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica1.mp3"));
            musicaFondo.setLooping(true);
            musicaFondo.setVolume(0.5f);
            musicaFondo.play();
        }
    }

    // ===== Transiciones =====
    private void prepararTransicionMapa(String target, float sx, float sy, String tr) {
        pendingMap = target;
        pendingSpawnX = sx;
        pendingSpawnY = sy;
        pendingTrans = (tr != null) ? tr : "none";
        transitionState = TransitionState.FADING_OUT;
        if (jugador != null) jugador.setBloqueado(true);
    }

    private void realizarCambioDeMapa() {
        if (pendingMap == null) { transitionState = TransitionState.FADING_IN; return; }

        String canon = canonicalizarDestino(pendingMap);
        if (!existeMapa(canon)) {
            Gdx.app.error("PORTAL", "Destino inexistente: " + canon);
            transitionState = TransitionState.FADING_IN;
            return;
        }
        if (mapaActualPath != null && mapaActualPath.equals(canon)) {
            reubicarEnMapaActual(pendingSpawnX, pendingSpawnY);
            transitionState = TransitionState.FADING_IN;
            return;
        }

        cargarMapaPorRuta(canon);

        boolean esInterior = canon.toLowerCase().contains("arcade") ||
            canon.toLowerCase().contains("bibloteca") ||
            canon.toLowerCase().contains("cine") ||
            canon.toLowerCase().contains("coffeshop") ||
            canon.toLowerCase().contains("communitycenter") ||
            canon.toLowerCase().contains("herramientas") ||
            canon.toLowerCase().contains("hippie_house") ||
            canon.toLowerCase().contains("pub") ||
            canon.toLowerCase().contains("supermercado");


        if (jugador != null) {
            jugador.cancelarMovimiento();
            jugador.setPos(pendingSpawnX, pendingSpawnY);
            jugador.setEscala(esInterior ? 0.7f : 1f); // cambia tamaño solo adentro
        }

        camara.position.set(pendingSpawnX, pendingSpawnY, 0f);
        camara.update();

        transitionState = TransitionState.FADING_IN;

        // clamping del spawn y centrado estético en Y
        float worldW = MAP_WIDTH  * TILE_SIZE_W * UNIT_SCALE;
        float worldH = MAP_HEIGHT * TILE_SIZE_H * UNIT_SCALE;
        float spawnX = pendingSpawnX;
        float spawnY = worldH * 0.5f;

        float halfW = (camara.viewportWidth  * camara.zoom) / 2f;
        float halfH = (camara.viewportHeight * camara.zoom) / 2f;
        float margen = 4f;
        spawnX = MathUtils.clamp(spawnX, halfW + margen, Math.max(halfW, worldW - halfW - margen));
        spawnY = MathUtils.clamp(spawnY, halfH + margen, Math.max(halfH, worldH - halfH - margen));

        if (TILE_SIZE_W > 0) spawnX = Math.round(spawnX / TILE_SIZE_W) * TILE_SIZE_W;
        if (TILE_SIZE_H > 0) spawnY = Math.round(spawnY / TILE_SIZE_H) * TILE_SIZE_H;

        if (jugador != null) {
            jugador.cancelarMovimiento();
            jugador.setPos(spawnX, spawnY);
        }
        camara.position.set(spawnX, spawnY, 0f);
        camara.update();

        transitionState = TransitionState.FADING_IN;
    }

    private void reubicarEnMapaActual(float sx, float sy) {
        float worldW = MAP_WIDTH  * TILE_SIZE_W * UNIT_SCALE;
        float worldH = MAP_HEIGHT * TILE_SIZE_H * UNIT_SCALE;
        float halfW = (camara.viewportWidth  * camara.zoom) / 2f;
        float halfH = (camara.viewportHeight * camara.zoom) / 2f;
        float margen = 4f;

        float spawnX = MathUtils.clamp(sx, halfW + margen, Math.max(halfW, worldW - halfW - margen));
        float spawnY = MathUtils.clamp(worldH * 0.5f, halfH + margen, Math.max(halfH, worldH - halfH - margen));

        if (TILE_SIZE_W > 0) spawnX = Math.round(spawnX / TILE_SIZE_W) * TILE_SIZE_W;
        if (TILE_SIZE_H > 0) spawnY = Math.round(spawnY / TILE_SIZE_H) * TILE_SIZE_H;

        if (jugador != null) {
            jugador.cancelarMovimiento();
            jugador.setPos(spawnX, spawnY);
        }
        camara.position.set(spawnX, spawnY, 0f);
        camara.update();
    }

    // ===== Input de portales =====
    private void procesarClickPortalesSiCorresponde() {
        if ((chat != null && chat.isChatVisible()) || (inventario != null && inventario.isVisible())) return;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector2 world = screenViewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            for (Portal p : portales) {
                if (p.rect.contains(world.x, world.y)) {
                    prepararTransicionMapa(p.targetMap, p.spawnX, p.spawnY, p.transicion);
                    return;
                }
            }
        }
    }

    // ===== Render =====
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Transición
        switch (transitionState) {
            case FADING_OUT:
                fadeAlpha = Math.min(1f, fadeAlpha + fadeSpeed * delta);
                if (fadeAlpha >= 1f) {
                    transitionState = TransitionState.SWITCHING;
                    realizarCambioDeMapa();
                }
                break;
            case FADING_IN:
                fadeAlpha = Math.max(0f, fadeAlpha - fadeSpeed * delta);
                if (fadeAlpha <= 0f) {
                    transitionState = TransitionState.NONE;
                    if (jugador != null) jugador.setBloqueado(false);
                }
                break;
            default: break;
        }

        // Lógica
        manejo.actualizar(delta);

        // Si Jugador aparece luego, enganchar UI y centrar una vez
        if (jugador == null) {
            jugador = manejo.getJugador();
            if (jugador != null && chat == null && Gdx.files.internal("uiskin.json").exists()) {
                Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
                chat = new Chat(skin, jugador);
                inventario = new Inventario(skin, chat, jugador);
            }
        }
        // Inicializar monedas a 50 una sola vez
        if (jugador != null && !dineroInicializado) {
            try {
                // Si usás la clase Moneda que te pasé:
                if (jugador.getDinero().getCantidad() == 0) {
                    jugador.getDinero().setCantidad(50);
                }
            } catch (Exception ignored) {
                // Si aún no integraste Moneda, no pasa nada
            }
            dineroInicializado = true;
        }

        if (jugador != null && !spawnInicialHecho && mapaTiled != null) {
            float worldW = MAP_WIDTH  * TILE_SIZE_W * UNIT_SCALE;
            float worldH = MAP_HEIGHT * TILE_SIZE_H * UNIT_SCALE;
            jugador.setPos(worldW * 0.5f, worldH * 0.5f); // spawn inicial centrado
            spawnInicialHecho = true;
        }

        // LÍMITES del jugador (por estética y seguridad)
        if (jugador != null && mapaTiled != null) {
            float worldW = MAP_WIDTH  * TILE_SIZE_W * UNIT_SCALE;
            float worldH = MAP_HEIGHT * TILE_SIZE_H * UNIT_SCALE;
            float margen = 1f;
            float px = MathUtils.clamp(jugador.getPersonajeX(), margen, Math.max(margen, worldW - margen));
            float py = MathUtils.clamp(jugador.getPersonajeY(), margen, Math.max(margen, worldH - margen));
            if (px != jugador.getPersonajeX() || py != jugador.getPersonajeY()) jugador.setPos(px, py);
        }

        // Cámara sigue al jugador
        float jugadorX = (jugador != null) ? jugador.getPersonajeX()
            : (MAP_WIDTH * TILE_SIZE_W * UNIT_SCALE) * 0.5f;
        float jugadorY = (jugador != null) ? jugador.getPersonajeY()
            : (MAP_HEIGHT * TILE_SIZE_H * UNIT_SCALE) * 0.5f;
        camara.position.set(jugadorX, jugadorY, 0f);

        // Clamp de cámara
        float worldWidth  = MAP_WIDTH  * TILE_SIZE_W * UNIT_SCALE;
        float worldHeight = MAP_HEIGHT * TILE_SIZE_H * UNIT_SCALE;
        float halfW = (camara.viewportWidth  * camara.zoom) / 2f;
        float halfH = (camara.viewportHeight * camara.zoom) / 2f;
        camara.position.x = MathUtils.clamp(camara.position.x, halfW, Math.max(halfW, worldWidth  - halfW));
        camara.position.y = MathUtils.clamp(camara.position.y, halfH, Math.max(halfH, worldHeight - halfH));
        camara.update();

        // Viewport y render de mapa
        screenViewport.apply();
        mapRenderer.setView(camara);
        mapRenderer.render();

        // (opcional) dibujar contorno de portales
        shape.setProjectionMatrix(camara.combined);
        shape.begin(ShapeRenderer.ShapeType.Line);
        for (Portal p : portales) shape.rect(p.rect.x, p.rect.y, p.rect.width, p.rect.height);
        shape.end();

        // Entidades
        Render.batch.setProjectionMatrix(camara.combined);
        Render.batch.begin();
        manejo.render(Render.batch);
        Render.batch.end();

        // UI
        if (chat != null) { chat.actualizar(delta); chat.render(); }
        if (inventario != null) { inventario.actualizar(delta); inventario.render(); }

        // Input según UI visible
        if (chat != null && chat.isChatVisible()) {
            chat.setInputProcessor();
        } else if (inventario != null && inventario.isVisible()) {
            inventario.setInputProcessor();
        } else {
            Gdx.input.setInputProcessor(manejo.getInputProcessor());
        }
        // === HUD de monedas ===
        if (jugador != null && lblMonedas != null) {
            lblMonedas.setText("Monedas: " + jugador.getDinero().getCantidad());
        }
        if (hud != null) {
            hud.act(delta);
            hud.draw();
        }

        debugOverlay.pollToggleKey();

        debugOverlay.render(
            shape,
            Render.batch,
            jugador,
            colisiones,
            camara,
            uiCamera,
            TILE_SIZE_W,
            TILE_SIZE_H,
            mapaActualPath // puedes pasar null si no querés mostrarlo
        );


        // Clicks en portales solo cuando no estamos en fade-out
        if (transitionState == TransitionState.NONE) procesarClickPortalesSiCorresponde();

        // Overlay negro del fade (con blending)
        if (fadeAlpha > 0f) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            uiCamera.update();
            shape.setProjectionMatrix(uiCamera.combined);
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(0f, 0f, 0f, MathUtils.clamp(fadeAlpha, 0f, 1f));
            shape.rect(0, 0, uiCamera.viewportWidth, uiCamera.viewportHeight);
            shape.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);
        }


    }

    @Override
    public void resize(int width, int height) {
        if (screenViewport != null) {
            screenViewport.update(width, height, true);
            float worldWidth  = MAP_WIDTH  * TILE_SIZE_W * UNIT_SCALE;
            float worldHeight = MAP_HEIGHT * TILE_SIZE_H * UNIT_SCALE;
            recalcularZoomParaNoSalirDelMapa(worldWidth, worldHeight);
            camara.update();
        }
        uiCamera.setToOrtho(false, width, height);
        uiCamera.update();

        if (chat != null) chat.resize(width, height);
        if (inventario != null) inventario.resize(width, height);
        if (hud != null) {
            hud.getViewport().update(width, height, true);
        }

    }

    @Override
    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        if (mapaTiled != null) mapaTiled.dispose();
        if (manejo != null) manejo.dispose();
        if (chat != null) chat.dispose();
        if (inventario != null) inventario.dispose();
        if (musicaFondo != null) musicaFondo.dispose();
        if (shape != null) shape.dispose();
        if (hud != null) hud.dispose();
        if (debugOverlay != null) debugOverlay.dispose();

    }

    private String areaActual = null;
    private void cambiarAreaPorClick(String targetArea, float spawnX, float spawnY, String transicion) {
        if (mapaTiled == null || targetArea == null) return;
        if (targetArea.equals(areaActual)) return;

        for (MapLayer layer : mapaTiled.getLayers()) {
            if (layer.getName() != null && layer.getName().startsWith("AREA_")) {
                layer.setVisible(layer.getName().equals(targetArea));
            }
        }
        areaActual = targetArea;

        String capaColision = "colisiones_" + targetArea.substring("AREA_".length());
        if (mapaTiled.getLayers().get(capaColision) != null) {
            colisiones.cargarDesdeMapa(mapaTiled, capaColision, UNIT_SCALE);
        } else {
            colisiones.cargarDesdeMapa(mapaTiled, "colisiones", UNIT_SCALE);
        }
        notificarControlColisionesActualizadas();

        if (jugador != null) {
            jugador.cancelarMovimiento();
            jugador.setPos(spawnX, spawnY);
        }
        camara.position.set(spawnX, spawnY, 0f);
        camara.update();
    }
}
