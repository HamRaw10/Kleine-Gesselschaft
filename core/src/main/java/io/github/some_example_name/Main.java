package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private Texture image;

    float circleX = 200;
    float circleY = 100;
    float circleRadius = 75;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
    }



    @Override
    public void render() {

        if(Gdx.input.isTouched()){//Para el mouse
            float velocidad = 0.03f;//Variable creada, la cual va a almacenar la velocidad en la que se mueva el ciruclo
            float targetX = Gdx.input.getX();//Variable para indicar que pertenece a x
            float targetY = Gdx.graphics.getHeight() - Gdx.input.getY();//Variable para indicar que pertenece a Y
            circleX += (targetX - circleX) * velocidad * Gdx.graphics.getDeltaTime() * 60;;//Variable del circulo en el eje x donde indica que el circulo se va a dirigir donde vaya el mouse
            circleY += (targetY - circleY) * velocidad * Gdx.graphics.getDeltaTime() * 60;;//Variable del circulo en el eje y donde indica que el circulo se va a dirigir donde vaya el mouse
            if (Math.abs(targetX - circleX) < 0.5f) circleX = targetX;
            if (Math.abs(targetY - circleY) < 0.5f) circleY = targetY;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            circleY++;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.S)){
            circleY--;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            circleX--;
        }else if(Gdx.input.isKeyPressed(Input.Keys.D)){
            circleX++;
        }

        float minX = circleRadius;//
        float maxX = Gdx.graphics.getWidth() - circleRadius;//
        float minY = circleRadius;//
        float maxY = Gdx.graphics.getHeight() - circleRadius;//

        circleX = Math.min(Math.max(circleX, minX), maxX);//
        circleY = Math.min(Math.max(circleY, minY), maxY);//

        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.circle(circleX, circleY, 75);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}









