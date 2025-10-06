package entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;

public class InventarioPersonalizable {

    // Categorías de personalización
    public enum Categoria {
        PIEL, PELO, ROPA, PANTALONES, CALZADO, ACCESORIO
    }

    // Mapas para almacenar los assets seleccionados y disponibles
    private Map<Categoria, String> assetsSeleccionados; // Ruta del asset seleccionado
    private Map<Categoria, Map<String, TextureRegion>> assetsDisponibles; // Categoría -> Nombre del asset -> TextureRegion

    public InventarioPersonalizable() {
        assetsSeleccionados = new HashMap<>();
        assetsDisponibles = new HashMap<>();

        // Inicializar categorías con mapas vacíos
        for (Categoria cat : Categoria.values()) {
            assetsDisponibles.put(cat, new HashMap<>());
        }

        // Establecer valores por defecto (puedes cargarlos desde un archivo o configurarlos aquí)
        assetsSeleccionados.put(Categoria.PIEL, "personaje/piel/piel_base.png");
        assetsSeleccionados.put(Categoria.PELO, "personaje/pelo/pelo_castano.png");
        assetsSeleccionados.put(Categoria.ROPA, "personaje/ropa/camiseta_azul.png");
        assetsSeleccionados.put(Categoria.PANTALONES, "personaje/pantalones/pantalon_vaquero.png");
        assetsSeleccionados.put(Categoria.CALZADO, "personaje/calzado/zapatos_marrones.png");
        assetsSeleccionados.put(Categoria.ACCESORIO, null); // Sin accesorio por defecto
    }

    /**
     * Carga un asset y lo añade a la lista de disponibles para una categoría.
     * @param categoria La categoría del asset (PIEL, PELO, etc.)
     * @param nombreAsset Un nombre único para este asset (ej. "piel_clara", "pelo_rubio")
     * @param rutaAsset La ruta del archivo de la textura.
     */
    public void cargarAssetDisponible(Categoria categoria, String nombreAsset, String rutaAsset) {
        Texture texture = new Texture(rutaAsset);
        assetsDisponibles.get(categoria).put(nombreAsset, new TextureRegion(texture));
    }

    /**
     * Selecciona un asset para una categoría.
     * @param categoria La categoría a modificar.
     * @param nombreAsset El nombre del asset a seleccionar (debe haber sido cargado previamente).
     */
    public void seleccionarAsset(Categoria categoria, String nombreAsset) {
        if (assetsDisponibles.get(categoria).containsKey(nombreAsset) || nombreAsset == null) {
            assetsSeleccionados.put(categoria, nombreAsset);
        } else {
            Gdx.app.error("Inventario", "Asset '" + nombreAsset + "' no encontrado para la categoría " + categoria);
        }
    }

    /**
     * Obtiene el TextureRegion del asset actualmente seleccionado para una categoría.
     * @param categoria La categoría a consultar.
     * @return El TextureRegion del asset seleccionado, o null si no hay ninguno o no se ha cargado.
     */
    public TextureRegion getAssetSeleccionado(Categoria categoria) {
        String nombreAsset = assetsSeleccionados.get(categoria);
        if (nombreAsset != null) {
            return assetsDisponibles.get(categoria).get(nombreAsset);
        }
        return null;
    }

    /**
     * Obtiene todos los assets disponibles para una categoría.
     * @param categoria La categoría a consultar.
     * @return Un mapa de nombres de assets a TextureRegions.
     */
    public Map<String, TextureRegion> getAssetsDisponibles(Categoria categoria) {
        return assetsDisponibles.get(categoria);
    }

    /**
     * Libera los recursos de todas las texturas cargadas.
     */
    public void dispose() {
        for (Map<String, TextureRegion> categoriaAssets : assetsDisponibles.values()) {
            for (TextureRegion region : categoriaAssets.values()) {
                if (region.getTexture() != null) {
                    region.getTexture().dispose();
                }
            }
        }
    }
}
