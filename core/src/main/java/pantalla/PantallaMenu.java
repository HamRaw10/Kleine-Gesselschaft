package pantalla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.some_example_name.Main;

public class PantallaMenu implements Screen {

    private Game juego;
    private Stage escenario;
    private Skin skin;

    public PantallaMenu(Game juego) {
        this.juego = juego;

        escenario = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(escenario);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextButton botonJugar = new TextButton("Jugar", skin);

        botonJugar.addListener(event -> {
            if (botonJugar.isPressed()) {
                juego.setScreen(new PantallaCarga(juego, ((Main)juego).assets));
            }
            return true;
        });

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();

        tabla.add(botonJugar).pad(20);

        escenario.addActor(tabla);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        escenario.act(delta);
        escenario.draw();
    }

    @Override public void hide() {
        // Limpieza crítica cuando la pantalla se oculta
        Gdx.input.setInputProcessor(null);
        escenario.clear(); // Elimina todos los actores
    }
    // Los otros métodos pueden estar vacíos o manejar recursos:
    @Override public void show() {}
    @Override public void resize(int width, int height) {
        escenario.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}

    @Override public void dispose() {
        escenario.dispose();
        skin.dispose();
    }
}
