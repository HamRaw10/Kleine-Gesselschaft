package Controles;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import Entidades.Jugador;
import Utilidades.utiles;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class controlDelJuego {
    private Jugador jugador;
    private SpriteBatch batch;

    public controlDelJuego(){
        batch = new SpriteBatch();
        jugador = new Jugador();
    }

    public void actualizar(float delta){
        float targetX = utiles.getMouseX();
        float targetY = utiles.getMouseY();

        jugador.actualizar(delta, targetX, targetY);
    }

    public void render() {
        batch.begin();
        jugador.render(batch);
        batch.end();
    }

    public void dispose() {
        batch.dispose();
        jugador.dispose();
    }



}
