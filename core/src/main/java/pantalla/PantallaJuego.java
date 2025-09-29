package pantalla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import controles.ControlDelJuego;
import utilidades.Render;
import utilidades.Chat;

public class PantallaJuego extends ScreenAdapter {
    private ControlDelJuego manejo;
    private Chat chat;

    private Texture tileset;
    private TextureRegion[][] tiles;

    private final int TILE_SIZE = 16;
    private TextureRegion pasto;
    public PantallaJuego(Game juego) {
        Gdx.input.setInputProcessor(null);
        manejo = new ControlDelJuego();
        Render.batch = new SpriteBatch();

        // Crear Chat, pasando el jugador desde ControlDelJuego
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        chat = new Chat(skin, manejo.getJugador());
    }

    public void show() {
        tileset = new Texture(Gdx.files.internal("town full/tiles/tiles.png"));
        tiles = TextureRegion.split(tileset, TILE_SIZE, TILE_SIZE);

        pasto = tiles[1][1];
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();

        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        Render.batch.begin();
        for (int x = 0; x < screenWidth; x += TILE_SIZE) {
            for (int y = 0; y < screenHeight; y += TILE_SIZE) {
                Render.batch.draw(pasto, x, y, TILE_SIZE, TILE_SIZE);
            }
        }

        Render.batch.end();

        // Actualizar lógica del juego
        manejo.actualizar(delta);
        manejo.render();

        // Actualizar chat
        chat.actualizar(delta);


        // Renderizar chat (globos y barra de texto)
        chat.render();

        // Manejo de input
        if (chat.isChatVisible()) {
            chat.setInputProcessor();
        } else {
            // Restaurar input del juego
            Gdx.input.setInputProcessor(manejo.getInputProcessor());
        }


    }

    @Override
    public void resize(int width, int height) {
        chat.resize(width, height);
    }

    @Override
    public void dispose() {
        tileset.dispose();
        Gdx.input.setInputProcessor(null);
        Render.batch.dispose();
        manejo.dispose();
        chat.dispose();
    }
}
