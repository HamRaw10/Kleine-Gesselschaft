package controles;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

import entidades.Jugador;
import objetos.Mapa;
import utilidades.Colisiones;

/** Lógica principal de juego / puente entre input y entidades. */


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import entidades.Jugador;
import objetos.Mapa;
import utilidades.Colisiones;

/**
 * Lógica principal del juego / puente entre input y entidades.
 * Se apoya en un Viewport para unproject del mouse y en una cámara externa.
 */
public class ControlDelJuego {

    // --- Dependencias núcleo ---
    private Jugador jugador;
    private Mapa mapa;
    private Colisiones colisiones;

    // --- Render / proyección ---
    private OrthographicCamera cam;
    private Viewport viewport;

    // --- Estado de movimiento destino ---
    private float destinoX;
    private float destinoY;

    /** Recibe colisiones y crea Jugador/Mapa con dichas colisiones. */
    public ControlDelJuego(Colisiones colisiones) {
        this.colisiones = colisiones;
        this.jugador = new Jugador(colisiones);
        this.mapa = new Mapa();

        // Inicialmente, el destino es la posición actual del jugador (si aplica)
        this.destinoX = jugador.getPersonajeX();
        this.destinoY = jugador.getPersonajeY();
    }

    /** Actualiza la lógica de juego por frame. */
    public void actualizar(float delta) {
        // Posición del mouse en pantalla (pixels, origen arriba-izquierda)
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();

        // Si tu clase Mapa espera coords de pantalla, mantenelo;
        // si espera mundo, pasale unproject también.
        mapa.actualizar(delta, mouseX, Gdx.graphics.getHeight() - mouseY);

        // Si el mapa no está expandido y no bloquea input, procesamos clicks.
        if (!mapa.isExpandido() && !mapa.isInputBloqueado()) {
            if (Gdx.input.justTouched()) {
                // Convertir de pantalla -> mundo usando el Viewport (preferido)
                Vector3 w = new Vector3(mouseX, mouseY, 0f);
                if (viewport != null) {
                    viewport.unproject(w);
                } else if (cam != null) {
                    // Fallback si no se setea el viewport (no recomendado)
                    cam.unproject(w);
                }
                destinoX = w.x;
                destinoY = w.y;
            }

            // Mover/animar al jugador hacia el destino
            jugador.actualizar(delta, destinoX, destinoY);
        } else {
            detenerMovimientoJugador();
        }
    }

    /** Detiene la animación de movimiento del jugador. */
    private void detenerMovimientoJugador() {
        // Si tu Jugador tiene una bandera pública de movimiento, la apagamos.
        // (Esto asume que 'estaEnMovimiento' existe en Jugador como en tu código previo.)
        try {
            jugador.estaEnMovimiento = false;
        } catch (Exception ignored) {
            // Si no existe, simplemente ignoramos.
        }
        // Si luego agregás setters de velocidad en Jugador, ponelos a 0 aquí.
    }

    // --- Getters/Setters y puente hacia otras capas ---

    /** Jugador para que Pantalla/otros accedan a su estado. */
    public Jugador getJugador() {
        return jugador;
    }

    /** InputProcessor principal (si usás Stage en otro lado, retornalo aquí). */
    public InputProcessor getInputProcessor() {
        // Por ahora null; si tenés un Stage para UI o similar, devolvelo aquí.
        return null;
    }

    /** Dibuja entidades con el SpriteBatch proporcionado por la pantalla. */
    public void render(SpriteBatch batch) {
        jugador.render(batch);
    }

    /** Liberar recursos propios. */
    public void dispose() {
        jugador.dispose();
        mapa.dispose();
        // colisiones típicamente pertenece al mapa/pantalla; no se hace dispose aquí
    }

    /** Cámara que usa la pantalla; útil como respaldo si no hay viewport. */
    public void setCamera(OrthographicCamera cam) {
        this.cam = cam;
    }

    /** Viewport para hacer unproject correcto del input. */
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    /** Permite actualizar las colisiones si se cambia de mapa. */
    public void setColisiones(Colisiones nuevasColisiones) {
        this.colisiones = nuevasColisiones;
        // Si Jugador depende de Colisiones, actualizalo también:
        // jugador.setColisiones(nuevasColisiones); // si tu clase lo expone
        Gdx.app.log("ControlDelJuego", "Colisiones actualizadas para nuevo mapa.");
    }
}

