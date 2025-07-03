package io.github.some_example_name;

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
    ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private Texture personajeTexture;

    float circleX = 200;
    float circleY = 100;
    float circleRadius = 75;
    float velocidad = 0.03f;//Variable creada, la cual va a almacenar la velocidad en la que se mueva el ciruclo

    @Override
    public void create() {
        batch = new SpriteBatch();
        personajeTexture = new Texture("personaje.png");
        batch.enableBlending();
        personajeTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Gdx.graphics.setVSync(true); // Evita parpadeos
    }



    @Override
    public void render() {
        Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();
        boolean seEstamoviendo = false;
        if(Gdx.input.isTouched()){//Para el mouse

            float targetX = Gdx.input.getX();//Variable para indicar que pertenece a x
            float targetY = Gdx.graphics.getHeight() - Gdx.input.getY();//Variable para indicar que pertenece a Y
            circleX += (targetX - circleX) * velocidad * Gdx.graphics.getDeltaTime() * 60;;//Variable del circulo en el eje x donde indica que el circulo se va a dirigir donde vaya el mouse
            circleY += (targetY - circleY) * velocidad * Gdx.graphics.getDeltaTime() * 60;;//Variable del circulo en el eje y donde indica que el circulo se va a dirigir donde vaya el mouse
            if (Math.abs(targetX - circleX) < 0.5f) circleX = targetX;
            if (Math.abs(targetY - circleY) < 0.5f) circleY = targetY;
            seEstamoviendo = true;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            circleY += velocidad * delta;
            seEstamoviendo = true;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.S)){
            circleY -= velocidad * delta;;
            seEstamoviendo = true;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            circleX -= velocidad * delta;
            seEstamoviendo = true;
        }else if(Gdx.input.isKeyPressed(Input.Keys.D)){
            circleX += velocidad * delta;
        }



        float minX = personajeTexture.getWidth()/2f;//
        float maxX = Gdx.graphics.getWidth() - minX;//
        float minY = personajeTexture.getHeight()/2f;//
        float maxY = Gdx.graphics.getHeight() - minY;//

        circleX = MathUtils.clamp(circleX, minX, maxX);//
        circleY = MathUtils.clamp(circleY, minY, maxY);//

        // En render(), ANTES de batch.begin():
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





