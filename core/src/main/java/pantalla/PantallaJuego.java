package pantalla;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import Controles.controlDelJuego;
import Utilidades.Render;

public class PantallaJuego extends ScreenAdapter {
    private controlDelJuego manejo;

    public PantallaJuego(Game juego) {
        Gdx.input.setInputProcessor(null);
        manejo = new controlDelJuego();
        Render.batch = new SpriteBatch(); // solo se inicializa en esta pantalla
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();
        manejo.actualizar(delta);
        manejo.render();
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        Render.batch.dispose();
        manejo.dispose();
    }
}
