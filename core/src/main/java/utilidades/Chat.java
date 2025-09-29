    package utilidades;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.Input;
    import com.badlogic.gdx.graphics.Color;
    import com.badlogic.gdx.graphics.g2d.BitmapFont;
    import com.badlogic.gdx.graphics.g2d.GlyphLayout;
    import com.badlogic.gdx.graphics.g2d.SpriteBatch;
    import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
    import com.badlogic.gdx.scenes.scene2d.Stage;
    import com.badlogic.gdx.scenes.scene2d.ui.TextField;
    import com.badlogic.gdx.scenes.scene2d.ui.Skin;

    import entidades.Personaje;

    public class Chat {
        private Stage escenarioChat;
        private static TextField campoTexto;
        private boolean chatVisible;
        private BitmapFont fuente;
        private SpriteBatch batch;
        private ShapeRenderer shapeRenderer;

        // Para los globos de chat
        private String mensajeActual;
        private float tiempoMensaje;
        private final float DURACION_MENSAJE = 5f; // segundos
        private Personaje personaje;

        public Chat(Skin skin, Personaje personaje) {
            this.personaje = personaje;
            this.escenarioChat = new Stage();
            this.batch = new SpriteBatch();
            this.fuente = new BitmapFont();
            this.shapeRenderer = new ShapeRenderer();

            // Configurar el campo de texto
            TextField.TextFieldStyle estiloCampo = new TextField.TextFieldStyle();
            estiloCampo.font = new BitmapFont();
            estiloCampo.fontColor = Color.WHITE;
            estiloCampo.background = skin.getDrawable("default-round");

            campoTexto = new TextField("", estiloCampo);
            campoTexto.setMessageText("Escribe un mensaje...");
            campoTexto.setWidth(400);
            campoTexto.setHeight(40);
            campoTexto.setPosition(
                Gdx.graphics.getWidth() / 2 - campoTexto.getWidth() / 2,
                20
            );
            campoTexto.setVisible(false);

            escenarioChat.addActor(campoTexto);
            chatVisible = false;

            // Configurar fuente para globos
            fuente.setColor(Color.BLACK);
            fuente.getData().setScale(0.8f); // Tamaño más pequeño para globos
        }

        public void actualizar(float delta) {
            if (chatVisible) {
                escenarioChat.act(delta);

                // Tecla Enter para enviar mensaje
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    enviarMensaje();
                }

                // Tecla ESC para cancelar
                if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    ocultarChat();
                }
            } else {
                // Tecla T para abrir chat
                if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                    mostrarChat();
                }
            }

            // Actualizar tiempo del mensaje actual
            if (mensajeActual != null) {
                tiempoMensaje += delta;
                if (tiempoMensaje >= DURACION_MENSAJE) {
                    mensajeActual = null;
                }
            }
        }

        public void render() {
            // Dibujar globo de chat si hay mensaje
            if (mensajeActual != null && personaje != null) {
                dibujarGloboChat();
            }

            // Dibujar interfaz de chat si está visible
            if (chatVisible) {
                escenarioChat.draw();
            }
        }

        private void dibujarGloboChat() {
            if (mensajeActual == null) return;

            float x = personaje.getPersonajeX() + personaje.getWidth() / 2;
            float y = personaje.getPersonajeY() + personaje.getHeight() + 20;

            // Medir texto con GlyphLayout
            GlyphLayout layout = new GlyphLayout(fuente, mensajeActual);
            float anchoGlobo = layout.width + 20;
            float altoGlobo = layout.height + 15;

            // Ajustar dentro de pantalla
            if (x - anchoGlobo/2 < 0) x = anchoGlobo/2;
            if (x + anchoGlobo/2 > Gdx.graphics.getWidth()) x = Gdx.graphics.getWidth() - anchoGlobo/2;

            // Dibujar fondo del globo con ShapeRenderer
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(x - anchoGlobo/2, y, anchoGlobo, altoGlobo);
            shapeRenderer.triangle(
                x - 8, y,
                x + 8, y,
                x, y - 12
            );
            shapeRenderer.end();

            // Borde
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(x - anchoGlobo/2, y, anchoGlobo, altoGlobo);
            shapeRenderer.end();

            // Texto
            batch.begin();
            fuente.setColor(Color.BLACK);
            fuente.draw(batch, mensajeActual, x - layout.width/2, y + altoGlobo/2 + layout.height/2);
            batch.end();
        }


        private void mostrarChat() {
            chatVisible = true;
            campoTexto.setVisible(true);
            campoTexto.setText("");
            Gdx.input.setInputProcessor(escenarioChat);
            escenarioChat.setKeyboardFocus(campoTexto); // ✅ foco correcto
        }

        private void ocultarChat() {
            chatVisible = false;
            campoTexto.setVisible(false);
            escenarioChat.unfocus(campoTexto); // ✅ sacar foco
        }


        private void enviarMensaje() {
            String mensaje = campoTexto.getText().trim();
            if (!mensaje.isEmpty() && mensaje.length() <= 50) { // Límite de caracteres
                mensajeActual = mensaje;
                tiempoMensaje = 0f;
                Gdx.app.log("CHAT", "Jugador: " + mensaje);
            }
            ocultarChat();
        }

        public void resize(int width, int height) {
            escenarioChat.getViewport().update(width, height, true);
            // Reposicionar campo de texto al redimensionar
            campoTexto.setPosition(
                width / 2 - campoTexto.getWidth() / 2,
                20
            );
        }

        public void dispose() {
            escenarioChat.dispose();
            batch.dispose();
            fuente.dispose();
            shapeRenderer.dispose();
        }

        // Métodos públicos para control externo
        public boolean isChatVisible() {
            return chatVisible;
        }

        public static boolean estaEscribiendo() {
            return campoTexto.hasKeyboardFocus();
        }




        public void setPersonaje(Personaje personaje) {
            this.personaje = personaje;
        }

        public String getMensajeActual() {
            return mensajeActual;
        }

        public void setInputProcessor() {
            if (chatVisible) {
                Gdx.input.setInputProcessor(escenarioChat);
            }
        }
    }
