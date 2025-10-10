package utilidades;  // Ajusta el paquete según tu estructura


import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
        this.MAP_WIDTH = mapWidth * tileSize;
        this.MAP_HEIGHT = mapHeight * tileSize;
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
        int tileX1 = Math.max(0, (int)Math.floor(rect.x / TILE_SIZE));
        int tileY1 = Math.max(0, (int)Math.floor(rect.y / TILE_SIZE));
        int tileX2 = Math.min(MAP_WIDTH  - 1, (int)Math.floor((rect.x + rect.width  - 0.001f) / TILE_SIZE));
        int tileY2 = Math.min(MAP_HEIGHT - 1, (int)Math.floor((rect.y + rect.height - 0.001f) / TILE_SIZE));
        for (int y = tileY1; y <= tileY2; y++) {
            for (int x = tileX1; x <= tileX2; x++) {
                if (esTileColisionable(x, y)) {
                    Rectangle t = new Rectangle(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    if (rect.overlaps(t)) return true;
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

    public boolean colisionaRect(float x, float y, float w, float h) {
        return colisionConTiles(new Rectangle(x, y, w, h));
    }


    public void setTileColisionable(int tileX, int tileY, boolean colisiona) {
        if (tileX < 0 || tileX >= MAP_WIDTH || tileY < 0 || tileY >= MAP_HEIGHT) return;
        tileTypes[tileY][tileX] = colisiona ? 1 : 0;
    }

    public void setRegionColisionableTiles(int tx0, int ty0, int tx1, int ty1, boolean colisiona) {
        int minX = Math.max(0, Math.min(tx0, tx1));
        int maxX = Math.min(MAP_WIDTH  - 1, Math.max(tx0, tx1));
        int minY = Math.max(0, Math.min(ty0, ty1));
        int maxY = Math.min(MAP_HEIGHT - 1, Math.max(ty0, ty1));
        int val = colisiona ? 1 : 0;
        for (int ty = minY; ty <= maxY; ty++)
            for (int tx = minX; tx <= maxX; tx++)
                tileTypes[ty][tx] = val;
    }

    public void setRegionColisionablePixels(float x, float y, float w, float h, boolean colisiona) {
        int tx0 = (int) Math.floor(x / TILE_SIZE);
        int ty0 = (int) Math.floor(y / TILE_SIZE);
        int tx1 = (int) Math.floor((x + w - 1) / TILE_SIZE);
        int ty1 = (int) Math.floor((y + h - 1) / TILE_SIZE);
        setRegionColisionableTiles(tx0, ty0, tx1, ty1, colisiona);
    }

    public void setCirculoColisionablePixels(float cx, float cy, float radio, boolean colisiona) {
        int tx0 = (int) Math.floor((cx - radio) / TILE_SIZE);
        int ty0 = (int) Math.floor((cy - radio) / TILE_SIZE);
        int tx1 = (int) Math.floor((cx + radio) / TILE_SIZE);
        int ty1 = (int) Math.floor((cy + radio) / TILE_SIZE);
        int val = colisiona ? 1 : 0;

        for (int ty = Math.max(0, ty0); ty <= Math.min(MAP_HEIGHT - 1, ty1); ty++) {
            for (int tx = Math.max(0, tx0); tx <= Math.min(MAP_WIDTH - 1, tx1); tx++) {
                float cxTile = tx * TILE_SIZE + TILE_SIZE * 0.5f;
                float cyTile = ty * TILE_SIZE + TILE_SIZE * 0.5f;
                float dx = cxTile - cx, dy = cyTile - cy;
                if (dx*dx + dy*dy <= radio*radio) tileTypes[ty][tx] = val;
            }
        }
    }


    public void toggleRegionTiles(int tx0, int ty0, int tx1, int ty1) {
        int minX = Math.max(0, Math.min(tx0, tx1));
        int maxX = Math.min(MAP_WIDTH  - 1, Math.max(tx0, tx1));
        int minY = Math.max(0, Math.min(ty0, ty1));
        int maxY = Math.min(MAP_HEIGHT - 1, Math.max(ty0, ty1));
        for (int ty = minY; ty <= maxY; ty++)
            for (int tx = minX; tx <= maxX; tx++)
                tileTypes[ty][tx] = (tileTypes[ty][tx] == 1) ? 0 : 1;
    }

    // === A) Un (1) tile: coloca en el mapa visual y marca colisión ===
    public void ponerTile(TextureRegion region, int tileX, int tileY,
                          boolean solido, TextureRegion[][] mapaVisual) {
        if (tileX < 0 || tileX >= MAP_WIDTH || tileY < 0 || tileY >= MAP_HEIGHT) return;
        mapaVisual[tileY][tileX] = region;          // dibujado
        tileTypes[tileY][tileX]  = solido ? 1 : 0;  // colisión
    }

    // === B) Bloque rectangular NxM desde el atlas: todo sólido o todo libre ===
    public void ponerBloque(TextureRegion[][] atlas,
                            int atlasRow, int atlasCol, int anchoTiles, int altoTiles,
                            int destTileX, int destTileY, boolean solido,
                            TextureRegion[][] mapaVisual) {
        for (int dy = 0; dy < altoTiles; dy++) {
            for (int dx = 0; dx < anchoTiles; dx++) {
                int tx = destTileX + dx;
                int ty = destTileY + dy;
                if (tx < 0 || tx >= MAP_WIDTH || ty < 0 || ty >= MAP_HEIGHT) continue;

                mapaVisual[ty][tx] = atlas[atlasRow + dy][atlasCol + dx];
                tileTypes[ty][tx] = solido ? 1 : 0;
            }
        }
    }

    // === C) Bloque NxM con MÁSCARA de colisión (true = sólido, false = libre) ===
    public void ponerBloqueConMascara(TextureRegion[][] atlas,
                                      int atlasRow, int atlasCol, int anchoTiles, int altoTiles,
                                      int destTileX, int destTileY, boolean[][] mascaraSolida,
                                      TextureRegion[][] mapaVisual) {
        for (int dy = 0; dy < altoTiles; dy++) {
            for (int dx = 0; dx < anchoTiles; dx++) {
                int tx = destTileX + dx;
                int ty = destTileY + dy;
                if (tx < 0 || tx >= MAP_WIDTH || ty < 0 || ty >= MAP_HEIGHT) continue;

                mapaVisual[ty][tx] = atlas[atlasRow + dy][atlasCol + dx];
                boolean solido = mascaraSolida != null && dy < mascaraSolida.length
                    && dx < mascaraSolida[dy].length && mascaraSolida[dy][dx];
                tileTypes[ty][tx] = solido ? 1 : 0;
            }
        }
    }





    // Getters para acceso externo si necesitas
    public int getTILE_SIZE() { return TILE_SIZE; }
    public int getMAP_WIDTH() { return MAP_WIDTH; }
    public int getMAP_HEIGHT() { return MAP_HEIGHT; }



}

