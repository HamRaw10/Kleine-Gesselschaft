package pantalla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.badlogic.gdx.math.Rectangle;

import controles.ControlDelJuego;
import entidades.Jugador;
import utilidades.Chat;
import utilidades.Colisiones;
import utilidades.Render;
import utilidades.Inventario;

public class PantallaJuego extends ScreenAdapter {

    private OrthographicCamera camara;
    private Jugador jugador;

    private TiledMap mapaTiled;
    private OrthogonalTiledMapRenderer mapRenderer;

    private Colisiones colisiones;
    private ControlDelJuego manejo;
    private Chat chat;
    private Inventario inventario;

    // Medidas del mapa leídas del TMX
    private int MAP_WIDTH;   // en tiles
    private int MAP_HEIGHT;  // en tiles
    private int TILE_SIZE_W; // px
    private int TILE_SIZE_H; // px

    // Si trabajás en “pixeles” como mundo, dejalo en 1f
    private static final float UNIT_SCALE = 1f;

    public PantallaJuego(Game juego) {
        camara = new OrthographicCamera();
        camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camara.update();

        Render.batch = new SpriteBatch();
    }

    @Override
    public void show() {
        // 1) Cargar el TMX (ruta pedida)
        mapaTiled = new TmxMapLoader().load("exteriores/centro.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(mapaTiled, UNIT_SCALE);

        // 2) Propiedades del mapa
        MapProperties props = mapaTiled.getProperties();
        MAP_WIDTH   = props.get("width", Integer.class);
        MAP_HEIGHT  = props.get("height", Integer.class);
        TILE_SIZE_W = props.get("tilewidth", Integer.class);
        TILE_SIZE_H = props.get("tileheight", Integer.class);

        // 3) Construir matriz de colisiones desde capa de objetos "colision"
        int[][] solids = new int[MAP_HEIGHT][MAP_WIDTH]; // 0 libre / 1 sólido

        MapLayer capaColision = mapaTiled.getLayers().get("colision"); // <-- cambia el nombre si tu capa se llama distinto
        if (capaColision != null) {
            for (MapObject obj : capaColision.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    Rectangle r = ((RectangleMapObject) obj).getRectangle();

                    int x0 = Math.max(0, (int) Math.floor(r.x / TILE_SIZE_W));
                    int y0 = Math.max(0, (int) Math.floor(r.y / TILE_SIZE_H));
                    int x1 = Math.min(MAP_WIDTH  - 1, (int) Math.floor((r.x + r.width)  / TILE_SIZE_W));
                    int y1 = Math.min(MAP_HEIGHT - 1, (int) Math.floor((r.y + r.height) / TILE_SIZE_H));

                    for (int y = y0; y <= y1; y++) {
                        for (int x = x0; x <= x1; x++) {
                            solids[y][x] = 1;
                        }
                    }
                }
            }
        }

        // 4) Instanciar Colisiones con la grilla derivada del TMX
        int tileSizeParaFisica = Math.max(TILE_SIZE_W, TILE_SIZE_H);
        // Usa la firma que tenga tu clase Colisiones:
        //   a) Si tu constructor es (int[][], int, int, int):
        colisiones = new Colisiones(solids, tileSizeParaFisica, MAP_WIDTH, MAP_HEIGHT);
        //   b) Si te marca "Expected 3 arguments": cambia por la variante correcta que tengas, p.e.:
        // colisiones = new Colisiones(solids, tileSizeParaFisica, MAP_WIDTH); // <-- solo si tu clase lo pide

        // 5) Control del juego (personaje se mantiene)
        manejo = new ControlDelJuego(colisiones);
        manejo.setCamera(camara);

        // 6) UI
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        chat = new Chat(skin, manejo.getJugador());
        inventario = new Inventario(skin, chat, jugador);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizar lógica del jugador
        manejo.actualizar(delta);

        // Seguir cámara al jugador (coordenadas en píxeles)
        float jugadorX = manejo.getJugador().getPersonajeX();
        float jugadorY = manejo.getJugador().getPersonajeY();
        camara.position.set(jugadorX, jugadorY, 0);

        // Clamp de cámara al tamaño real del mapa en píxeles
        float worldWidth  = MAP_WIDTH  * TILE_SIZE_W * UNIT_SCALE;
        float worldHeight = MAP_HEIGHT * TILE_SIZE_H * UNIT_SCALE;

        float minX = camara.viewportWidth  / 2f;
        float maxX = worldWidth  - camara.viewportWidth  / 2f;
        float minY = camara.viewportHeight / 2f;
        float maxY = worldHeight - camara.viewportHeight / 2f;

        camara.position.x = MathUtils.clamp(camara.position.x, minX, Math.max(minX, maxX));
        camara.position.y = MathUtils.clamp(camara.position.y, minY, Math.max(minY, maxY));
        camara.update();

        // === Render del mapa ===
        mapRenderer.setView(camara);
        mapRenderer.render(); // renderiza todas las capas

        // === Render del personaje y HUD ===
        Render.batch.setProjectionMatrix(camara.combined);
        Render.batch.begin();
        manejo.render(Render.batch); // personaje / entidades
        Render.batch.end();

        // UI
        chat.actualizar(delta);
        chat.render();

        inventario.actualizar(delta);
        inventario.render();

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
    }
}
