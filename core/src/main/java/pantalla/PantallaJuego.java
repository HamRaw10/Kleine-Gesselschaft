package pantalla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.audio.Music;

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
    private Music musicaFondo;

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
        // 1) Cargar mapa y renderer
        mapaTiled = new TmxMapLoader().load("exteriores/centro.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(mapaTiled, UNIT_SCALE);

        // 2) Propiedades del mapa
        MapProperties props = mapaTiled.getProperties();
        MAP_WIDTH   = props.get("width", Integer.class);
        MAP_HEIGHT  = props.get("height", Integer.class);
        TILE_SIZE_W = props.get("tilewidth", Integer.class);
        TILE_SIZE_H = props.get("tileheight", Integer.class);

        // 🔁 3) Cargar colisiones desde la Object Layer "colisiones" (¡ojo nombre!)
        colisiones = new Colisiones();
        colisiones.cargarDesdeMapa(mapaTiled, "colisiones", UNIT_SCALE);


        // 4) Resto igual
        manejo = new ControlDelJuego(colisiones);
        manejo.setCamera(camara);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        chat = new Chat(skin, manejo.getJugador());
        inventario = new Inventario(skin, chat, jugador);
        musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("assets/musica1.mp3"));
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.5f);
        musicaFondo.play();
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
        if (musicaFondo != null) {
            musicaFondo.dispose();
        }

    }
}
