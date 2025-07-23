package com.abyssdev.entertheabyss.pantallas;

import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MenuInicio extends ScreenAdapter {

    private final EnterTheAbyssPrincipal juego;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture fondo;

    private final String[] opciones = {"Comenzar", "Salir"}; // "Comenzar" primero
    private int opcionSeleccionada = 0;
    private float tiempoParpadeo = 0;
    private boolean mostrarRojo = true;

    private GlyphLayout layout;

    public MenuInicio(EnterTheAbyssPrincipal juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        font = new BitmapFont(); // Fuente por defecto
        font.getData().setScale(2.5f); // Aumentar tamaño de fuente

        fondo = new Texture(Gdx.files.internal("Fondos/ImagenMenuInicio.PNG"));
        layout = new GlyphLayout();
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

        batch.begin();
        batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f -100;

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

            font.draw(batch, texto, x, y);
        }

        batch.end();
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
                juego.setScreen(new PantallaJuego(juego));
            } else if (opcionSeleccionada == 1) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fondo.dispose();
    }
}
