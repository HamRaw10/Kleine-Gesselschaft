// utilidades/items/ClothingAnimationSet.java
package utilidades.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.EnumMap;

import javax.script.AbstractScriptEngine;

public class ClothingAnimationSet {

    public enum Dir { ARRIBA, ABAJO, DERECHA, IZQUIERDA }

    private final EnumMap<Dir, Animation<TextureRegion>> anims = new EnumMap<>(Dir.class);

    private float offsetX = 0f, offsetY = 0f;

    public ClothingAnimationSet setOffset(float ox, float oy){ this.offsetX=ox; this.offsetY=oy; return this; }
    public void put(Dir d, Animation<TextureRegion> a){ anims.put(d, a); }
    public Animation<TextureRegion> get(Dir d){ return anims.get(d); }

    public static ClothingAnimationSet loadFromFolder4(
        String baseFolder, String baseName, int frames, float frameDuration,
        String downSuffix, String upSuffix, String rightSuffix, String leftSuffix
    ){
        baseFolder = baseFolder.replace("\\", "/");
        ClothingAnimationSet set = new ClothingAnimationSet();
        set.put(Dir.ABAJO,     buildAnim(baseFolder, baseName + downSuffix,   frames));
        set.put(Dir.ARRIBA,    buildAnim(baseFolder, baseName + upSuffix,     frames));
        set.put(Dir.DERECHA,   buildAnim(baseFolder, baseName + rightSuffix,  frames));
        set.put(Dir.IZQUIERDA, buildAnim(baseFolder, baseName + leftSuffix,   frames));
        return set;
    }


    // Mantén este como helper con nombres por defecto si te sirve:
    public static ClothingAnimationSet loadFromFolder(String baseFolder, String baseName, int frames, float frameDuration){
        // por defecto: adelante/atrás/derecha/izquierda
        return loadFromFolder4(baseFolder, baseName, frames, frameDuration,
            "_adelante", "_atras", "_derecha", "_izquierda");
    }

    private static Animation<TextureRegion> buildAnim(String folder, String prefix, int frames){
        folder = folder.replace("\\", "/"); // <— sanitize
        TextureRegion[] regs = new TextureRegion[frames];

        if (frames <= 1) {
            String path = folder + "/" + prefix + ".png";   // SIN índice
            if (!Gdx.files.internal(path).exists()) throw new GdxRuntimeException("File not found (1f): " + path);
            Texture tex = new Texture(Gdx.files.internal(path));
            tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            regs[0] = new TextureRegion(tex);
            return new Animation<>(0.1f, regs);
        }

        for (int i = 1; i <= frames; i++) {
            String path = folder + "/" + prefix + i + ".png"; // CON índice
            if (!Gdx.files.internal(path).exists()) throw new GdxRuntimeException("File not found (multi): " + path);
            Texture tex = new Texture(Gdx.files.internal(path));
            tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            regs[i - 1] = new TextureRegion(tex);
        }
        return new Animation<>(0.08f, regs);
    }

    public void draw(SpriteBatch batch, Dir dir, float t, float x, float y, float w, float h){
        Animation<TextureRegion> a = anims.get(dir);
        if (a == null) return;
        TextureRegion frame = a.getKeyFrame(t, true);
        batch.draw(frame, x + offsetX, y + offsetY, w, h);
    }

    public void dispose(){
        for (Animation<TextureRegion> a : anims.values()){
            for (Object o : a.getKeyFrames()){
                TextureRegion tr = (TextureRegion) o;
                if (tr.getTexture()!=null) tr.getTexture().dispose();
            }
        }
        anims.clear();
    }
}
