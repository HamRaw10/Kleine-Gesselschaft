package utilidades.items;

public class Item {
    private final String id;       // "zapatillas_rapidas"
    private final String nombre;   // "Zapatillas rápidas"
    private final ItemType tipo;
    private final StatsModifier mod;

    public Item(String id, String nombre, ItemType tipo, StatsModifier mod){
        this.id = id; this.nombre = nombre; this.tipo = tipo; this.mod = mod;
    }
    public String getId(){ return id; }
    public String getNombre(){ return nombre; }
    public ItemType getTipo(){ return tipo; }
    public StatsModifier getMod(){ return mod; }
}
