package io.github.some_example_name;
import Controles.controlDelJuego;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private controlDelJuego manejo;

    @Override
    public void create() {
        manejo = new controlDelJuego(); // Inicia el administrador del juego
        Render.batch = new SpriteBatch();
        this.setScreen(new PantallaCarga()); // Establece la pantalla de carga como la primera
    }

    @Override
    public void render() {
        Render.limpiarPantalla();
        super.render();


        manejo.actualizar(Gdx.graphics.getDeltaTime());
        manejo.render();
    }

    @Override
    public void dispose() {
        manejo.dispose();
    }
}





