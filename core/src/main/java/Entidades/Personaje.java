package Entidades;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

import Utilidades.Animacion;
import Utilidades.utiles;

public abstract class Personaje {
    protected float personajeX;
    protected float personajeY;
    protected float velocidad;//Variable creada, la cual va a almacenar la velocidad en la que se mueva el ciruclo
    protected Texture texture;
    protected float escala;
    protected float ancho;
    protected float largo;

    public Personaje(String texturaDelPersonaje, float personajeX, float personajeY, float escala){
        this.texture = new Texture(texturaDelPersonaje);
        this.personajeY = personajeY;
        this.personajeX = personajeX;
        this.escala = escala;


    }

    public void limitesDelPersonaje(){
        float minX = texture.getWidth() / 2f;//
        float maxX = Gdx.graphics.getWidth() - minX;//
        float minY = texture.getHeight() / 2f;//
        float maxY = Gdx.graphics.getHeight() - minY;//
        personajeX = MathUtils.clamp(personajeX, minX, maxX);//
        personajeY = MathUtils.clamp(personajeY, minY, maxY);//
    }

    public void actualizar(float delta, float targetX, float targetY) {

    }

    public Texture getTexture() {
        return texture;
    }
    public float getPersonajeX() {
        return personajeX - (texture.getWidth() * escala / 2f);
    }
    public float getPersonajeY() {
        return personajeY - (texture.getHeight() * escala / 2f);
    }
    public float getWidth() {
        return texture.getWidth() * escala;
    }
    public float getHeight() {
        return texture.getHeight() * escala;
    }

    public void dispose() {
        texture.dispose();
    }

    public abstract void render(SpriteBatch batch);
}
