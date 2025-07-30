package io.github.some_example_name;
import Controles.controlDelJuego;
import Utilidades.Render;
import pantalla.PantallaCarga;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private controlDelJuego manejo;

    @Override
    public void create() {
        manejo = new controlDelJuego(); // Inicia el administrador del juego
        Render.batch = new SpriteBatch();
       
    }

    @Override
    public void render() {
        Render.limpiarPantalla();
        super.render();


        manejo.actualizar(Gdx.graphics.getDeltaTime());
         manejo.render();
    }

    public void update(){

    }

    @Override
    public void dispose() {
        Render.batch.dispose();
        manejo.dispose();
    }
}





