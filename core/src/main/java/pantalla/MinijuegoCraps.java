package pantalla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import entidades.Jugador;
import utilidades.Moneda;

public class MinijuegoCraps extends ScreenAdapter {
    private Stage stage;
    private Skin skin;
    private Jugador jugador;
    private SpriteBatch batch;
    private Texture[] dados = new Texture[6]; // dado1.png a dado6.png
    private int dado1, dado2, punto = 0;
    private boolean primeraTirada = true;
    private Label lblEstado, lblDados;

    public MinijuegoCraps(Jugador jugador, Skin skin) {
        this.jugador = jugador;
        this.skin = skin;
        this.batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Cargar dados
        for (int i = 0; i < 6; i++) {
            dados[i] = new Texture("dado" + (i + 1) + ".png");
        }

        lblEstado = new Label("Primera tirada. Apuesta: 10 monedas", skin);
        lblDados = new Label("", skin);
        TextButton btnTirar = new TextButton("Tirar Dados", skin);
        btnTirar.addListener(event -> tirarDados());

        Table table = new Table(skin);
        table.setFillParent(true);
        table.center();
        table.add(lblEstado).row();
        table.add(lblDados).row();
        table.add(btnTirar).size(200, 50).row();
        stage.addActor(table);
    }

    private boolean tirarDados() {
        Moneda dinero = jugador.getDinero();
        if (primeraTirada && !dinero.restar(10)) {
            lblEstado.setText("No tienes suficientes monedas!");
            return false;
        }

        dado1 = (int) (Math.random() * 6) + 1;
        dado2 = (int) (Math.random() * 6) + 1;
        int suma = dado1 + dado2;
        lblDados.setText("Dado1: " + dado1 + " Dado2: " + dado2 + " Suma: " + suma);

        if (primeraTirada) {
            if (suma == 7 || suma == 11) {
                dinero.sumar(20);
                lblEstado.setText("¡Ganaste! +20 monedas");
                primeraTirada = true; // Reiniciar
            } else if (suma == 2 || suma == 3 || suma == 12) {
                lblEstado.setText("Perdiste.");
                primeraTirada = true;
            } else {
                punto = suma;
                lblEstado.setText("Punto establecido: " + punto + ". Tira de nuevo.");
                primeraTirada = false;
            }
        } else {
            if (suma == punto) {
                dinero.sumar(20);
                lblEstado.setText("¡Ganaste el punto! +20 monedas");
                primeraTirada = true;
            } else if (suma == 7) {
                lblEstado.setText("Perdiste (sacaste 7).");
                primeraTirada = true;
            } else {
                lblEstado.setText("Sigue tirando. Punto: " + punto);
            }
        }
        return false;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        // Dibujar dados si hay resultado
        if (dado1 > 0) {
            batch.begin();
            batch.draw(new TextureRegion(dados[dado1 - 1]), 200, 300, 100, 100);
            batch.draw(new TextureRegion(dados[dado2 - 1]), 350, 300, 100, 100);
            batch.end();
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        for (Texture t : dados) t.dispose();
    }
}
