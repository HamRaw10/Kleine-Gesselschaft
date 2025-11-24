package pantalla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import entidades.Jugador;
import utilidades.Moneda;

public class MinijuegoCaraCruz extends ScreenAdapter {
    private Stage stage;
    private Skin skin;
    private Jugador jugador;
    private SpriteBatch batch;
    private Texture monedaCara, monedaCruz;
    private Animation<TextureRegion> animGiro;
    private float tiempoAnimacion;
    private boolean animando = false;
    private boolean resultadoCara;
    private Label lblResultado;

    public MinijuegoCaraCruz(Jugador jugador, Skin skin) {
        this.jugador = jugador;
        this.skin = skin;
        this.batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Cargar texturas (asegúrate de tener "moneda_cara.png", "moneda_cruz.png", "moneda_giro1.png" a "moneda_giro4.png")
        monedaCara = new Texture("moneda_cara.png");
        monedaCruz = new Texture("moneda_cruz.png");
        TextureRegion[] framesGiro = {
            new TextureRegion(new Texture("moneda_giro1.png")),
            new TextureRegion(new Texture("moneda_giro2.png")),
            new TextureRegion(new Texture("moneda_giro3.png")),
            new TextureRegion(new Texture("moneda_giro4.png"))
        };
        animGiro = new Animation<>(0.1f, framesGiro);

        // UI: Botones para elegir
        ImageButton btnCara = new ImageButton(new TextureRegionDrawable(new TextureRegion(monedaCara)));
        ImageButton btnCruz = new ImageButton(new TextureRegionDrawable(new TextureRegion(monedaCruz)));
        lblResultado = new Label("", skin);

        Table table = new Table(skin);
        table.setFillParent(true);
        table.center();
        table.add(new Label("Elige: Cara o Cruz (Apuesta: 10 monedas)", skin)).colspan(2).row();
        table.add(btnCara).size(100).pad(10);
        table.add(btnCruz).size(100).pad(10).row();
        table.add(lblResultado).colspan(2).row();
        stage.addActor(table);

        // Listeners
        btnCara.addListener(event -> {
            if (!animando) iniciarJuego(true); // Cara
            return true;
        });
        btnCruz.addListener(event -> {
            if (!animando) iniciarJuego(false); // Cruz
            return true;
        });
    }

    private void iniciarJuego(boolean eleccionCara) {
        Moneda dinero = jugador.getDinero();
        if (!dinero.restar(10)) {
            lblResultado.setText("No tienes suficientes monedas!");
            return;
        }
        animando = true;
        tiempoAnimacion = 0;
        // Simular resultado aleatorio
        resultadoCara = Math.random() < 0.5f;
        // Después de animación, mostrar resultado
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0.5f); // Fondo semi-transparente
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (animando) {
            tiempoAnimacion += delta;
            batch.begin();
            TextureRegion frame = animGiro.getKeyFrame(tiempoAnimacion, true);
            batch.draw(frame, Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 - 50, 100, 100);
            batch.end();

            if (tiempoAnimacion > 1f) { // Fin de animación
                animando = false;
                mostrarResultado();
            }
        }
    }

    private void mostrarResultado() {
        TextureRegion resultadoTex = resultadoCara ? new TextureRegion(monedaCara) : new TextureRegion(monedaCruz);
        batch.begin();
        batch.draw(resultadoTex, Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 - 50, 100, 100);
        batch.end();

        // Lógica de ganar/perder
        Moneda dinero = jugador.getDinero();
        if (resultadoCara) {
            dinero.sumar(20); // Doble
            lblResultado.setText("¡Ganaste! Cara. +20 monedas");
        } else {
            lblResultado.setText("Perdiste. Cruz.");
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        monedaCara.dispose();
        monedaCruz.dispose();
    }
}
