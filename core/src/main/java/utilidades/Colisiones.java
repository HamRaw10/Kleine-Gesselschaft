package utilidades;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Colisiones {

    private final Array<Rectangle> collisionRects = new Array<>();

    public Colisiones(int[][] solids, int tileSizeParaFisica, int mapWidth, int mapHeight) {
    }

    public Colisiones() {

    }

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
                // Crear una copia escalada
                collisionRects.add(new Rectangle(
                    r.x * unitScale,
                    r.y * unitScale,
                    r.width * unitScale,
                    r.height * unitScale
                ));
            }
        }
    }

    public Array<Rectangle> getRectangulos() {
        return collisionRects;
    }

    public boolean colisiona(Rectangle hitboxJugador) {
        for (Rectangle r : collisionRects) {
            if (r.overlaps(hitboxJugador)) return true;
        }
        return false;
    }
}
