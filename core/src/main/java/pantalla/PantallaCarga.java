package pantalla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class PantallaCarga implements Screen {

    imagen fondo;
    spritebatch b;
    boolean fadeInTerminado = false;
    float a = 0;
    float contTiempo = 0, tiempoEspera = 5;

    @Override
    public void show(){
        fondo = new Imagen();
    }
}

