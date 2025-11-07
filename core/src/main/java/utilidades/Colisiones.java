package utilidades;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

public class Colisiones {

    private final Array<Rectangle> collisionRects = new Array<>();

    public Colisiones() { }
    public Colisiones(int[][] solids, int tileSizeParaFisica, int mapWidth, int mapHeight) { }

    // en Colisiones.java
    private final Rectangle tmp = new Rectangle();



    public void cargarDesdeMapa(TiledMap map, String nombreCapa, float unitScale) {
        collisionRects.clear();
        MapLayer layer = map.getLayers().get(nombreCapa);
        if (layer == null) {
            System.out.println("DEBUG: No se encontró la capa de colisiones: " + nombreCapa);
            return;
        }
        for (MapObject obj : layer.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                collisionRects.add(new Rectangle(
                    r.x * unitScale,
                    r.y * unitScale,
                    r.width * unitScale,
                    r.height * unitScale
                ));
            }
        }
    }

    public Array<Rectangle> getRectangulos() { return collisionRects; }



    /** Punto dentro de alguna colisión. */
    public boolean colisionaPunto(float x, float y) {
        for (Rectangle r : collisionRects) {
            if (r.contains(x, y)) return true;
        }
        return false;
    }

    /** Compatibilidad con tu firma vieja. */
    public boolean colisiona(Rectangle hitboxJugador) {
        for (Rectangle r : collisionRects) {
            if (Intersector.overlaps(hitboxJugador, r)) return true;
        }
        return false;
    }


    public boolean colisionaAABB(float x, float y, float w, float h) {
        tmp.set(x, y, w, h);
        for (Rectangle r : collisionRects) {
            if (r.overlaps(tmp)) return true;
        }
        return false;
    }

}
