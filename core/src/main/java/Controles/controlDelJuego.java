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
        float targetX = utiles.getMouseX();
        float targetY = utiles.getMouseY();
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        jugador.actualizar(delta, targetX, targetY);


        // Actualizar el mapa (siempre)
        mapa.actualizar(delta, mouseX, mouseY);

        // Solo actualizar jugador si el mapa no está expandido
        if (!mapa.isExpandido()) {
            jugador.actualizar(delta, mouseX, mouseY);
        } else {
            // Opcional: Resetear velocidad cuando el mapa está abierto
            jugador.estaEnMovimiento = false;
        }
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
