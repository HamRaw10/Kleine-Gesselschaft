package entidades;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import utilidades.Animacion;
import utilidades.Colisiones;
import utilidades.Moneda;   // ⬅️ NUEVO

public class Jugador extends Personaje {

    private final Colisiones colisiones;

    // Hitbox de “pies”: ajustá a tu sprite real
    private final float hitW = 20f, hitH = 14f;
    private final float hitOffsetX; // (spriteWidth - hitW)/2
    private final float hitOffsetY; // cuanto subís/bajás el rect respecto del origen del sprite

    private final Rectangle hitbox;

    private float velPx = 160f;
    public boolean estaEnMovimiento = false;
    private boolean bloqueado = false;

    private Animation<TextureRegion> animacionIdle;
    private Animation<TextureRegion> animacionAdelante;
    private Animation<TextureRegion> animacionAtras;
    private Animation<TextureRegion> animacionDerecha;
    private Animation<TextureRegion> animacionIzquierda;
    private float tiempoAnimacion;
    private float velocidadX, velocidadY;

    private Direccion direccionActual = Direccion.ABAJO;

    // ⬇️ NUEVO: dinero del jugador
    private final Moneda dinero;

    private enum Direccion {ARRIBA, ABAJO, DERECHA, IZQUIERDA}

    // Constructor clásico -> inicia con 0 monedas
    public Jugador(Colisiones colisiones) {
        this(colisiones, 0);
    }

    // ⬇️ NUEVO: constructor con monedas iniciales
    public Jugador(Colisiones colisiones, int monedasIniciales) {
        super("personaje/adelante/001.png", 300, 150, 1f);
        this.colisiones = colisiones;
        this.dinero = new Moneda(monedasIniciales);

        // Si tu Personaje tiene getWidth()/getHeight(), usalos:
        float spriteW = getWidth();
        float spriteH = getHeight();

        // Hitbox centrada horizontal, apoyada abajo (ajustá hitOffsetY si hace falta)
        this.hitOffsetX = (spriteW - hitW) * 0.5f;
        this.hitOffsetY = 0f;

        this.hitbox = new Rectangle(personajeX + hitOffsetX, personajeY + hitOffsetY, hitW, hitH);

        this.animacionIdle = Animacion.crearAnimacionDesdeCarpeta("personaje/adelante", 5, 0.2f);
        this.animacionAdelante = Animacion.crearAnimacionDesdeCarpeta("personaje/adelante", 6, 0.08f);
        this.animacionAtras = Animacion.crearAnimacionDesdeCarpeta("personaje/atras", 6, 0.08f);
        this.animacionDerecha = Animacion.crearAnimacionDesdeCarpeta("personaje/derecha", 6, 0.08f);
        this.animacionIzquierda = Animacion.crearAnimacionDesdeCarpeta("personaje/izquierda", 6, 0.08f);
    }

    // === DINERO (helpers) ===
    public Moneda getDinero() {
        return dinero;
    }

    public void ganarMonedas(int cant) {
        dinero.sumar(cant);
    }

    public boolean gastarMonedas(int costo) {
        return dinero.restar(costo);
    }

    // === HITBOX ===
    public Rectangle getHitbox() {
        return hitbox;
    }

    private void syncHitbox() {
        hitbox.setPosition(personajeX + hitOffsetX, personajeY + hitOffsetY);
    }

    public void setPos(float x, float y) {
        this.personajeX = x;
        this.personajeY = y;
        syncHitbox();
    }

    // === MOVIMIENTO con resolución por ejes ===
    private void moverConColision(float dx, float dy) {
        if (dx != 0f) {
            float oldX = personajeX;
            personajeX += dx;
            syncHitbox();
            if (colisiones.colisiona(hitbox)) {
                personajeX = oldX;
                syncHitbox();
                dx = 0f;
            }
        }
        if (dy != 0f) {
            float oldY = personajeY;
            personajeY += dy;
            syncHitbox();
            if (colisiones.colisiona(hitbox)) {
                personajeY = oldY;
                syncHitbox();
                dy = 0f;
            }
        }
        velocidadX = dx;
        velocidadY = dy;
    }

    public float getVelPx() {
        return velPx;
    }

    public void setVelPx(float v) {
        velPx = v;
    }

    public void cancelarMovimiento() {
        velocidadX = 0f;
        velocidadY = 0f;
        estaEnMovimiento = false;
    }

    @Override
    public void actualizar(float delta, float targetX, float targetY) {
        if (bloqueado) {
            velocidadX = 0;
            velocidadY = 0;
            return;
        }

        float cx = personajeX + getWidth() * 0.5f;
        float cy = personajeY + getHeight() * 0.3f;

        float dx = targetX - cx;
        float dy = targetY - cy;
        float len = (float) Math.sqrt(dx * dx + dy * dy);

        float movX = 0, movY = 0;
        if (len > 1f) {
            dx /= len;
            dy /= len;
            movX = dx * velPx * delta;
            movY = dy * velPx * delta;

            if (Math.abs(movX) > Math.abs(movY)) {
                direccionActual = movX > 0 ? Direccion.DERECHA : Direccion.IZQUIERDA;
            } else {
                direccionActual = movY > 0 ? Direccion.ARRIBA : Direccion.ABAJO;
            }
        }

        moverConColision(movX, movY);

        estaEnMovimiento = Math.abs(velocidadX) > 0.001f || Math.abs(velocidadY) > 0.001f;
        tiempoAnimacion += delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame;
        if (!estaEnMovimiento) {
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
        float w = getWidth() * escala;
        float h = getHeight() * escala;
        batch.draw(frame, getPersonajeX(), getPersonajeY(), w, h);
    }


    public void setBloqueado(boolean b) {
        bloqueado = b;
        velocidadX = 0;
        velocidadY = 0;
    }
}
