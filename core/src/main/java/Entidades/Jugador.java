package Entidades;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import Utilidades.Animacion;
import Utilidades.utiles;

public class Jugador extends Personaje {

    public boolean estaEnMovimiento = false;
    private Animation<TextureRegion> animacionIdle;
    private Animation<TextureRegion> animacionAdelante;
    private Animation<TextureRegion> animacionAtras;
    private Animation<TextureRegion> animacionDerecha;
    private Animation<TextureRegion> animacionIzquierda;
    private float tiempoAnimacion;
    private float velocidadX, velocidadY;
    private Direccion direccionActual = Direccion.ABAJO;

    // Enum para direcciones
    private enum Direccion {
        ARRIBA, ABAJO, DERECHA, IZQUIERDA
    }

    public Jugador() {
        super("char_a_p1/adelante/001.png", 300, 150, 1f);
        this.velocidad = 0.1f;

        // Cargar animaciones para cada dirección
        this.animacionIdle = Animacion.crearAnimacionDesdeCarpeta(
            "char_a_p1/adelante", 5, 0.2f);
        this.animacionAdelante = Animacion.crearAnimacionDesdeCarpeta(
            "char_a_p1/adelante", 6, 0.08f);
        this.animacionAtras = Animacion.crearAnimacionDesdeCarpeta(
            "char_a_p1/atras", 6, 0.08f);
        this.animacionDerecha = Animacion.crearAnimacionDesdeCarpeta(
            "char_a_p1/derecha", 6, 0.08f);
        this.animacionIzquierda = Animacion.crearAnimacionDesdeCarpeta(
            "char_a_p1/izquierda", 6, 0.08f);
    }

    @Override
    public void actualizar(float delta, float targetX, float targetY) {
        float prevX = personajeX;
        float prevY = personajeY;

        if (utiles.Clickear()) {
            velocidadX = (targetX - personajeX) * velocidad * delta * 60;
            velocidadY = (targetY - personajeY) * velocidad * delta * 60;

            personajeX += velocidadX;
            personajeY += velocidadY;

            // Determinar dirección predominante
            if (Math.abs(velocidadX) > Math.abs(velocidadY)) {
                direccionActual = velocidadX > 0 ? Direccion.DERECHA : Direccion.IZQUIERDA;
            } else {
                direccionActual = velocidadY > 0 ? Direccion.ARRIBA : Direccion.ABAJO;
            }
        }

        limitesDelPersonaje();
        tiempoAnimacion += delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame;

        if (!estaEnMovimiento()) {
            frame = animacionIdle.getKeyFrame(tiempoAnimacion, true);
        } else {
            switch (direccionActual) {
                case ARRIBA:
                    frame = animacionAtras.getKeyFrame(tiempoAnimacion, true);
                    break;
                case ABAJO:
                    frame = animacionAdelante.getKeyFrame(tiempoAnimacion, true);
                    break;
                case DERECHA:
                    frame = animacionDerecha.getKeyFrame(tiempoAnimacion, true);
                    break;
                case IZQUIERDA:
                    frame = animacionIzquierda.getKeyFrame(tiempoAnimacion, true);
                    break;
                default:
                    frame = animacionIdle.getKeyFrame(tiempoAnimacion, true);
            }
        }

        batch.draw(
            frame,
            getPersonajeX(),
            getPersonajeY(),
            getWidth(),
            getHeight()
        );
    }

    private boolean estaEnMovimiento() {
        estaEnMovimiento = true;
        return Math.abs(velocidadX) > 0.1f || Math.abs(velocidadY) > 0.1f;
    }
}
