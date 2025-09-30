package com.abyssdev.entertheabyss.pantallas;

import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.abyssdev.entertheabyss.ui.Sonidos;

public class MenuInicio extends Pantalla {

    private BitmapFont font;
    private Texture fondo;

    private final String[] opciones = {"Comenzar", "Salir"}; // "Comenzar" primero
    private int opcionSeleccionada = 0;
    private float tiempoParpadeo = 0;
    private boolean mostrarRojo = true;

    private GlyphLayout layout;
    private Viewport viewport;
    private OrthographicCamera camara;

    public MenuInicio(EnterTheAbyssPrincipal juego) {
        super(juego);
    }

    @Override
    public void show() {
        font = new BitmapFont();
        font.getData().setScale(2.5f);
        camara = new OrthographicCamera();
        viewport = new FitViewport(640, 480, camara);
        viewport.apply();
        camara.position.set(camara.viewportWidth / 2f, camara.viewportHeight / 2f, 0);
        camara.update();
        fondo = new Texture(Gdx.files.internal("Fondos/ImagenMenuInicio.PNG"));
        layout = new GlyphLayout();

       Sonidos.reproducirMusicaMenu(); // ✅ Reproducir música de menú
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tiempoParpadeo += delta;
        if (tiempoParpadeo > 0.5f) {
            mostrarRojo = !mostrarRojo;
            tiempoParpadeo = 0;
        }

        manejarInput();

        camara.update();
        juego.batch.setProjectionMatrix(camara.combined);


        float ancho = viewport.getWorldWidth();
        float alto = viewport.getWorldHeight();

        juego.batch.begin();
        juego.batch.draw(fondo, 0, 0, ancho, alto);


        float centerX = viewport.getWorldWidth() / 2f;
        float centerY = viewport.getWorldHeight() / 2f - 100;

        for (int i = 0; i < opciones.length; i++) {
            String texto = opciones[i];

            layout.setText(font, texto);
            float textoAncho = layout.width;
            float textoAlto = layout.height;

            float x = centerX - textoAncho / 2f;
            float y = centerY + (opciones.length - 1 - i) * 60; // separadas 60 px verticalmente, empezando arriba

            if (i == opcionSeleccionada && mostrarRojo) {
                font.setColor(Color.RED);
            } else {
                font.setColor(Color.WHITE);
            }

            font.draw(juego.batch, texto, x, y);
        }

        juego.batch.end();


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
            if (opcionSeleccionada == 0) {
                Sonidos.reproducirMusicaJuego(); // ✅ Reproducir música de juego ANTES de cambiar
                juego.setScreen(new PantallaJuego(juego));
            } else if (opcionSeleccionada == 1) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void dispose() {
        font.dispose();
        fondo.dispose();
    }
}
