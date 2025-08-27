package controles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import entidades.Jugador;
import objetos.Mapa;
import objetos.Fondo;
import utilidades.Utiles;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ControlDelJuego {
    private Jugador jugador;
    private SpriteBatch batch;
    private Mapa mapa;
    private Fondo fondo;

    public ControlDelJuego(){
        batch = new SpriteBatch();
        jugador = new Jugador();
        mapa = new Mapa();
        fondo = new Fondo("/fondoPrueba.jpg");
    }

    public void actualizar(float delta){
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();



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
        jugador.estaEnMovimiento = false;


    }
    public void render() {

        batch.begin();

        fondo.render(batch);
        jugador.render(batch);
        mapa.render(batch);
        batch.end();
    }

    public void dispose() {
        batch.dispose();
        jugador.dispose();
        mapa.dispose();
        fondo.dispose();
    }
}
