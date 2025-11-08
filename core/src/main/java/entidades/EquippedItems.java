package entidades;

import java.util.EnumMap;
import utilidades.items.Item;

public class EquippedItems {
    private final EnumMap<EquipamentSlot, Item> slots = new EnumMap<>(EquipamentSlot.class);

    public Item get(EquipamentSlot s){ return slots.get(s); }
    public void set(EquipamentSlot s, Item item){ slots.put(s, item); }
    public EnumMap<EquipamentSlot, Item> all(){ return slots; }
}
