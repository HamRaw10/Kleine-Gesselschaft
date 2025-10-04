package controles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import entidades.Jugador;
import objetos.Mapa;
import utilidades.Colisiones;
import utilidades.Utiles;

/** Lógica principal de juego / puente entre input y entidades. */
public class ControlDelJuego {
    private Jugador jugador;
    private Mapa mapa;
    private Colisiones colisiones;
    private com.badlogic.gdx.graphics.OrthographicCamera cam;
    private float destinoX, destinoY;


    /** NUEVO: recibe colisiones y crea Jugador con ellas */
    public ControlDelJuego(Colisiones colisiones) {
        this.jugador = new Jugador(colisiones);
        this.colisiones = colisiones;
        this.mapa = new Mapa();
    }



    public void actualizar(float delta) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        mapa.actualizar(delta, mouseX, mouseY);

        if (!mapa.isExpandido() && !mapa.isInputBloqueado()) {
            // solo al hacer click actualizamos destino
            if (Gdx.input.justTouched()) {
                com.badlogic.gdx.math.Vector3 w = cam.unproject(new com.badlogic.gdx.math.Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                destinoX = w.x; destinoY = w.y;
            }
            jugador.actualizar(delta, destinoX, destinoY);
        } else {
            detenerMovimientoJugador();
        }
    }


    private void detenerMovimientoJugador() {
        // Deja de animar como moviéndose
        jugador.estaEnMovimiento = false;
        // Si agregas setters de velocidad en Jugador, ponlos aquí a 0.
    }

    // Getter usado por Chat y otros
    public entidades.Jugador getJugador() {
        return jugador;
    }

    public InputProcessor getInputProcessor() {
        // Devuelve tu Stage/UI si tienes. Por ahora null.
        return null;
    }

    /** Usa el SpriteBatch que te pasa PantallaJuego. No crees uno propio aquí. */
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        jugador.render(batch);
    }

    public void dispose() {
        // No disposes 'batch' aquí porque no lo creamos en esta clase
        jugador.dispose();
        mapa.dispose();
    }

    public void setCamera(com.badlogic.gdx.graphics.OrthographicCamera cam) {
        this.cam = cam;
    }

}
