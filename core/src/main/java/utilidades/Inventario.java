package utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

import entidades.Jugador;
import entidades.EquipamentSlot;
import utilidades.items.ClothingItem;
import utilidades.items.Item;
import entidades.Mochila;

public class Inventario {

    private final Stage stage;        // Stage propio del inventario
    private final Skin skin;
    private final Group root;         // Contenedor para poder add/remove fácil
    private final Jugador jugador;

    private final Table grid;         // Grilla de items
    private final Label titulo;

    private final int ICON_SIZE = 48;
    private final int COLS = 4;

    private boolean visible = false;

    public Inventario(Stage stage, Skin skin, Jugador jugador) {
        this.stage = stage;
        this.skin = skin;
        this.jugador = jugador;

        this.root = new Group();
        this.grid = new Table(skin);
        this.titulo = new Label("Inventario - Ropa", skin);

        buildUI();
        actualizarSlots(); // primera carga
        // OJO: no agregamos 'root' al stage todavía. Lo hace setVisible(true)
    }

    // === Construcción UI ===
    private void buildUI() {
        // Darle tamaño inicial al root (por si se abre antes del primer resize)
        root.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());

        Table panel = new Table(skin);
        panel.setFillParent(true);      // <- importante
        panel.pad(20f);
        panel.defaults().pad(6f);

        titulo.setAlignment(Align.center);
        panel.add(titulo).colspan(2).growX().row();

        ScrollPane scroll = new ScrollPane(grid, skin);
        scroll.setFadeScrollBars(false);
        panel.add(scroll).colspan(2).grow().row();

        TextButton cerrar = new TextButton("Cerrar (E)", skin);
        cerrar.addListener(new ClickListener(){
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { return true; }
            @Override public void clicked(InputEvent event, float x, float y) { setVisible(false); }
        });
        panel.add(cerrar).right().colspan(2).row();

        root.addActor(panel);
        root.setVisible(false);
    }


    // === Refresca la grilla con los ítems de la mochila ===
    public void actualizarSlots() {

        grid.clear();

        Mochila m = jugador.getMochila();
        Array<Item> items = new Array<>(m.getItems().toArray(new Item[0]));

        int col = 0;
        for (Item it : items) {
            if (!(it instanceof ClothingItem)) continue;
            final ClothingItem ci = (ClothingItem) it;

            ImageButton btn = buildItemButton(ci);
            btn.setUserObject(ci);

            Label nombre = new Label(ci.getDisplayName(), skin);
            nombre.setWrap(true);

            Table cell = new Table(skin);
            cell.defaults().pad(2f);
            cell.add(btn).size(ICON_SIZE).row();
            cell.add(nombre).width(ICON_SIZE + 24f).center();

            if (esElEquipado(ci)) {
                nombre.setText("★ " + ci.getDisplayName());
            }

            btn.addListener(new ClickListener() {
                @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override  public void clicked(InputEvent event, float x, float y) {
                    ImageButton btn = (ImageButton) event.getListenerActor();
                    ClothingItem ci = (ClothingItem) btn.getUserObject();
                    System.out.println("INVENTARIO: Clic en ítem " + ci.getId() + " (slot: " + ci.getSlot() + ")");

                    Item equippedItem = jugador.getEquipped().get(ci.getSlot());
                    if (equippedItem == null || !equippedItem.getId().equals(ci.getId())) {
                        System.out.println("INVENTARIO: Intentando equipar " + ci.getId());
                        boolean success = jugador.equipClothing(ci);
                        if (success) {
                            System.out.println("INVENTARIO: Equipado exitosamente " + ci.getId());
                        } else {
                            System.out.println("INVENTARIO: Falló equipar " + ci.getId());
                        }
                    } else {
                        System.out.println("INVENTARIO: Desequipando " + ci.getId());
                        jugador.unequipClothing(ci.getSlot());
                    }
                    actualizarSlots();  // Refrescar UI
                }
            });

            grid.add(cell).pad(6f);
            col++;
            if (col >= COLS) { col = 0; grid.row(); }
        }
    }

    private ImageButton buildItemButton(ClothingItem ci) {
        TextureRegionDrawable icon = new TextureRegionDrawable(
            new TextureRegion(getIconTexture(ci)));
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = icon;
        style.imageDown = icon;
        style.up = simplePanel(ICON_SIZE + 8, ICON_SIZE + 8, 0.12f);
        style.down = simplePanel(ICON_SIZE + 8, ICON_SIZE + 8, 0.2f);
        return new ImageButton(style);
    }

    private Texture getIconTexture(ClothingItem ci) {
        String path = ci.getFolder() + "/" + ci.getBaseName() + "_adelante.png";
        Gdx.app.log("INV", "Cargando ícono: " + path);
        try {
            return new Texture(Gdx.files.internal(path));
        } catch (GdxRuntimeException e) {
            Gdx.app.error("INV", "Error cargando ícono: " + path, e);
            return null;
        }
    }

    private TextureRegionDrawable simplePanel(int w, int h, float gray) {
        Pixmap pm = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        Color c = new Color(gray, gray, gray, 0.9f);
        pm.setColor(c);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return new TextureRegionDrawable(new TextureRegion(t));
    }

    private boolean esElEquipado(ClothingItem ci) {
        Item actual = jugador.getEquipped().get(ci.getSlot());
        if (actual == null) return false;
        if (!(actual instanceof ClothingItem)) return false;
        ClothingItem eq = (ClothingItem) actual;
        return eq.getFolder().equals(ci.getFolder()) &&
            eq.getBaseName().equals(ci.getBaseName());
    }

    // === Ciclo de dibujo/actualización ===
    public void render() {
        if (!visible) return;
        stage.draw();
    }

    public void actualizar(float delta) {
        if (!visible) return;
        stage.act(delta);
    }

    // === API para PantallaJuego ===
    public Group getRoot()  { return root; }
    public Stage getStage() { return stage; }

    public boolean isVisible() { return visible; }

    public void setVisible(boolean v) {
        if (this.visible == v) return;
        this.visible = v;

        if (v) {
            if (root.getStage() == null) stage.addActor(root);
            // Ajustar tamaño del contenedor a la pantalla actual
            root.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
            root.setVisible(true);
            setInputProcessor();
        } else {
            root.setVisible(false);
            root.remove();
        }
    }


    public void setInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        root.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
    }


    public void dispose() {
        stage.dispose();
    }
}
