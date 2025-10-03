package utilidades;  // Ajusta el paquete según tu estructura

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para manejar colisiones en el juego.
 * Soporta colisiones tile-based (con mapa) y entre entidades (rectángulos).
 */
public class Colisiones {
    private final int[][] tileTypes;  // 0 = libre, 1 = colisionable, etc.
    private final int TILE_SIZE;
    private final int MAP_WIDTH;
    private final int MAP_HEIGHT;

    public Colisiones(int[][] tileTypes, int tileSize, int mapWidth, int mapHeight) {
        this.tileTypes = tileTypes;
        this.TILE_SIZE = tileSize;
        this.MAP_WIDTH = mapWidth;
        this.MAP_HEIGHT = mapHeight;
    }

    /**
     * Verifica si un tile en posición (tileX, tileY) es colisionable.
     * @param tileX Columna del tile (x / TILE_SIZE)
     * @param tileY Fila del tile (y / TILE_SIZE)
     * @return true si es colisionable (bloquea movimiento)
     */
    public boolean esTileColisionable(int tileX, int tileY) {
        if (tileX < 0 || tileX >= MAP_WIDTH || tileY < 0 || tileY >= MAP_HEIGHT) {
            return true;  // Fuera del mapa = colisión (borde del mundo)
        }
        return tileTypes[tileY][tileX] == 1;  // Asume 1 = colisionable; expande si necesitas más tipos
    }

    /**
     * Chequea si el rectángulo (entidad) colisiona con algún tile colisionable.
     * @param rect Bounding box de la entidad (jugador, etc.)
     * @return true si hay colisión con un tile
     */
    public boolean colisionConTiles(Rectangle rect) {
        // Calcular tiles que cubre el rectángulo
        int tileX1 = (int) (rect.x / TILE_SIZE);
        int tileY1 = (int) (rect.y / TILE_SIZE);
        int tileX2 = (int) ((rect.x + rect.width) / TILE_SIZE);
        int tileY2 = (int) ((rect.y + rect.height) / TILE_SIZE);

        // Chequear cada tile en el área
        for (int y = tileY1; y <= tileY2; y++) {
            for (int x = tileX1; x <= tileX2; x++) {
                if (esTileColisionable(x, y)) {
                    // Verificar colisión precisa con el tile (no solo si el tile es colisionable)
                    Rectangle tileRect = new Rectangle(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    if (rect.overlaps(tileRect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Predice si mover la entidad a una nueva posición causaría colisión con tiles.
     * Útil para movimiento suave: prueba el movimiento antes de aplicarlo.
     * @param entidadPos Posición actual (Vector2 con x,y)
     * @param nuevaPos Nueva posición tentativa
     * @param entidadSize Ancho y alto de la entidad (para crear rect)
     * @return true si la nueva posición colisiona
     */
    public boolean colisionAlMover(Vector2 entidadPos, Vector2 nuevaPos, Vector2 entidadSize) {
        Rectangle nuevaRect = new Rectangle(nuevaPos.x, nuevaPos.y, entidadSize.x, entidadSize.y);
        return colisionConTiles(nuevaRect);
    }

    /**
     * Chequea colisión entre dos entidades (rectángulos).
     * @param rect1 Bounding box de la primera entidad
     * @param rect2 Bounding box de la segunda entidad
     * @return true si colisionan
     */
    public boolean colisionEntreEntidades(Rectangle rect1, Rectangle rect2) {
        return rect1.overlaps(rect2);
    }

    /**
     * Resuelve colisión simple: Ajusta la posición de la entidad para evitar superposición con tiles.
     * (Básico: mueve hacia atrás en el eje de colisión. Para más avanzado, usa separación de ejes.)
     * @param rect Entidad que colisiona
     * @param movimiento Vector de movimiento intentado (dx, dy)
     * @return Nueva posición ajustada (Vector2)
     */
    public Vector2 resolverColision(Rectangle rect, Vector2 movimiento) {
        // Probar movimiento en X primero
        rect.x += movimiento.x;
        if (colisionConTiles(rect)) {
            rect.x -= movimiento.x;  // Revertir X
        }

        // Probar movimiento en Y
        rect.y += movimiento.y;
        if (colisionConTiles(rect)) {
            rect.y -= movimiento.y;  // Revertir Y
        }

        return new Vector2(rect.x, rect.y);
    }

    public List<Rectangle> obtenerObstaculosEnArea(float areaX, float areaY, float areaWidth, float areaHeight) {
        List<Rectangle> obstaculos = new ArrayList<>();
        int startX = Math.max(0, (int)((areaX - areaWidth/2) / TILE_SIZE));
        int startY = Math.max(0, (int)((areaY - areaHeight/2) / TILE_SIZE));
        int endX = Math.min(MAP_WIDTH, (int)((areaX + areaWidth/2) / TILE_SIZE) + 1);
        int endY = Math.min(MAP_HEIGHT, (int)((areaY + areaHeight/2) / TILE_SIZE) + 1);
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                if (esTileColisionable(x, y)) {
                    Rectangle tileRect = new Rectangle(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    obstaculos.add(tileRect);
                }
            }
        }
        return obstaculos;
    }



    // Getters para acceso externo si necesitas
    public int getTILE_SIZE() { return TILE_SIZE; }
    public int getMAP_WIDTH() { return MAP_WIDTH; }
    public int getMAP_HEIGHT() { return MAP_HEIGHT; }
}

