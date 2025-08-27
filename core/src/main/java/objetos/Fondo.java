package objetos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class Fondo implements Disposable {
    private Texture texturaFondo;

    public Fondo(String rutaImagen) {

        try {
            texturaFondo = new Texture(Gdx.files.internal(rutaImagen.startsWith("/") ? rutaImagen.substring(1) : rutaImagen));
        } catch (Exception e) {
            Gdx.app.error("Fondo", "Error al cargar la textura del fondo: " + rutaImagen, e);
        }
    }

    public void render(SpriteBatch batch) {
        if (texturaFondo != null) {
            batch.draw(texturaFondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    @Override
    public void dispose() {
        if (texturaFondo != null) {
            texturaFondo.dispose();
        }
    }
}
