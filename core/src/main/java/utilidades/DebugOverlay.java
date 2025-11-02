package utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import java.util.Locale;

import entidades.Jugador;

public class DebugOverlay {

    private boolean enabled = false;          // Toggle con F3
    private final BitmapFont font = new BitmapFont(); // fuente por defecto (uiskin no necesario)

    // Colores de ayuda
    private static final Color COLISION_COLOR = new Color(1f, 0f, 0f, 0.9f);    // rojo
    private static final Color HITBOX_COLOR   = new Color(1f, 1f, 0f, 0.95f);   // amarillo
    private static final Color TEXT_COLOR     = new Color(0.95f, 0.95f, 0.95f, 1f);

    /** Llama esto cada frame para alternar con F3 (Windows) o Fn+F3 (Mac). */

    public void pollToggleKey() {
        // F3 (Minecraft-like) y alternativas por si macOS intercepta F3
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)
            || Gdx.input.isKeyJustPressed(Input.Keys.F9)
            || Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) { // tecla ` (backtick)
            enabled = !enabled;
        }
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean value) { enabled = value; }

    /**
     * Dibuja el overlay y las guías de colisión/hitbox si está activado.
     * - shape: ShapeRenderer configurado por la pantalla (se usa en modo world-space)
     * - batch: SpriteBatch de la pantalla (se usa en modo UI/screen-space)
     * - jugador: para hitbox/posición
     * - colisiones: para rectangles del mapa
     * - camMundo: cámara del mundo (tiles/entidades)
     * - camUI: cámara para texto en pantalla
     * - tileW/tileH: tamaño de tile del mapa
     * - mapName: nombre o ruta del tmx actual (mostrar en HUD)
     */
    public void render(ShapeRenderer shape,
                       SpriteBatch batch,
                       Jugador jugador,
                       Colisiones colisiones,
                       OrthographicCamera camMundo,
                       OrthographicCamera camUI,
                       int tileW, int tileH,
                       String mapName) {

        if (!enabled) return;

        // ===== 1) Dibujar rectángulos de colisión en el MUNDO =====
        shape.setProjectionMatrix(camMundo.combined);
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(COLISION_COLOR);

        Array<Rectangle> rects = (colisiones != null) ? colisiones.getRectangulos() : null;
        if (rects != null) {
            for (Rectangle r : rects) {
                shape.rect(r.x, r.y, r.width, r.height);
            }
        }

        // ===== 2) Dibujar HITBOX del jugador =====
        if (jugador != null && jugador.getHitbox() != null) {
            shape.setColor(HITBOX_COLOR);
            Rectangle hb = jugador.getHitbox();
            shape.rect(hb.x, hb.y, hb.width, hb.height);
        }
        shape.end();

        // ===== 3) Texto estilo “F3”: FPS + posición + bloque/tile + datos mapa =====
        final float px = (jugador != null) ? jugador.getPersonajeX() : 0f;
        final float py = (jugador != null) ? jugador.getPersonajeY() : 0f;
        final int blockX = Math.round(px);
        final int blockY = Math.round(py);
        final int tileX  = (tileW > 0) ? (int) Math.floor(px / tileW) : 0;
        final int tileY  = (tileH > 0) ? (int) Math.floor(py / tileH) : 0;

        StringBuilder sb = new StringBuilder();
        sb.append("Minecraft-like Debug (F3)\n");
        sb.append("FPS: ").append(Gdx.graphics.getFramesPerSecond()).append("\n");
        sb.append(String.format(Locale.US, "Pos: X=%.1f  Y=%.1f  (Block: %d, %d)\n", px, py, blockX, blockY));
        sb.append(String.format(Locale.US, "Tile: (%d, %d)  TileSize: %dx%d\n", tileX, tileY, tileW, tileH));
        if (camMundo != null) {
            sb.append(String.format(Locale.US, "Cam Zoom: %.3f  View: %.0fx%.0f\n",
                camMundo.zoom, camMundo.viewportWidth, camMundo.viewportHeight));
        }
        if (rects != null) sb.append("Collision rects: ").append(rects.size).append("\n");
        if (mapName != null) sb.append("Map: ").append(mapName).append("\n");

        // Dibujo en pantalla (UI)
        batch.setProjectionMatrix(camUI.combined);
        batch.begin();
        font.setColor(TEXT_COLOR);
        // esquina superior izquierda (con pequeño margen)
        float startX = 8f;
        float startY = camUI.viewportHeight - 8f;
        font.draw(batch, sb.toString(), startX, startY);
        batch.end();
    }

    public void dispose() {
        font.dispose();
    }
}
