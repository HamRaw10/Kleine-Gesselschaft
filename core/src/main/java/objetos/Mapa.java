package objetos;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.awt.Rectangle;

public class Mapa extends Objeto {
    private final Texture mapaCompleto;
    private boolean expandido;
    private final Rectangle bounds;
    private float tiempoExpandido;
    private final float TIEMPO_TRANSICION = 0.3f;
    public Mapa() {
        super("mapa.png", Gdx.graphics.getWidth() - 110, 10, 0.5f);
        this.mapaCompleto = new Texture("MapaGrande.jpg");
        this.bounds = new Rectangle((int) getMapaX(), (int) getMapaY(), (int) getWidth(), (int) getHeight());
        this.expandido = false;
    }

    @Override
    public void actualizar(float delta, float targetX, float targetY) {
        bounds.setRect(getMapaX(), getMapaY(), getWidth(), getHeight());

        // Control de entrada (clic o tecla M)
        if ((Gdx.input.justTouched() && bounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()))){
            toggleExpandido();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            toggleExpandido();
        }

        // Actualizar tiempo de transición
        if (expandido && tiempoExpandido < TIEMPO_TRANSICION) {
            tiempoExpandido += delta;
        } else if (!expandido && tiempoExpandido > 0) {
            tiempoExpandido -= delta;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (expandido || tiempoExpandido > 0) {
            // Calcular progreso de la animación (0 a 1)
            float progreso = expandido ?
                Math.min(1, tiempoExpandido/TIEMPO_TRANSICION) :
                Math.max(0, 1 - (tiempoExpandido/TIEMPO_TRANSICION));

            // Dibujar fondo semitransparente
            batch.setColor(1, 1, 1, progreso * 0.7f);
            batch.draw(mapaCompleto,
                0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

            // Restaurar color
            batch.setColor(1, 1, 1, 1);
        }

        // Dibujar mini mapa (con transición de opacidad si está expandiéndose)
        if (!expandido || tiempoExpandido < TIEMPO_TRANSICION) {
            float opacidad = expandido ? 1 - (tiempoExpandido/TIEMPO_TRANSICION) : 1;
            batch.setColor(1, 1, 1, opacidad);
            batch.draw(texture, getMapaX(), getMapaY(), getWidth(), getHeight());
            batch.setColor(1, 1, 1, 1);
        }
    }

    public void toggleExpandido() {
        expandido = !expandido;
    }

    public boolean isExpandido() {
        return expandido;
    }

    @Override
    public void dispose() {
        super.dispose();
        mapaCompleto.dispose();
    }


}
