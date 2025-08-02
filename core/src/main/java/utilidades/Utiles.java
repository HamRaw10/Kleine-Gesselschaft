package utilidades;

import com.badlogic.gdx.Gdx;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class Utiles {

    public static boolean Clickear(){
        return Gdx.input.isTouched();
    }

    public static float getMouseX() {
        return Gdx.input.getX();
    }

    public static float getMouseY() {
        return Gdx.graphics.getHeight() - Gdx.input.getY();
    }
}
