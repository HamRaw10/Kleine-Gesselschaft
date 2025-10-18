package pantalla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


import controles.ControlDelJuego;
import entidades.Jugador;
import utilidades.Chat;
import utilidades.Colisiones;
import utilidades.Inventario;
import utilidades.Render;

public class PantallaJuego extends ScreenAdapter {

    // --- Render/cámara ---
    private OrthographicCamera camara;
    private ScreenViewport screenViewport;

    // --- Mundo / mapa Tiled ---
    private TiledMap mapaTiled;
    private OrthogonalTiledMapRenderer mapRenderer;

    // Propiedades del mapa
    private int MAP_WIDTH;    // tiles
    private int MAP_HEIGHT;   // tiles
    private int TILE_SIZE_W;  // px
    private int TILE_SIZE_H;  // px

    // Si trabajás en píxeles, dejalo en 1f
    private static final float UNIT_SCALE = 1f;

    // --- Juego/entidades/UI ---
    private Jugador jugador;            // lo provee ControlDelJuego
    private ControlDelJuego manejo;
    private Colisiones colisiones;
    private Chat chat;
    private Inventario inventario;
    private Music musicaFondo;

    // Opcional: resolución “virtual” objetivo del viewport (se recorta al tamaño del mapa)
    private static final float TARGET_VIRTUAL_W = 1280f;
    private static final float TARGET_VIRTUAL_H = 720f;

    @SuppressWarnings("unused")
    private final Game juego; // por si lo necesitás más adelante

    public PantallaJuego(Game juego) {
        this.juego = juego;

        // Cámara (el Viewport la configurará)
        camara = new OrthographicCamera();

        // SpriteBatch global que ya usás en tu proyecto
        Render.batch = new SpriteBatch();
    }

    private void recalcularZoomParaNoSalirDelMapa(float worldWidth, float worldHeight) {
        // Lo que la cámara intenta mostrar (sin zoom) en unidades de mundo
        float vw = camara.viewportWidth;
        float vh = camara.viewportHeight;

        // Necesitamos un zoom tal que:
        // vw*zoom <= worldWidth  y  vh*zoom <= worldHeight
        // => zoom <= worldWidth/vw  y  zoom <= worldHeight/vh
        // Elegimos el MÁS RESTRICTIVO (el más chico).
        float maxZoomPorAncho = worldWidth  / vw;
        float maxZoomPorAlto  = worldHeight / vh;

        // Si el mapa es más chico que el viewport, tendremos que “alejar” (zoom > 1) o “acercar” (zoom < 1).
        // Para que nunca se vea fuera, tomamos el mínimo.
        float zoomSeguro = Math.min(maxZoomPorAncho, maxZoomPorAlto);

        // Evitar números raros o NaN
        if (zoomSeguro <= 0f || Float.isNaN(zoomSeguro) || Float.isInfinite(zoomSeguro)) {
            zoomSeguro = 1f;
        }

        camara.zoom = zoomSeguro;
    }

    @Override
    public void show() {
        // === 1) Cargar mapa y renderer ===
        mapaTiled = new TmxMapLoader().load("exteriores/centro.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(mapaTiled, UNIT_SCALE);

        // === 2) Leer propiedades del TMX ===
        MapProperties props = mapaTiled.getProperties();
        MAP_WIDTH   = props.get("width", Integer.class);
        MAP_HEIGHT  = props.get("height", Integer.class);
        TILE_SIZE_W = props.get("tilewidth", Integer.class);
        TILE_SIZE_H = props.get("tileheight", Integer.class);

        // Tamaño real del mundo en píxeles
        final float worldWidth  = MAP_WIDTH  * TILE_SIZE_W * UNIT_SCALE;
        final float worldHeight = MAP_HEIGHT * TILE_SIZE_H * UNIT_SCALE;

        // Viewport SIN barras negras
        screenViewport = new ScreenViewport(camara);
        screenViewport.apply(true);

// Posición inicial cualquiera (se va a recalcular el zoom)
        camara.position.set(worldWidth / 2f, worldHeight / 2f, 0f);
        recalcularZoomParaNoSalirDelMapa(worldWidth, worldHeight);  // <<< llama a este método (abajo)
        camara.update();


        // === 4) Colisiones, control, UI, música ===
        colisiones = new Colisiones();
        colisiones.cargarDesdeMapa(mapaTiled, "colisiones", UNIT_SCALE);

        manejo = new ControlDelJuego(colisiones);
        manejo.setCamera(camara);
        manejo.setViewport(screenViewport);   // << MUY IMPORTANTE para el input

        jugador = manejo.getJugador();

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        chat = new Chat(skin, jugador);
        inventario = new Inventario(skin, chat, jugador);

        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("assets/musica1.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.5f);
        musicaFondo.play();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Lógica
        manejo.actualizar(delta);

        // Seguir cámara al jugador
        float jugadorX = manejo.getJugador().getPersonajeX();
        float jugadorY = manejo.getJugador().getPersonajeY();
        camara.position.set(jugadorX, jugadorY, 0f);

        // Clamp: que la cámara nunca muestre fuera del mapa
        float worldWidth  = MAP_WIDTH  * TILE_SIZE_W * UNIT_SCALE;
        float worldHeight = MAP_HEIGHT * TILE_SIZE_H * UNIT_SCALE;

        // OJO: con ortho, el “medio” visible depende de viewportWidth*zoom
        float halfW = (camara.viewportWidth  * camara.zoom) / 2f;
        float halfH = (camara.viewportHeight * camara.zoom) / 2f;

        float minX = halfW;
        float maxX = Math.max(halfW, worldWidth  - halfW);
        float minY = halfH;
        float maxY = Math.max(halfH, worldHeight - halfH);

        camara.position.x = MathUtils.clamp(camara.position.x, minX, maxX);
        camara.position.y = MathUtils.clamp(camara.position.y, minY, maxY);
        camara.update();

        screenViewport.apply();


        // === Mapa ===
        mapRenderer.setView(camara);
        mapRenderer.render();

        // === Entidades ===
        Render.batch.setProjectionMatrix(camara.combined);
        Render.batch.begin();
        manejo.render(Render.batch);
        Render.batch.end();

        // === UI (sus Stages deberían tener su propio ScreenViewport adentro) ===
        chat.actualizar(delta);
        chat.render();

        inventario.actualizar(delta);
        inventario.render();

        // Input focus
        if (chat.isChatVisible()) {
            chat.setInputProcessor();
        } else if (inventario.isVisible()) {
            inventario.setInputProcessor();
        } else {
            Gdx.input.setInputProcessor(manejo.getInputProcessor());
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
        if (chat != null) chat.resize(width, height);
        if (inventario != null) inventario.resize(width, height);
    }


    @Override
    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        if (mapaTiled != null) mapaTiled.dispose();
        if (Render.batch != null) Render.batch.dispose();
        if (manejo != null) manejo.dispose();
        if (chat != null) chat.dispose();
        if (inventario != null) inventario.dispose();
        if (musicaFondo != null) musicaFondo.dispose();
    }
}
