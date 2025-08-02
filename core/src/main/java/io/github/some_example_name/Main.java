package io.github.some_example_name;
import pantalla.PantallaMenu;

import com.badlogic.gdx.Game;
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





