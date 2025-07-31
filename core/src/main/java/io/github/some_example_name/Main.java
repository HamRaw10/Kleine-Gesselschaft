package io.github.some_example_name;
import Controles.controlDelJuego;
import Utilidades.Render;
import pantalla.PantallaMenu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;




/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public AssetManager assets;

    @Override
    public void create() {
        assets = new AssetManager();
        this.setScreen(new PantallaMenu(this));
    }

}





