package Controles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import Entidades.Jugador;
import objetos.Mapa;
import Utilidades.utiles;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class controlDelJuego {
    private Jugador jugador;
    private SpriteBatch batch;
    private Mapa mapa;

    public controlDelJuego(){
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
            float targetX = utiles.getMouseX();
            float targetY = utiles.getMouseY();
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
