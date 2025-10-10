package pantalla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import controles.ControlDelJuego;
import entidades.Jugador;
import utilidades.Chat;
import utilidades.Colisiones;
import utilidades.Render;
import utilidades.Inventario;
import objetos.Mapa;  // Tu minimapa
public class PantallaJuego extends ScreenAdapter {

    private Colisiones colisiones;
    public Jugador jugador;
    private ControlDelJuego manejo;
    private Chat chat;

    private Inventario inventario;

    private Texture tileset;
    private TextureRegion[][] mapa;

    // ====== AJUSTE: tus tiles son 48x48 ======
    private final int TILE_SIZE = 48;

    // Tamaño del mundo en tiles
    private final int MAP_WIDTH  = 240;
    private final int MAP_HEIGHT = 160;


    private TmxMapLoader mapLoader;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;
    private Mapa minimapa;  // Referencia al minimapa para conectar cambios
    private float mundoAncho, mundoAlto;

    private OrthographicCamera camara;

    // Mapeo de regiones del minimapa a archivos TMX (ajusta según tus necesidades)
    private java.util.Map<String, String> regionToTmx = new java.util.HashMap<>();
    {
        regionToTmx.put("compras", "compras.tmx");
        regionToTmx.put("centro", "centro.tmx");
        regionToTmx.put("Eduactivo", "Eduactivo.tmx");
        regionToTmx.put("entreteniemiento", "entretenimiento.tmx");
        regionToTmx.put("van", "van.tmx");
    }

    public PantallaJuego(Game juego) {
        // Cámara
        camara = new OrthographicCamera();
        camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camara.position.set(0, 0, 0);
        camara.update();

        Render.batch = new SpriteBatch();
        mapLoader = new TmxMapLoader(Gdx.files.internal(""));  // Ruta base para TMX en assets/
    }

    @Override
    public void show() {

        // Cargar mapa inicial (ej: centro.tmx)
        cambiarEscenario("fuente");  // O el que quieras como default
        // Inicializar colisiones con el TMX (se actualizará en cambiarEscenario)
        colisiones = new Colisiones(null, 48, 0, 0);  // Inicial temporal; se actualiza después
        // ControlDelJuego (pasa colisiones)
        manejo = new ControlDelJuego(colisiones);
        manejo.setCamera(camara);
        // Minimapa (agrega referencia a esta pantalla para callbacks)
        minimapa = new Mapa(this);  // Modificaremos Mapa para aceptar PantallaJuego
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        chat = new Chat(skin, manejo.getJugador());
        inventario = new Inventario(skin, chat, manejo.getJugador());
    }


    public void cambiarEscenario(String nombreRegion) {
        String tmxFile = regionToTmx.get(nombreRegion);
        if (tmxFile == null) {
            Gdx.app.log("PantallaJuego", "TMX no encontrado para región: " + nombreRegion);
            return;
        }

        // Cargar TMX
        tiledMap = mapLoader.load(tmxFile);
        if (renderer != null) renderer.dispose();
        renderer = new OrthogonalTiledMapRenderer(tiledMap, 1f / 48f, Render.batch);  // Escala si tiles son 48x48
        // Tamaño del mundo (de la capa principal)
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        mundoAncho = layer.getWidth() * 48;  // Asume tile size 48; ajusta si es diferente
        mundoAlto = layer.getHeight() * 48;
        // Configurar colisiones desde TMX (actualizar Colisiones)
        actualizarColisionesDesdeTMX();

        // Spawn del jugador (busca objeto "PlayerSpawn" en capa "Objects")
        Vector2 spawn = encontrarSpawnPoint();
        if (spawn != null) {
            jugador = manejo.getJugador();
            jugador.setPersonajeX(spawn.x);
            jugador.setPersonajeY(spawn.y);
        }
        // Actualizar cámara bounds
        camara.update();
        Gdx.app.log("PantallaJuego", "Cambiado a escenario: " + tmxFile);
    }


    private void actualizarColisionesDesdeTMX() {
        // Extraer matriz de colisiones de capa "Colisiones" (tiles con property "collidable=true")
        TiledMapTileLayer colLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Colisiones");
        if (colLayer == null) {
            colLayer = (TiledMapTileLayer) tiledMap.getLayers().get(0);  // Fallback a primera capa
        }
        int width = colLayer.getWidth();
        int height = colLayer.getHeight();
        int[][] tileTypes = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var cell = colLayer.getCell(x, y);
                tileTypes[y][x] = (cell != null && cell.getTile() != null &&
                    cell.getTile().getProperties().containsKey("collidable") &&
                    (Boolean) cell.getTile().getProperties().get("collidable")) ? 1 : 0;
            }
        }

        colisiones = new Colisiones(tileTypes, 48, width, height);  // Tile size 48; ajusta
        manejo.setColisiones(colisiones);  // Agrega setter en ControlDelJuego si no existe
    }


    private Vector2 encontrarSpawnPoint() {
        MapObjects objects = tiledMap.getLayers().get("Objects").getObjects();  // Capa "Objects"
        for (MapObject obj : objects) {
            if (obj instanceof RectangleMapObject && "PlayerSpawn".equals(obj.getName())) {
                Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                return new Vector2(rect.x, rect.y);
            }
        }
        // Fallback si no hay spawn
        return new Vector2(100, 100);
    }




        @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        manejo.actualizar(delta);

        // Seguir al jugador
        float jugadorX = manejo.getJugador().getPersonajeX() + (TILE_SIZE / 2f);
        float jugadorY = manejo.getJugador().getPersonajeY() + (TILE_SIZE / 2f);
        camara.position.set(jugadorX, jugadorY, 0);

        // Clamp cámara al mundo usando el TILE_SIZE correcto (48)
        float minX = camara.viewportWidth  / 2f;
        float maxX = MAP_WIDTH  * TILE_SIZE - camara.viewportWidth  / 2f;
        float minY = camara.viewportHeight / 2f;
        float maxY = MAP_HEIGHT * TILE_SIZE - camara.viewportHeight / 2f;
        camara.position.x = MathUtils.clamp(camara.position.x, minX, maxX);
        camara.position.y = MathUtils.clamp(camara.position.y, minY, maxY);
        camara.update();


            // Render TMX
            renderer.setView(camara);
            renderer.render();


            // Render entidades (jugador, etc.)
            Render.batch.setProjectionMatrix(camara.combined);
            Render.batch.begin();
            manejo.render(Render.batch);
            Render.batch.end();

            // UI: Chat e Inventario
            chat.actualizar(delta);
            chat.render();

            inventario.actualizar(delta);
            inventario.render();

            Render.batch.setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
            Render.batch.begin();

            minimapa.actualizar(delta, 0, 0);  // TargetX/Y dummy para minimapa
            minimapa.render(Render.batch);
            Render.batch.end();

            // Input handling (igual que antes)
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
        camara.setToOrtho(false, width, height);
        camara.update();
        if (chat != null) chat.resize(width, height);
        if (renderer != null) renderer.setView(camara);
        if (inventario != null) inventario.resize(width, height); // Nueva línea
    }

    @Override
    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
        if (renderer != null) renderer.dispose();
        if (Render.batch != null) Render.batch.dispose();
        if (manejo != null) manejo.dispose();
        if (chat != null) chat.dispose();
        if (inventario != null) inventario.dispose();
        if (minimapa != null) minimapa.dispose();
    }
    public Mapa getMinimapa() { return minimapa; }

}
