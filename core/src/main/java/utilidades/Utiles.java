package utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class Utiles {

    public static boolean Clickear(){
        return Gdx.input.isTouched();
    }

    public static float getMouseX() {
        return Gdx.input.getX();
    }

    public static float getMouseY() {
        return Gdx.graphics.getHeight() - Gdx.input.getY();
    }

    private static TextureRegion[][] sliceTiles(Texture tex, int tileW, int tileH, int margin, int spacing) {
        int cols = (tex.getWidth()  - 2*margin + spacing) / (tileW + spacing);
        int rows = (tex.getHeight() - 2*margin + spacing) / (tileH + spacing);
        TextureRegion[][] out = new TextureRegion[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = margin + c * (tileW + spacing);
                int y = margin + r * (tileH + spacing);
                out[r][c] = new TextureRegion(tex, x, y, tileW, tileH);
            }
        }
        return out;
    }

    private static int[][] loadCSV(String path, int width, int height) {
        String csv = Gdx.files.internal(path).readString().trim();
        String[] toks = csv.split("[,\\n\\r]+");
        int[][] data = new int[height][width];
        int k = 0;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                data[y][x] = Integer.parseInt(toks[k++].trim());
        return data;
    }


}
