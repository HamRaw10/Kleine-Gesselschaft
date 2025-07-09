package objetos;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public abstract class Objeto{
    protected float mapaX;
    protected float mapaY;
    protected float velocidad;//Variable creada, la cual va a almacenar la velocidad en la que se mueva el ciruclo
    protected Texture texture;
    protected float escala;
    protected float ancho;
    protected float largo;

    public Objeto(String texturaDelObjeto, float mapaX, float mapaY, float escala){
        this.texture = new Texture(texturaDelObjeto);
        this.mapaY = mapaY;
        this.mapaX = mapaX;
        this.escala = escala;


    }


    public void limitesDelPersonaje(){
        float minX = texture.getWidth() / 2f;//
        float maxX = Gdx.graphics.getWidth() - minX;//
        float minY = texture.getHeight() / 2f;//
        float maxY = Gdx.graphics.getHeight() - minY;//
        mapaX = MathUtils.clamp(mapaX, minX, maxX);//
        mapaY = MathUtils.clamp(mapaY, minY, maxY);//
    }

    public Texture getTexture() {

        return texture;
    }
    public float getMapaX() {

        return mapaX - (texture.getWidth() * escala / 2f);
    }
    public float getMapaY() {
        return mapaY - (texture.getHeight() * escala / 2f);
    }
    public float getWidth() {

        return texture.getWidth() * escala;
    }
    public float getHeight() {

        return texture.getHeight() * escala;
    }

    public abstract void actualizar(float delta, float targetX, float targetY);

    public abstract void render(SpriteBatch batch);

    public void dispose() {

        texture.dispose();
    }

}
