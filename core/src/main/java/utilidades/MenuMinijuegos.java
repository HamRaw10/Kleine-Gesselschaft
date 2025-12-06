package utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuMinijuegos {

    public enum Opcion { CARA_O_CRUZ, CRAPS }

    public interface SeleccionListener {
        void onElegir(Opcion opcion);
        void onCerrar();
    }

    public interface MinijuegoFinListener {
        void onFin(int puntosObtenidos);
    }

    private final Stage stage;
    private final Skin skin;
    private final SeleccionListener listener;
    private boolean abierto = false;

    public MenuMinijuegos(Skin skin, SeleccionListener listener) {
        this.skin = skin;
        this.listener = listener;
        this.stage = new Stage(new ScreenViewport());
        construirUI();
    }

    private void construirUI() {
        Table root = new Table(skin);
        root.setFillParent(true);
        root.center().pad(20f);

        TextButton btnCaraCruz = new TextButton("Cara o Cruz", skin);
        TextButton btnCraps = new TextButton("Craps", skin);
        TextButton btnCerrar = new TextButton("Salir", skin);

        btnCaraCruz.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (listener != null) listener.onElegir(Opcion.CARA_O_CRUZ);
            }
        });
        btnCraps.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (listener != null) listener.onElegir(Opcion.CRAPS);
            }
        });
        btnCerrar.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (listener != null) listener.onCerrar();
            }
        });

        root.add("Selecciona un minijuego").colspan(2).padBottom(12f).row();
        root.add(btnCaraCruz).width(220f).height(60f).pad(6f);
        root.row();
        root.add(btnCraps).width(220f).height(60f).pad(6f);
        root.row();
        root.add(btnCerrar).width(220f).height(50f).padTop(14f);

        stage.addActor(root);
    }

    public void mostrar() {
        if (abierto) return;
        abierto = true;
        Gdx.input.setInputProcessor(stage);
    }

    public void ocultar() { abierto = false; }

    public boolean estaAbierto() { return abierto; }

    public void render(float delta) {
        if (!abierto) return;
        stage.act(delta);
        stage.draw();
    }

    public Stage getStage() { return stage; }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() { stage.dispose(); }
}
