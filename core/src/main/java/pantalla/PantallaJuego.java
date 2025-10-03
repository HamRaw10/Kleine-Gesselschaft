package pantalla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import controles.ControlDelJuego;
import utilidades.Colisiones;
import utilidades.Render;
import utilidades.Chat;

public class PantallaJuego extends ScreenAdapter {
    private ControlDelJuego manejo;
    private Chat chat;

    private Texture tileset;
    private TextureRegion[][] tiles;

    private final int TILE_SIZE = 16;

    // Mapa (120 x 80 tiles)
    private final int MAP_WIDTH = 240;
    private final int MAP_HEIGHT = 160;
    private TextureRegion[][] mapa; // cada tile del mundo

    private OrthographicCamera camara;
    private Colisiones colisiones;

    public PantallaJuego(Game juego) {
        Gdx.input.setInputProcessor(null);
        manejo = new ControlDelJuego();
        Render.batch = new SpriteBatch();

        // Crear Chat
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        chat = new Chat(skin, manejo.getJugador());

        // Cámara
        camara = new OrthographicCamera();
        camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camara.position.set(0, 0, 0);  // Empieza en (0,0), pero se actualizará con el jugador
        camara.update();
    }

    public void show() {
        tileset = new Texture(Gdx.files.internal("town full/tiles/tiles.png"));
        tiles = TextureRegion.split(tileset, TILE_SIZE, TILE_SIZE);

        // Crear mapa "aleatorio" de tiles
        mapa = new TextureRegion[MAP_HEIGHT][MAP_WIDTH];

        // NUEVO: Matriz para colisiones (0 = libre, 1 = colisionable)
        int[][] tileTypes = new int[MAP_HEIGHT][MAP_WIDTH];  // <-- Esta la pasarás a Colisiones


        TextureRegion pasto = tiles[1][1];
        TextureRegion piedra = tiles[3][4];

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (Math.random() < 0.1) {
                    mapa[y][x] = piedra; // 10% rocas
                    tileTypes[y][x] = 1;  // Colisionable
                } else {
                    mapa[y][x] = pasto; // suelo normal
                    tileTypes[y][x] = 0;  // Libre
                }
            }
        }
        colisiones = new Colisiones(tileTypes, TILE_SIZE, MAP_WIDTH, MAP_HEIGHT);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();
        Gdx.gl.glClearColor(0, 0, 0, 1);  // Fondo negro o el color que quieras
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        manejo.actualizar(delta);
        // --- Actualizar cámara para seguir al jugador ---
        float jugadorX = manejo.getJugador().getPersonajeX() + (TILE_SIZE / 2);
        float jugadorY = manejo.getJugador().getPersonajeY() + (TILE_SIZE / 2);

        camara.position.set(jugadorX, jugadorY, 0);

        // NUEVO: Clamp la cámara para NO SALIR del mapa (evita el rectángulo negro arriba/abajo/izq/der)
        // viewportWidth/2 es la mitad del ancho de la pantalla (para que la cámara no muestre bordes vacíos)
        float minX = camara.viewportWidth / 2f;
        float maxX = (MAP_WIDTH * TILE_SIZE) - camara.viewportWidth / 2f;
        float minY = camara.viewportHeight / 2f;
        float maxY = (MAP_HEIGHT * TILE_SIZE) - camara.viewportHeight / 2f;
        camara.position.x = MathUtils.clamp(camara.position.x, minX, maxX);
        camara.position.y = MathUtils.clamp(camara.position.y, minY, maxY);

        camara.update();

        Render.batch.setProjectionMatrix(camara.combined);

        Render.batch.begin();

        // Dibujar tiles dentro del viewport
        int startX = (int) (camara.position.x / TILE_SIZE) - (Gdx.graphics.getWidth() / (2 * TILE_SIZE)) - 1;
        int startY = (int) (camara.position.y / TILE_SIZE) - (Gdx.graphics.getHeight() / (2 * TILE_SIZE)) - 1;
        int endX = (int) ((camara.position.x + Gdx.graphics.getWidth()) / TILE_SIZE) + 1;
        int endY = (int) ((camara.position.y + Gdx.graphics.getHeight()) / TILE_SIZE) + 1;

        // Limitar a los bordes del mapa
        startX = Math.max(0, startX);
        startY = Math.max(0, startY);
        endX = Math.min(MAP_WIDTH, endX);
        endY = Math.min(MAP_HEIGHT, endY);

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                if (mapa[y][x] != null) {
                    Render.batch.draw(mapa[y][x], x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }


        manejo.render(Render.batch);

        // Dibujar jugador
        Render.batch.end();


        // Actualizar lógica del juego


        // Chat
        chat.actualizar(delta);
        chat.render();

        // Input: chat vs juego
        if (chat.isChatVisible()) {
            chat.setInputProcessor();
        } else {
            Gdx.input.setInputProcessor(manejo.getInputProcessor());
        }
    }

    @Override
    public void resize(int width, int height) {
        camara.setToOrtho(false, width, height);
        camara.update();
        chat.resize(width, height);
    }

    @Override
    public void dispose() {
        if (tileset != null) tileset.dispose();
        if (Render.batch != null) Render.batch.dispose();
        if (manejo != null) manejo.dispose();
        if (chat != null) chat.dispose();
    }
}
