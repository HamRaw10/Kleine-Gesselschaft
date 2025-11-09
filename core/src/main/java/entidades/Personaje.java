package entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;


/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */

public abstract class Personaje {
    protected float personajeX;
    protected float personajeY;
    protected float velocidad;//Variable creada, la cual va a almacenar la velocidad en la que se mueva el ciruclo
    protected Texture texture;
    protected float escala = 1f;
    protected float ancho;
    protected float largo;
    protected float velocidadX;
    protected float velocidadY;

    // NUEVO: Hitbox para colisiones (más pequeño que el sprite para precisión)
    protected Rectangle hitbox;

    public Personaje(String texturaDelPersonaje, float personajeX, float personajeY, float escala) {
        this.texture = new Texture(texturaDelPersonaje);
        this.personajeY = personajeY;
        this.personajeX = personajeX;
        this.escala = escala;

        // NUEVO: Inicializar hitbox (ej. 80% del tamaño del sprite)
        float hitboxWidth = getWidth() * 0.8f;
        float hitboxHeight = getHeight() * 0.8f;
        hitbox = new Rectangle(
            personajeX - hitboxWidth / 2,
            personajeY - hitboxHeight / 2,
            hitboxWidth,
            hitboxHeight
        );
    }

    public void setEscala(float escala) {
        this.escala = escala;
    }

    protected void actualizarPosicion(float newX, float newY) {
        this.personajeX = newX;
        this.personajeY = newY;
        // Actualizar hitbox
        float halfWidth = hitbox.width / 2;
        float halfHeight = hitbox.height / 2;
        hitbox.setPosition(personajeX - halfWidth, personajeY - halfHeight);
    }


    public void limitesDelPersonaje() {
        float minX = texture.getWidth() / 2f;//
        float maxX = Gdx.graphics.getWidth() - minX;//
        float minY = texture.getHeight() / 2f;//
        float maxY = Gdx.graphics.getHeight() - minY;//
        personajeX = MathUtils.clamp(personajeX, minX, maxX);//
        personajeY = MathUtils.clamp(personajeY, minY, maxY);//
    }

    public void Colisiones(List<Rectangle> obstaculos, float ANCHO_MAPA, float ALTO_MAPA) {
        // limitar al borde izquierdo
        if (personajeX < 0) {
            personajeX = 0;
        }

        // Limitar al borde derecho
        if (personajeX + getWidth() > ANCHO_MAPA) {
            personajeX = ANCHO_MAPA - getWidth();
        }

        // Limitar al borde inferior
        if (personajeY < 0) {
            personajeY = 0;
        }

        // lmitar al borde superior
        if (personajeY + getHeight() > ALTO_MAPA) {
            personajeY = ALTO_MAPA - getHeight();
        }

        // crear el rectangulo del jugador con su nueva posicion tentativa
        Rectangle rectJugador = new Rectangle(personajeX, personajeY, getWidth(), getHeight());

        // verificar colisiones con obstaculos
        for (Rectangle obstaculo : obstaculos) {
            if (rectJugador.overlaps(obstaculo)) {
                // rvertir posicion si hay colison
                personajeX -= velocidadX;
                personajeY -= velocidadY;
                break;
            }
        }
    }

    public void actualizar(float delta, float targetX, float targetY) {

    }

    public Texture getTexture() {
        return texture;
    }

    public float getPersonajeX() {
        return personajeX - (texture.getWidth() * escala / 2f);
    }

    public float getPersonajeY() {
        return personajeY - (texture.getHeight() * escala / 2f);
    }

    public float getWidth() {
        return texture.getWidth() * escala;
    }

    public float getHeight() {
        return texture.getHeight() * escala;
    }

    public void dispose() {
        texture.dispose();
    }

    public abstract void render(SpriteBatch batch);
}
