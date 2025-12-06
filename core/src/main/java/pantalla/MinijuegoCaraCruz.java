package pantalla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import entidades.Jugador;
import utilidades.Moneda;
import utilidades.MenuMinijuegos;

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
    private Label lblPuntos;
    private int puntosPendientes = 0;
    private boolean finalizado = false;
    private final MenuMinijuegos.MinijuegoFinListener finListener;
    private int rachaGanadas = 0;
    private Boolean eleccionActual = null; // true = cara, false = cruz
    private int apuestaActual = 0;

    public MinijuegoCaraCruz(Jugador jugador, Skin skin, MenuMinijuegos.MinijuegoFinListener finListener) {
        this.jugador = jugador;
        this.skin = skin;
        this.finListener = finListener;
        this.batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Cargar texturas (solo cara y cruz)
        // Assets ubicados en assets/images_carasec/
        String basePath = "images_carasec/";
        monedaCara = new Texture(basePath + "moneda_cara.png");
        monedaCruz = new Texture(basePath + "moneda_cruz.png");
        TextureRegion[] framesGiro = {
            new TextureRegion(monedaCara),
            new TextureRegion(monedaCruz)
        };
        animGiro = new Animation<>(0.1f, framesGiro);

        // UI: Botones para elegir
        ImageButton btnCara = new ImageButton(new TextureRegionDrawable(new TextureRegion(monedaCara)));
        ImageButton btnCruz = new ImageButton(new TextureRegionDrawable(new TextureRegion(monedaCruz)));
        lblResultado = new Label("", skin);
        lblPuntos = new Label("Puntos acumulados: 0", skin);
        Label lblSeleccion = new Label("Selecciona Cara o Cruz y luego presiona Jugar", skin);
        TextButton btnSalir = new TextButton("Salir", skin);
        TextButton btnJugar = new TextButton("Jugar", skin);

        Table table = new Table(skin);
        table.setFillParent(true);
        table.center();
        table.add(new Label("Elige: Cara o Cruz (Apuesta: 10 monedas)", skin)).colspan(2).row();
        table.add(btnCara).size(100).pad(10);
        table.add(btnCruz).size(100).pad(10).row();
        table.add(lblSeleccion).colspan(2).padTop(4f).row();
        table.add(btnJugar).colspan(2).size(160f, 48f).padTop(6f).row();
        table.add(lblResultado).colspan(2).row();
        table.add(lblPuntos).colspan(2).padTop(6f).row();
        table.add(btnSalir).colspan(2).size(160f, 48f).padTop(8f);
        stage.addActor(table);

        // Listeners
        btnCara.addListener(event -> {
            eleccionActual = true;
            lblSeleccion.setText("Seleccionaste: Cara");
            return true;
        });
        btnCruz.addListener(event -> {
            eleccionActual = false;
            lblSeleccion.setText("Seleccionaste: Cruz");
            return true;
        });
        btnJugar.addListener(event -> {
            if (btnJugar.isPressed()) {
                if (eleccionActual == null) {
                    lblResultado.setText("Elige Cara o Cruz antes de jugar.");
                } else if (!animando) {
                    iniciarJuego(eleccionActual);
                }
            }
            return true;
        });
        btnSalir.addListener(event -> {
            if (btnSalir.isPressed()) salir();
            return true;
        });
    }

    private void iniciarJuego(boolean eleccionCara) {
        Moneda dinero = jugador.getDinero();
        if (!dinero.restar(10)) {
            lblResultado.setText("No tienes suficientes monedas!");
            return;
        }
        apuestaActual = 10;
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            salir();
        }
    }

    private void mostrarResultado() {
        TextureRegion resultadoTex = resultadoCara ? new TextureRegion(monedaCara) : new TextureRegion(monedaCruz);
        batch.begin();
        batch.draw(resultadoTex, Gdx.graphics.getWidth() / 2 - 50, Gdx.graphics.getHeight() / 2 - 50, 100, 100);
        batch.end();

        if (resultadoCara) {
            rachaGanadas++;
            int recompensa = 30 + 10 * Math.max(0, rachaGanadas - 1); // +30 base y crece por racha
            // Se devuelve la apuesta + la recompensa para que el neto refleje la ganancia completa
            puntosPendientes += recompensa + apuestaActual;
            lblResultado.setText("¡Ganaste! Cara. +" + recompensa + " (se devuelve apuesta " + apuestaActual + ") - racha " + rachaGanadas);
        } else {
            rachaGanadas = 0;
            lblResultado.setText("Perdiste. Cruz.");
        }
        lblPuntos.setText("Puntos acumulados: " + puntosPendientes);
        apuestaActual = 0;
    }

    private void salir() {
        if (finalizado) return;
        finalizado = true;
        if (finListener != null) finListener.onFin(puntosPendientes);
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        monedaCara.dispose();
        monedaCruz.dispose();
    }
}
