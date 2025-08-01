package pantalla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class PantallaCarga implements Screen {

    private ShapeRenderer shapeRenderer;
    private Game juego;
    private AssetManager assets;
    private float progreso = 0;
    private BitmapFont fuente;
    private SpriteBatch batch;

    public PantallaCarga(Game juego, AssetManager assets) {
        shapeRenderer = new ShapeRenderer();
        this.juego = juego;
        this.assets = assets;
        batch = new SpriteBatch();
        fuente = new BitmapFont();

        // Aca se van a poner los recursos a cargar

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (assets.update()) {
            // 100% cargado
            juego.setScreen(new PantallaJuego(juego));
        } else {
            progreso = assets.getProgress(); // valor de 0.0 a 1.0

            // 1. Dibujar la barra de carga PRIMERO (ShapeRenderer)
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            // Configura la matriz de proyección (igual que el viewport de SpriteBatch)
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

            // Fondo de la barra (gris)
            shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
            shapeRenderer.rect(50, 50, 400, 30);

            // Relleno (azul)
            shapeRenderer.setColor(0, 0.5f, 1f, 1);
            shapeRenderer.rect(50, 50, 400 * progreso, 30);
            shapeRenderer.end();

            // 2. Dibujar el texto DESPUÉS (SpriteBatch)
            batch.begin();
            fuente.setColor(1, 1, 1, 1); // Color blanco
            fuente.draw(batch, "Cargando... " + (int)(progreso * 100) + "%", 100, 200);
            batch.end();
        }
    }

    @Override public void resize(int w, int h) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {
        batch.dispose();
        fuente.dispose();
    }
}

