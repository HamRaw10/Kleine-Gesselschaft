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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import controles.ControlDelJuego;
import entidades.Jugador;
import utilidades.Chat;
import utilidades.Colisiones;
import utilidades.Render;
import utilidades.Inventario;
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

    private OrthographicCamera camara;

    public PantallaJuego(Game juego) {
        // Cámara
        camara = new OrthographicCamera();
        camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camara.position.set(0, 0, 0);
        camara.update();

        Render.batch = new SpriteBatch();
    }

    @Override
    public void show() {
        // 0) cargar tileset y cortar atlas
        tileset = new Texture(Gdx.files.internal("town full/tiles/tiles.png")); // evita espacios en rutas
        tileset.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        final int TILE_W = 48, TILE_H = 48;
        TextureRegion[][] atlas = TextureRegion.split(tileset, TILE_W, TILE_H);

        final int MAP_W = MAP_WIDTH;
        final int MAP_H = MAP_HEIGHT;

        // 1) crear buffer visual y colisiones "en crudo"
        mapa = new TextureRegion[MAP_H][MAP_W];
        int[][] tileTypes = new int[MAP_H][MAP_W];

        // 2) crear el manejador de colisiones con esa matriz
        colisiones = new Colisiones(tileTypes, TILE_W, MAP_W, MAP_H);

        // 3) ahora SÍ, usar helpers de colisión para poblar el mapa
        TextureRegion pasto     = atlas[1][0]; // elige un tile "lleno"
        TextureRegion obstaculo = atlas[0][4]; // algo sólido (roca/valla)

        // piso base (libre)
        for (int y = 0; y < MAP_H; y++) {
            for (int x = 0; x < MAP_W; x++) {
                colisiones.ponerTile(pasto, x, y, false, mapa);
            }
        }

        // ejemplos de objetos:
        // a) uno 1x1 sólido
        colisiones.ponerTile(obstaculo, 20, 15, true, mapa);

        // b) bloque 3x3 sólido (casa)
        int casaRow = 10, casaCol = 5, casaW = 3, casaH = 3;
        int destX = 40, destY = 22;
        colisiones.ponerBloque(atlas, casaRow, casaCol, casaW, casaH, destX, destY, true, mapa);

        // c) bloque 3x3 con máscara (solo base sólida)
    /*
    boolean[][] mask = {
        {false,false,false},
        {false,false,false},
        {true, true, true}
    };
    colisiones.ponerBloqueConMascara(atlas, casaRow, casaCol, casaW, casaH, destX+6, destY, mask, mapa);
    */

        // 4) controlador + cámara + chat (después de tener colisiones)
        manejo = new ControlDelJuego(colisiones);
        manejo.setCamera(camara);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        chat = new Chat(skin, manejo.getJugador());
        inventario = new Inventario(skin, chat, jugador);
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

        Render.batch.setProjectionMatrix(camara.combined);
        Render.batch.begin();

        // Dibujar solo lo visible
        int tilesAncho = (int)Math.ceil(camara.viewportWidth  / TILE_SIZE);
        int tilesAlto  = (int)Math.ceil(camara.viewportHeight / TILE_SIZE);
        int centerTileX = (int)(camara.position.x / TILE_SIZE);
        int centerTileY = (int)(camara.position.y / TILE_SIZE);
        int startX = Math.max(0, centerTileX - tilesAncho/2 - 1);
        int startY = Math.max(0, centerTileY - tilesAlto /2 - 1);
        int endX   = Math.min(MAP_WIDTH,  centerTileX + tilesAncho/2 + 2);
        int endY   = Math.min(MAP_HEIGHT, centerTileY + tilesAlto /2 + 2);

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                TextureRegion tr = mapa[y][x];
                if (tr != null) {
                    Render.batch.draw(tr, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Entidades / UI overlay
        manejo.render(Render.batch);
        Render.batch.end();

        // Chat
        chat.actualizar(delta);
        chat.render();

        // Inventario
        inventario.actualizar(delta);
        inventario.render();

        if (chat.isChatVisible()) {
            chat.setInputProcessor();
        } if (!inventario.isVisible()) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                float x = Gdx.input.getX();
                float y = Gdx.graphics.getHeight() - Gdx.input.getY();
            }
        }else {
            Gdx.input.setInputProcessor(manejo.getInputProcessor());
        }
    }

    @Override
    public void resize(int width, int height) {
        camara.setToOrtho(false, width, height);
        camara.update();
        if (chat != null) chat.resize(width, height);
        if (inventario != null) inventario.resize(width, height); // Nueva línea
    }

    @Override
    public void dispose() {
        if (tileset != null) tileset.dispose();
        if (Render.batch != null) Render.batch.dispose();
        if (manejo != null) manejo.dispose();
        if (chat != null) chat.dispose();
        if (inventario != null) inventario.dispose(); // Nueva línea
    }
}
