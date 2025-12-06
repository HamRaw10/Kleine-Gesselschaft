package utilidades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import entidades.Jugador;
import entidades.Mochila;
import entidades.EquipamentSlot;
import utilidades.items.ClothingItem;
import utilidades.items.Item;

public class Tienda {

    public static class Oferta {
        private final ClothingItem prototipo;
        private final int precio;

        public Oferta(ClothingItem prototipo, int precio) {
            this.prototipo = prototipo;
            this.precio = precio;
        }

        public ClothingItem crearItem() {
            return new ClothingItem(
                prototipo.getId(),
                prototipo.getNombre(),
                prototipo.getSlot(),
                prototipo.getFolder(),
                prototipo.getBaseName(),
                prototipo.getVariant()
            );
        }

        public String getId() { return prototipo.getId(); }
        public String getNombre() { return prototipo.getNombre(); }
        public String getNombreMinuscula() { return prototipo.getNombre().toLowerCase(); }
        public int getPrecio() { return precio; }
    }

    private final List<Oferta> ofertas = new ArrayList<>();

    public Tienda(List<Oferta> ofertas) {
        if (ofertas != null) this.ofertas.addAll(ofertas);
    }

    public List<Oferta> getOfertas() {
        return Collections.unmodifiableList(ofertas);
    }

    /** Devuelve true si el jugador ya tiene un item con ese id en la mochila. */
    public boolean yaPosee(Jugador jugador, String itemId) {
        if (jugador == null || itemId == null) return false;
        Mochila m = jugador.getMochila();
        for (Item it : m.getItems()) {
            if (itemId.equalsIgnoreCase(it.getId())) return true;
        }
        return false;
    }

    /**
     * Intenta comprar la oferta: descuenta monedas, crea un nuevo item y lo añade a la mochila.
     * No compra si ya existe o si no hay fondos.
     */
    public boolean comprar(Jugador jugador, Oferta oferta) {
        if (jugador == null || oferta == null) return false;
        if (yaPosee(jugador, oferta.getId())) return false;
        if (!jugador.getDinero().restar(oferta.getPrecio())) return false;
        jugador.getMochila().add(oferta.crearItem());
        return true;
    }

    /** Lista predeterminada de ropa que coincide con la del inventario. */
    public static Tienda crearTiendaHippie() {
        List<Oferta> lista = Arrays.asList(
            new Oferta(new ClothingItem("pant-hippie", "pantalón hippie",
                EquipamentSlot.PIERNAS, "Ropa/Pantalones", "pantalon_hippie", 1), 20),
            new Oferta(new ClothingItem("pant-jean", "pantalón jean",
                EquipamentSlot.PIERNAS, "Ropa/Pantalones", "pantalon_jean", 1), 20),
            new Oferta(new ClothingItem("pant-marron", "pantalón marrón",
                EquipamentSlot.PIERNAS, "Ropa/Pantalones", "pantalon_marron", 1), 20),
            new Oferta(new ClothingItem("remera-hippie", "remera hippie",
                EquipamentSlot.TORSO, "Ropa/Remeras", "remera_hippie", 1), 20),
            new Oferta(new ClothingItem("remera-boca", "remera boca",
                EquipamentSlot.TORSO, "Ropa/Remeras", "remera_boca", 1), 20),
            new Oferta(new ClothingItem("remera-river", "remera river",
                EquipamentSlot.TORSO, "Ropa/Remeras", "remera_river", 1), 20)
        );
        return new Tienda(lista);
    }
}
