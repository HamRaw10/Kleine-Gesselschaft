package utilidades.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.EnumMap;
import entidades.EquipamentSlot;

public class VisualOutfit {
    private final EnumMap<EquipamentSlot, ClothingAnimationSet> layers =
        new EnumMap<>(EquipamentSlot.class);

    public void set(EquipamentSlot slot, ClothingAnimationSet set){
        layers.put(slot, set);
    }
    public ClothingAnimationSet get(EquipamentSlot slot){ return layers.get(slot); }

    /** Dibuja en orden de capas para que quede natural */
    public void draw(SpriteBatch batch, ClothingAnimationSet.Dir dir, float stateTime,
                     float x, float y, float w, float h){
        // Orden recomendado: zapatillas -> pantalón -> remera -> cabello -> accesorios
        drawIf(batch, EquipamentSlot.PIES,    dir, stateTime, x, y, w, h);
        drawIf(batch, EquipamentSlot.PIERNAS, dir, stateTime, x, y, w, h);
        drawIf(batch, EquipamentSlot.TORSO,   dir, stateTime, x, y, w, h);
        drawIf(batch, EquipamentSlot.CABEZA,  dir, stateTime, x, y, w, h);
        drawIf(batch, EquipamentSlot.ACCESORIO,dir,stateTime, x, y, w, h);
    }

    private void drawIf(SpriteBatch batch, EquipamentSlot slot, ClothingAnimationSet.Dir dir,
                        float t, float x, float y, float w, float h){
        ClothingAnimationSet set = layers.get(slot);
        if (set != null) set.draw(batch, dir, t, x, y, w, h);
    }

    public void dispose(){
        for (ClothingAnimationSet s : layers.values()){
            if (s != null) s.dispose();
        }
        layers.clear();
    }
}
