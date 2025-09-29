package controles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import entidades.Jugador;
import objetos.Mapa;
import utilidades.Utiles;
import utilidades.Chat;



/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ControlDelJuego {
    private Jugador jugador;
    private SpriteBatch batch;
    private Mapa mapa;

    public ControlDelJuego(){
        batch = new SpriteBatch();
        jugador = new Jugador();
        mapa = new Mapa();
    }

    public void actualizar(float delta){
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();


        // Actualizar el mapa (siempre)
        mapa.actualizar(delta, mouseX, mouseY);

        if (!mapa.isExpandido() && !mapa.isInputBloqueado()) {
            float targetX = Utiles.getMouseX();
            float targetY = Utiles.getMouseY();
            jugador.actualizar(delta, targetX, targetY);
        } else {
            detenerMovimientoJugador();
        }

    }


    private void detenerMovimientoJugador() {
        // Implementa según tu clase Jugador
        jugador.estaEnMovimiento = false;

        // Si tu jugador tiene velocidad, también podrías resetearla:
        // jugador.setVelocidad(0, 0);
    }

    // ✅ Getter para integrar con Chat
    public Jugador getJugador() {
        return jugador;
    }

    // ✅ Si usás inputProcessor propio
    public InputProcessor getInputProcessor() {
        // devolver el stage de UI, o null si no tenés
        return null;
    }

    public void render() {
        batch.begin();
        jugador.render(batch);
        mapa.render(batch);
        batch.end();
    }

    public void dispose() {
        batch.dispose();
        jugador.dispose();
        mapa.dispose();
    }



}
