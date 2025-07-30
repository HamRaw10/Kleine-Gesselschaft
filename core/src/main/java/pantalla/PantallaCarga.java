package pantalla;

import com.badlogic.gdx.Screen;

import Utilidades.Recursos;
import Utilidades.Render;
import objetos.Imagen;

public class PantallaCarga implements Screen {

    Imagen fondo;
    @Override
    public void show() {
        fondo = new Imagen(Recursos.fondos);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();
        Render.batch.begin();
                fondo.dibujar(); //aun faltaria la imagen de la pantalla de carga
        Render.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
