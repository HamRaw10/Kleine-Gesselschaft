package pantalla;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.some_example_name.Main;

public class PantallaMenu implements Screen {

    private Game juego;
    private Stage escenario;
    private Skin skin;
    private boolean muted = false; // estado del sonido

    public PantallaMenu(Game juego) {
        this.juego = juego;

        escenario = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(escenario);

        // Cargar skin básica (para compatibilidad)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Cargar texturas de los botones
        Texture botonJugarTex = new Texture(Gdx.files.internal("menu/play.png"));
        Texture botonConfiguracionTex = new Texture(Gdx.files.internal("menu/setting.png"));
        Texture botonCreditosTex = new Texture(Gdx.files.internal("menu/credits.png"));
        Texture sonidoSiTex = new Texture(Gdx.files.internal("menu/sonidosi.png"));
        Texture sonidoNoTex = new Texture(Gdx.files.internal("menu/sonidono.png"));
        Texture logoTex = new Texture(Gdx.files.internal("logo/STUDIO_1_-removebg-preview.png"));

        // Crear botones con imágenes
        ImageButton botonJugar = new ImageButton(new TextureRegionDrawable(new TextureRegion(botonJugarTex)));
        botonJugar.getImage().setScaling(Scaling.stretch);

        ImageButton botonConfiguracion = new ImageButton(new TextureRegionDrawable(new TextureRegion(botonConfiguracionTex)));
        botonConfiguracion.getImage().setScaling(Scaling.stretch);

        ImageButton botonCreditos = new ImageButton(new TextureRegionDrawable(new TextureRegion(botonCreditosTex)));
        botonCreditos.getImage().setScaling(Scaling.stretch);

        ImageButton botonSonido = new ImageButton(new TextureRegionDrawable(new TextureRegion(sonidoSiTex)));
        botonSonido.getImage().setScaling(Scaling.stretch);

        // Crear logo
        Image logo = new Image(logoTex);

        // Tabla principal para los botones centrales
        Table tablaPrincipal = new Table();
        tablaPrincipal.setFillParent(true);
        tablaPrincipal.top().center();

        // Añadir logo
        tablaPrincipal.add(logo).pad(20).colspan(3);
        tablaPrincipal.row();

        // Añadir botones principales
        tablaPrincipal.add(botonConfiguracion).size(100, 100).pad(15);
        tablaPrincipal.add(botonJugar).size(100, 100).pad(15);
        tablaPrincipal.add(botonCreditos).size(100, 100).pad(15);

        // Tabla para el botón de sonido (esquina superior izquierda)
        Table tablaSonido = new Table();
        tablaSonido.setFillParent(true);
        tablaSonido.top().left();
        tablaSonido.add(botonSonido).size(60, 60).pad(10);

        // Añadir tablas al escenario
        escenario.addActor(tablaPrincipal);
        escenario.addActor(tablaSonido);

        // Listeners de los botones (MANTENIENDO LA FUNCIONALIDAD CORRECTA)

        // Botón Jugar - FUNCIONA BIEN
        botonJugar.addListener(event -> {
            if (botonJugar.isPressed()) {
                juego.setScreen(new PantallaCarga(juego, ((Main) juego).assets));
            }
            return true;
        });

        // Botón Sonido - toggle mute/unmute
        botonSonido.addListener(event -> {
            if (botonSonido.isPressed()) {
                muted = !muted;
                if (muted) {
                    botonSonido.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(sonidoNoTex));
                } else {
                    botonSonido.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(sonidoSiTex));
                }
            }
            return true;
        });

        // Botón Configuración
        botonConfiguracion.addListener(event -> {
            if (botonConfiguracion.isPressed()) {
                System.out.println("Abrir configuración...");
                // juego.setScreen(new PantallaConfiguracion(juego));
            }
            return true;
        });

        // Botón Créditos
        botonCreditos.addListener(event -> {
            if (botonCreditos.isPressed()) {
                System.out.println("Abrir créditos...");
                // juego.setScreen(new PantallaCreditos(juego));
            }
            return true;
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        escenario.act(delta);
        escenario.draw();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        escenario.clear();
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {
        escenario.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        escenario.dispose();
        skin.dispose();
        // Nota: Las texturas se deben disponer en sus respectivas pantallas
        // o usar AssetManager para un manejo centralizado
    }
}
