package io.github.some_example_name;
import Entidades.personaje;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private personaje personajeJugador;
    private SpriteBatch batch;
    private Texture personajeTexture;

    float circleX = 200;
    float circleY = 100;

    float velocidad = 0.03f;//Variable creada, la cual va a almacenar la velocidad en la que se mueva el ciruclo

    @Override
    public void create() {
        personajeJugador.cargarPersonaje();
    }




    public void render() {
        Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();

        personajeJugador.dibujarPersonaje();
        personajeJugador.moverPersonaje();
        personajeJugador.limitesPersonajeEnPantalla();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.begin();
        batch.setColor(1, 1, 1, 1);
        float escala = 0.5f;
        batch.draw(personajeTexture,
            circleX - (personajeTexture.getWidth()*escala)/2f,
            circleY - (personajeTexture.getHeight()*escala)/2f,
            personajeTexture.getWidth()*escala,
            personajeTexture.getHeight()*escala);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        personajeTexture.dispose();
    }
}





