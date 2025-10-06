package com.abyssdev.entertheabyss.pantallas;

import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;
import com.abyssdev.entertheabyss.ui.FontManager;
import com.abyssdev.entertheabyss.ui.Sonidos;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuInicio extends Pantalla {

    private BitmapFont fontOpciones;
    private Texture fondo;

    private final String[] opciones = {"Comenzar", "Opciones", "Salir"};
    private int opcionSeleccionada = 0;

    private float tiempoParpadeo = 0;
    private float alphaParpadeo = 1f;
    private float tiempoTotal = 0;

    private GlyphLayout layout;
    private Viewport viewport;
    private OrthographicCamera camara;

    public MenuInicio(Game juego, SpriteBatch batch) {
        super(juego,batch);
    }

    @Override
    public void show() {
        fontOpciones = FontManager.getInstance().getGrande();

        camara = new OrthographicCamera();
        viewport = new FitViewport(640, 480, camara);
        viewport.apply();
        camara.position.set(camara.viewportWidth / 2f, camara.viewportHeight / 2f, 0);
        camara.update();

        fondo = new Texture(Gdx.files.internal("Fondos/ImagenMenuInicio.PNG"));
        fondo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        layout = new GlyphLayout();
        Sonidos.reproducirMusicaMenu();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tiempoTotal += delta;
        actualizarAnimaciones(delta);
        manejarInput();

        camara.update();
        batch.setProjectionMatrix(camara.combined);

        float ancho = viewport.getWorldWidth();
        float alto = viewport.getWorldHeight();
        float centerX = ancho / 2f;
        float centerY = alto / 2f;

        batch.begin();
        dibujarFondoCubriendo(fondo, ancho, alto);

        float startY = centerY - 80;
        for (int i = 0; i < opciones.length; i++) {
            String texto = opciones[i];
            layout.setText(fontOpciones, texto);

            float x = centerX - layout.width / 2f;
            float y = startY - (i * 70);

            if (i == opcionSeleccionada) {
                fontOpciones.setColor(1, 0, 0, alphaParpadeo);
                layout.setText(fontOpciones, "►");
                fontOpciones.draw(batch, "►", x - 50, y);
                fontOpciones.draw(batch, "◄", x + layout.width + 30, y);
            } else {
                fontOpciones.setColor(1, 1, 1, 0.6f);
            }

            fontOpciones.draw(batch, texto, centerX - layout.width / 2f, y);
        }

        batch.end();
    }

    private void actualizarAnimaciones(float delta) {
        tiempoParpadeo += delta;
        alphaParpadeo = 0.7f + 0.3f * (float) Math.sin(tiempoParpadeo * 4);
    }

    private void dibujarFondoCubriendo(Texture textura, float anchoViewport, float altoViewport) {
        float texturaAspect = (float) textura.getWidth() / textura.getHeight();
        float viewportAspect = anchoViewport / altoViewport;

        float drawWidth, drawHeight;
        float offsetX = 0, offsetY = 0;

        if (texturaAspect > viewportAspect) {
            drawHeight = altoViewport;
            drawWidth = drawHeight * texturaAspect;
            offsetX = (anchoViewport - drawWidth) / 2f;
        } else {
            drawWidth = anchoViewport;
            drawHeight = drawWidth / texturaAspect;
            offsetY = (altoViewport - drawHeight) / 2f;
        }

       batch.draw(textura, offsetX, offsetY, drawWidth, drawHeight);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camara.position.set(camara.viewportWidth / 2f, camara.viewportHeight / 2f, 0);
        camara.update();
    }

    private void manejarInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            opcionSeleccionada = (opcionSeleccionada + 1) % opciones.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            opcionSeleccionada = (opcionSeleccionada - 1 + opciones.length) % opciones.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (opcionSeleccionada) {
                case 0:
                    Sonidos.reproducirMusicaJuego();
                    juego.setScreen(new PantallaJuego(juego,batch));
                    break;
                case 1:
                    juego.setScreen(new PantallaOpciones(juego,batch, this));
                    break;
                case 2:
                    Gdx.app.exit();
                    break;
            }
        }
    }

    @Override
    public void dispose() {
        if (fondo != null) fondo.dispose();
    }
}
