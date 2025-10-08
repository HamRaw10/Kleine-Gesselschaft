package entidades;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import utilidades.Animacion;
import utilidades.Colisiones;

public class Jugador extends Personaje {

    // NUEVO: referencia a colisiones y hitbox
    private Colisiones colisiones;
    // En Jugador
    private boolean bloqueado = false;
    public Personaje personaje;
    private float hitW = 20f, hitH = 14f; // hitbox “pies” (ajustá a tu sprite)
    private float velPx = 160f;
    public boolean estaEnMovimiento = false;
    private Animation<TextureRegion> animacionIdle;
    private Animation<TextureRegion> animacionAdelante;
    private Animation<TextureRegion> animacionAtras;
    private Animation<TextureRegion> animacionDerecha;
    private Animation<TextureRegion> animacionIzquierda;
    private float tiempoAnimacion;
    private float velocidadX, velocidadY;

    private static final float PASO = 4f;
    private Direccion direccionActual = Direccion.ABAJO;


    // Enum para direcciones
    private enum Direccion {
        ARRIBA, ABAJO, DERECHA, IZQUIERDA
    }

    public Jugador(Colisiones colisiones) {
        super("char_a_p1/adelante/001.png", 300, 150, 1f);
        this.colisiones = colisiones;
        this.velocidad = 0.1f;

        this.animacionIdle      = Animacion.crearAnimacionDesdeCarpeta("char_a_p1/adelante", 5, 0.2f);
        this.animacionAdelante  = Animacion.crearAnimacionDesdeCarpeta("char_a_p1/adelante", 6, 0.08f);
        this.animacionAtras     = Animacion.crearAnimacionDesdeCarpeta("char_a_p1/atras",    6, 0.08f);
        this.animacionDerecha   = Animacion.crearAnimacionDesdeCarpeta("char_a_p1/derecha",  6, 0.08f);
        this.animacionIzquierda = Animacion.crearAnimacionDesdeCarpeta("char_a_p1/izquierda",6, 0.08f);
    }

    private com.badlogic.gdx.math.Rectangle getHitbox() {
        float offX = (getWidth() - hitW) * 0.5f;
        float offY = 0f; // si tu sprite tiene “sombra” abajo y querés subirla, ajustá esto
        return new com.badlogic.gdx.math.Rectangle(personajeX + offX, personajeY + offY, hitW, hitH);
    }





    private void moverConColision(float dx, float dy) {
        // X por micro-pasos
        float restX = dx;
        while (Math.abs(restX) > 0f) {
            float step = Math.abs(restX) > PASO ? Math.signum(restX) * PASO : restX;
            com.badlogic.gdx.math.Rectangle hb = getHitbox();
            float nextX = hb.x + step;
            if (!colisiones.colisionaRect(nextX, hb.y, hb.width, hb.height)) {
                personajeX += step;
                restX -= step;
            } else break;
        }
        // Y por micro-pasos
        float restY = dy;
        while (Math.abs(restY) > 0f) {
            float step = Math.abs(restY) > PASO ? Math.signum(restY) * PASO : restY;
            com.badlogic.gdx.math.Rectangle hb = getHitbox();
            float nextY = hb.y + step;
            if (!colisiones.colisionaRect(hb.x, nextY, hb.width, hb.height)) {
                personajeY += step;
                restY -= step;
            } else break;
        }
    }

