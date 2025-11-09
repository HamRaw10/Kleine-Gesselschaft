package utilidades.items;

import entidades.EquipamentSlot;

public class ClothingItem extends Item {
    private final EquipamentSlot slot;
    private final String folder;
    private final String baseName;
    private final int variant;

    public ClothingItem(String id, String nombre, EquipamentSlot slot,
                        String folder, String baseName, int variant) {
        // Llamamos al constructor correcto del padre Item
        // Si no te interesan los últimos parámetros (ItemType y StatsModifier),
        // podés pasar valores por defecto o null.
        super(id, nombre, ItemType.ROPA, null);

        this.slot = slot;
        this.folder = folder;
        this.baseName = baseName;
        this.variant = variant;
    }

    public EquipamentSlot getSlot() { return slot; }
    public String getFolder() { return folder; }
    public String getBaseName() { return baseName; }
    public int getVariant() { return variant; }

    public String getDisplayName() {
        return super.getNombre(); // o 'nombre' según cómo lo definiste en Item
    }

}
