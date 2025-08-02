package utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation; // ¡Miau! Falta este import importante

public class Animacion {
    public static Animation<TextureRegion> crearAnimacion(String rutaCarpeta, int totalFrames, float duracion) {
        TextureRegion[] frames = new TextureRegion[totalFrames];

        for (int i = 0; i < totalFrames; i++) {
            String rutaCompleta = String.format("%s/char_a_p1_%d.png", rutaCarpeta, (i+1));
            Texture texture = new Texture(Gdx.files.internal(rutaCompleta));
            frames[i] = new TextureRegion(texture);
        }
        return new Animation<TextureRegion>(duracion, frames);
    }

    public static Animation<TextureRegion> crearAnimacionDesdeCarpeta(
        String rutaCarpeta, int totalFrames, float duracionFrame) {

        TextureRegion[] frames = new TextureRegion[totalFrames];
        for (int i = 0; i < totalFrames; i++) {
            String numeroFrame = String.format("%03d", i+1); // 001, 002, etc.
            String rutaCompleta = rutaCarpeta + "/" + numeroFrame + ".png";
            frames[i] = new TextureRegion(new Texture(Gdx.files.internal(rutaCompleta)));
        }
        return new Animation<>(duracionFrame, frames);
    }
}
