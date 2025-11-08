package utilidades.items;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ItemDB {
    private static final Map<String, Item> ITEMS;
    static {
        Map<String, Item> t = new HashMap<>();

        // Consumibles
        t.put("cafe", new Item("cafe","Café", ItemType.CONSUMIBLE, new StatsModifier(0.5f)));
        t.put("te",   new Item("te","Té", ItemType.CONSUMIBLE, new StatsModifier(0.3f)));
        t.put("jugo", new Item("jugo","Jugo", ItemType.CONSUMIBLE, StatsModifier.none()));

        // Ropa
        t.put("remera_basica",      new Item("remera_basica","Remera básica", ItemType.ROPA, StatsModifier.none()));
        t.put("campera_urbana",     new Item("campera_urbana","Campera urbana", ItemType.ROPA, StatsModifier.none()));
        t.put("pantalon_jean",      new Item("pantalon_jean","Pantalón de jean", ItemType.ROPA, StatsModifier.none()));
        t.put("gorro_lana",         new Item("gorro_lana","Gorro de lana", ItemType.ROPA, StatsModifier.none()));
        t.put("anteojos_cuadrados", new Item("anteojos_cuadrados","Anteojos", ItemType.ROPA, StatsModifier.none()));
        t.put("zapatillas_basicas", new Item("zapatillas_basicas","Zapatillas básicas", ItemType.ROPA, new StatsModifier(0.2f)));
        t.put("zapatillas_rapidas", new Item("zapatillas_rapidas","Zapatillas rápidas", ItemType.ROPA, new StatsModifier(0.8f)));

        ITEMS = Collections.unmodifiableMap(t);
    }

    public static Item get(String id){ return ITEMS.get(id); }
    public static Collection<Item> all(){ return ITEMS.values(); }
}
