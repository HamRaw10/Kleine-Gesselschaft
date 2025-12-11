package utilidades;

import com.badlogic.gdx.math.Rectangle;

/**
 * Portal sencillo para usar con libGDX + Tiled.
 * Se instancia vacío y se completan los campos desde el loader.
 */
public class Portal {
    public Rectangle rect;
    public String targetMap;   // Opción A: salto a otro TMX
    public String targetArea;  // Opción B: alternar grupos dentro del mismo TMX
    public float spawnX, spawnY;
    public String transicion;
    public boolean hasCustomSpawn; // true si vino spawnX/spawnY desde Tiled

    public boolean isToMap()  { return targetMap  != null && !targetMap.isEmpty(); }
    public boolean isToArea() { return targetArea != null && !targetArea.isEmpty(); }
}
