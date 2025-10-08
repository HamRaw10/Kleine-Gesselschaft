package utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import entidades.Jugador;

public class Inventario {
    private Stage escenarioInventario;
    private boolean visible;
    public Jugador jugador;
    private Float velBackup = null;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private Skin skin;
    private Table tablaPrincipal;
    private Table tablaSlots;
    private Table tablaCategorias;
    private Chat chat; // Referencia al chat para verificar si está visible

    // Categorías (pestañas simples)
    private String[] categorias = {"Ropa", "Pelo", "Pantalones", "Calzado", "Accesorios"};
    private int categoriaActual = 0;

    // Tamaños
    private final float ANCHO_INVENTARIO = 600f;
    private final float ALTO_INVENTARIO = 400f;
    private final float TAMANO_SLOT = 60f;
    private final int FILAS = 5;
    private final int COLUMNAS = 4;

    public Inventario(Skin skin, Chat chat, Jugador jugador) { // Añadido parámetro Chat
        this.skin = skin;
        this.chat = chat;
        this.jugador = jugador;
        this.visible = false;
        this.escenarioInventario = new Stage(new ScreenViewport());
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();

        // Crear UI
        crearInterfaz();
    }

    private void crearInterfaz() {
        // Tabla principal (centrado y fill parent)
        tablaPrincipal = new Table();
        tablaPrincipal.setFillParent(true);
        tablaPrincipal.center().pad(50f); // Padding para centrar en pantalla

        // Pestañas de categorías (fila horizontal arriba)
        tablaCategorias = new Table(skin);
        tablaCategorias.top();
        for (int i = 0; i < categorias.length; i++) {
            final int index = i;
            TextButton botonCategoria = new TextButton(categorias[i], skin, "default"); // Usa estilo default del skin
            botonCategoria.addListener(event -> {
                categoriaActual = index;
                actualizarSlots();
                return true;
            });
            tablaCategorias.add(botonCategoria).pad(5f).expandX().fillX();
        }
        // Añadir tabla de categorías a principal (una sola fila)
        tablaPrincipal.add(tablaCategorias).colspan(COLUMNAS).expandX().fillX().row();

        // Cuadrícula de slots
        tablaSlots = new Table(skin);
        tablaSlots.center();
        actualizarSlots();

        // Añadir slots a la tabla principal
        tablaPrincipal.add(tablaSlots).expand().fill().row();

        // Botón de cerrar (arriba derecha, en una celda separada)
        TextButton botonCerrar = new TextButton("X", skin, "default");
        botonCerrar.addListener(event -> {
            cerrarInventario();
            return true;
        });
        // Añadir en una nueva row para no superponer
        tablaPrincipal.add(botonCerrar).top().right().pad(10f).size(40f, 40f).colspan(COLUMNAS);

        escenarioInventario.addActor(tablaPrincipal);
    }

    private void actualizarSlots() {
        tablaSlots.clear();
        // Crear slots vacíos (placeholders con texto "Slot" y fondo del skin)
        Drawable fondoSlot = skin.getDrawable("white"); // Drawable blanco del skin uiskin.json (ajusta si no existe)
        if (fondoSlot == null) {
            fondoSlot = skin.newDrawable("white", Color.GRAY); // Fallback a gris
        }

        for (int fila = 0; fila < FILAS; fila++) {
            Table filaSlots = new Table(); // Subtabla para fila horizontal
            for (int col = 0; col < COLUMNAS; col++) {
                TextButton slot = new TextButton("Slot", skin, "default"); // Usa TextButton como placeholder
                slot.getLabel().setColor(Color.GRAY); // Color gris para vacío
                slot.getStyle().up = skin.newDrawable(fondoSlot, Color.LIGHT_GRAY); // Fondo gris
                slot.setSize(TAMANO_SLOT, TAMANO_SLOT);
                // Aquí agregar listeners para ítems en el futuro
                filaSlots.add(slot).pad(5f).size(TAMANO_SLOT, TAMANO_SLOT);
            }
            tablaSlots.add(filaSlots).expandX().fillX().row();
        }

        // Título de categoría actual (debajo de slots)
        TextButton titulo = new TextButton("Categoría: " + categorias[categoriaActual], skin, "default");
        titulo.setColor(Color.WHITE);
        tablaSlots.add(titulo).colspan(COLUMNAS).expandX().fillX().row();
    }

    public void actualizar(float delta) {
        if (visible) {
            escenarioInventario.act(delta);

            // Cerrar con 'E' o clic en botón (ya manejado en listener)
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                cerrarInventario();
            }
        } else {
            // Abrir con 'E' solo si chat no está visible
            if (Gdx.input.isKeyJustPressed(Input.Keys.E) && !chat.isChatVisible()) { // Usa el método de tu Chat
                abrirInventario();
            }
        }
    }

    public void render() {
        if (visible) {
            // Fondo semitransparente
            shapeRenderer.setProjectionMatrix(escenarioInventario.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();

            // Dibujar UI
            escenarioInventario.draw();
        }
    }

    private void abrirInventario() {
        visible = true;
        Gdx.input.setInputProcessor(escenarioInventario);
        if (jugador != null) {
            if (velBackup == null) velBackup = jugador.getVelPx(); // guardar
            jugador.setVelPx(0f);       // bloquear movimiento por velocidad
            jugador.cancelarMovimiento();// que quede quieto YA
        }
    }

    private void cerrarInventario() {
        visible = false;
        Gdx.input.setInputProcessor(null);
        if (jugador != null && velBackup != null) {
            jugador.setVelPx(velBackup);
            velBackup = null;
        }
    }


    public void resize(int width, int height) {
        escenarioInventario.getViewport().update(width, height, true);
    }

    public void dispose() {
        if (escenarioInventario != null) escenarioInventario.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setInputProcessor() {
        if (visible) {
            Gdx.input.setInputProcessor(escenarioInventario);
        }
    }
}


