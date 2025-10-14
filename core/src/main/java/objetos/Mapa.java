package objetos;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Rectangle;
import java.util.HashMap;
import java.lang.String;

import pantalla.PantallaJuego;
import utilidades.Chat;

public class Mapa extends Objeto {
    private final Texture mapaCompleto;
    private boolean expandido;
    private final Rectangle bounds;
    private float tiempoExpandido;
    private final float TIEMPO_TRANSICION = 0.3f;
    private final Texture botonCerrar;
    private final Rectangle boundsBotonCerrar;
    private final float TAMANO_BOTON = 50f;
    private boolean bloqueoInput;

    private PantallaJuego pantallaJuego;  // NUEVO: Referencia para callback

    public boolean isInputBloqueado() {
        return bloqueoInput;
    }

    private HashMap<String, Rectangle> regionesClickeables; // les añade direccion y lugar a las regiones
    private HashMap<String, String> regionAEscenario;   // les añade escenarios a las regiones

    public Mapa() {  // NUEVO: Constructor modificado
        super("mapa.png", Gdx.graphics.getWidth() - 120, 70, 0.2f);
        this.mapaCompleto = new Texture("mapanuevo.png");
        this.botonCerrar = new Texture("botonCerrar.png"); // Necesitarás esta textura
        this.bounds = new Rectangle(getMapaX(), getMapaY(), getWidth(), getHeight()); // Ahora acepta floats
        this.boundsBotonCerrar = new Rectangle();
        this.pantallaJuego = pantallaJuego;
        this.expandido = false;

        this.regionesClickeables = new HashMap<>();
        this.regionAEscenario = new HashMap<>();


        aniadirRegionesClickeables("compras", 95,205,80,80, "compras");  // Ajusta nombres si quieres
        aniadirRegionesClickeables("centro", 270,170,60,80, "centro");
        aniadirRegionesClickeables("Eduactivo", 215,75,70,60, "Eduactivo");
        aniadirRegionesClickeables("entretenimiento", 400,95,130,130, "entretenimiento");
        aniadirRegionesClickeables("van", 20,355,100,80, "van");
    }

    private void aniadirRegionesClickeables(String nombreRegion, float x, float y, float largo, float alto, String nombreEscenario) {
        regionesClickeables.put(nombreRegion, new Rectangle(x, y, largo, alto));
        regionAEscenario.put(nombreRegion, nombreEscenario);
    }
    @Override
    public void actualizar(float delta, float targetX, float targetY) {
        bounds.set(getMapaX(), getMapaY(), getWidth(), getHeight());


        // Solo procesar inputs si no están bloqueados
        if (!bloqueoInput) {
            // Procesar clic en el mapa pequeño solo si NO está expandido
            if (!expandido && Gdx.input.justTouched() &&
                bounds.contains(targetX, targetY)) {
                toggleExpandido();
            }

            // Procesar botón de cerrar solo cuando está expandido
            if (expandido) {
                float botonX = Gdx.graphics.getWidth() - TAMANO_BOTON - 20;
                float botonY = 20;
                boundsBotonCerrar.set(botonX, botonY, TAMANO_BOTON, TAMANO_BOTON);
                chequearRegionesClickeadas(targetX, targetY);

                if (Gdx.input.justTouched() && boundsBotonCerrar.contains(targetX, targetY)) {
                    toggleExpandido();
                    bloqueoInput = true; // Bloquear inputs temporalmente
                }
                else{
                    chequearRegionesClickeadas(targetX, targetY);
                }
            }
        }

        // Restaurar inputs cuando la animación de cierre esté completa
        if (bloqueoInput && !expandido && tiempoExpandido <= 0) {
            bloqueoInput = false;
        }

        // Control de entrada (clic o tecla M)
        if ((Gdx.input.justTouched() && bounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()))){
            toggleExpandido();
        }

        if (!expandido && Gdx.input.justTouched() &&
            bounds.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
            toggleExpandido();
        }


        if (!Chat.estaEscribiendo() && Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            toggleExpandido();
        }


        // Actualizar tiempo de transición
        if (expandido && tiempoExpandido < TIEMPO_TRANSICION) {
            tiempoExpandido += delta;
        } else if (!expandido && tiempoExpandido > 0) {
            tiempoExpandido -= delta;
        }
    }

    private void chequearRegionesClickeadas(float targetX, float targetY) {
    }

    /*
        private void chequearRegionesClickeadas(float x, float y) {
            for (String regionName : regionesClickeables.keySet()) {
                Rectangle region = regionesClickeables.get(regionName);
                if (region.contains(x, y)) {
                    String escenarioNuevo = regionAEscenario.get(regionName);  // Nombre del TMX sin .tmx
                    cambioEscenario(escenarioNuevo);
                    return;
                }
            }
        }
        */
/*
    private void cambioEscenario(String escenarioNuevo) {
        System.out.println("Changing to scenario: " + escenarioNuevo);
        toggleExpandido();
        bloqueoInput = true;
        // NUEVO: Llamar al callback en PantallaJuego
        if (pantallaJuego != null) {
            pantallaJuego.cambiarEscenario(escenarioNuevo);
        }
    }
*/
    @Override
    public void render(SpriteBatch batch) {
        if (expandido || tiempoExpandido > 0) {
            // Calcular progreso de la animación (0 a 1)
            float progreso = expandido ?
                Math.min(1, tiempoExpandido/TIEMPO_TRANSICION) :
                Math.max(0, 1 - (tiempoExpandido/TIEMPO_TRANSICION));

            // Dibujar fondo semitransparente
            batch.setColor(1, 1, 1, progreso * 0.7f);
            batch.draw(mapaCompleto,
                0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

            // Dibujar botón de cerrar (con la misma transición)
            if (expandido || tiempoExpandido > 0) {
                batch.setColor(1, 1, 1, progreso);
                batch.draw(botonCerrar,
                    boundsBotonCerrar.x, boundsBotonCerrar.y,
                    boundsBotonCerrar.width, boundsBotonCerrar.height);
            }

            // Restaurar color
            batch.setColor(1, 1, 1, 1);
        }

        // Dibujar mini mapa (con transición de opacidad si está expandiéndose)
        if (!expandido || tiempoExpandido < TIEMPO_TRANSICION) {
            float opacidad = expandido ? 1 - (tiempoExpandido/TIEMPO_TRANSICION) : 1;
            batch.setColor(1, 1, 1, opacidad);
            batch.draw(texture, getMapaX(), getMapaY(), getWidth(), getHeight());
            batch.setColor(1, 1, 1, 1);
        }
    }

    public void toggleExpandido() {
        expandido = !expandido;
    }

    public boolean isExpandido() {
        return expandido;
    }

    @Override
    public void dispose() {
        super.dispose();
        mapaCompleto.dispose();
    }
}