    private void moverEjeX(float dx) {
        if (dx == 0) return;
        float tile = colisiones.getTILE_SIZE();
        float offX = (getWidth() - hitW) * 0.5f;

        com.badlogic.gdx.math.Rectangle hb = getHitbox();

        if (dx > 0) {
            // destino del borde derecho de la hitbox
            float destRight = hb.x + hb.width + dx;
            int tileRight = (int) Math.floor((destRight - 0.001f) / tile);

            // chequear filas que ocupa la hitbox
            int tileY1 = (int) Math.floor(hb.y / tile);
            int tileY2 = (int) Math.floor((hb.y + hb.height - 0.001f) / tile);

            boolean bloquea = false;
            for (int ty = tileY1; ty <= tileY2; ty++) {
                if (colisiones.esTileColisionable(tileRight, ty)) {
                    bloquea = true; break;
                }
            }
            if (bloquea) {
                // encajar justo antes del bloque
                float borde = tileRight * tile; // x del borde izquierdo del tile bloqueante
                personajeX = borde - hitW - offX; // pegado al borde
            } else {
                personajeX += dx;
            }
        } else { // dx < 0
            float destLeft = hb.x + dx;
            int tileLeft = (int) Math.floor(destLeft / tile);

            int tileY1 = (int) Math.floor(hb.y / tile);
            int tileY2 = (int) Math.floor((hb.y + hb.height - 0.001f) / tile);

            boolean bloquea = false;
            for (int ty = tileY1; ty <= tileY2; ty++) {
                if (colisiones.esTileColisionable(tileLeft, ty)) {
                    bloquea = true; break;
                }
            }
            if (bloquea) {
                // encajar justo después del bloque
                float borde = (tileLeft + 1) * tile; // borde derecho del tile bloqueante
                personajeX = borde - offX;          // pegado al borde
            } else {
                personajeX += dx;
            }
        }
    }

    private void moverEjeY(float dy) {
        if (dy == 0) return;
        float tile = colisiones.getTILE_SIZE();
        float offX = (getWidth() - hitW) * 0.5f; // para recomputar hitbox
        float offY = 0f;

        com.badlogic.gdx.math.Rectangle hb = getHitbox();

        if (dy > 0) {
            float destTop = hb.y + hb.height + dy;
            int tileTop = (int) Math.floor((destTop - 0.001f) / tile);

            int tileX1 = (int) Math.floor(hb.x / tile);
            int tileX2 = (int) Math.floor((hb.x + hb.width - 0.001f) / tile);

            boolean bloquea = false;
            for (int tx = tileX1; tx <= tileX2; tx++) {
                if (colisiones.esTileColisionable(tx, tileTop)) {
                    bloquea = true; break;
                }
            }
            if (bloquea) {
                float borde = tileTop * tile;       // borde inferior del bloqueante en Y+
                personajeY = borde - hitH - offY;   // encajar
            } else {
                personajeY += dy;
            }
        } else { // dy < 0
            float destBottom = hb.y + dy;
            int tileBottom = (int) Math.floor(destBottom / tile);

            int tileX1 = (int) Math.floor(hb.x / tile);
            int tileX2 = (int) Math.floor((hb.x + hb.width - 0.001f) / tile);

            boolean bloquea = false;
            for (int tx = tileX1; tx <= tileX2; tx++) {
                if (colisiones.esTileColisionable(tx, tileBottom)) {
                    bloquea = true; break;
                }
            }
            if (bloquea) {
                float borde = (tileBottom + 1) * tile; // borde superior del bloqueante
                personajeY = borde - offY;             // encajar
            } else {
                personajeY += dy;
            }
        }
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
        float len = (float)Math.sqrt(dx*dx + dy*dy);

        float movX = 0, movY = 0;
        if (len > 1f) {
            dx /= len; dy /= len;
            movX = dx * velPx * delta;
            movY = dy * velPx * delta;

            // dirección para animación
            if (Math.abs(movX) > Math.abs(movY)) {
                direccionActual = movX > 0 ? Direccion.DERECHA : Direccion.IZQUIERDA;
            } else {
                direccionActual = movY > 0 ? Direccion.ARRIBA : Direccion.ABAJO;
            }
        }

        // MOVER CON ENCAJE A BORDE (eje por eje)
        moverEjeX(movX);
        moverEjeY(movY);

        // Para la animación Idle / Walk:
        this.velocidadX = movX;
        this.velocidadY = movY;

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
        boolean mov = Math.abs(velocidadX) > 0.001f || Math.abs(velocidadY) > 0.001f;
        estaEnMovimiento = mov;
        return mov;
    }

    public void setBloqueado(boolean b) {
            bloqueado = b;
            velocidadX = 0;
            velocidadY = 0;
    }





}
