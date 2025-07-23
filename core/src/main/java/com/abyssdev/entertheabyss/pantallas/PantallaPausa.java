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

public class PantallaPausa extends ScreenAdapter {

    private final EnterTheAbyssPrincipal juego;
    private final PantallaJuego pantallaJuego;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture fondoPausa;

    private final String[] opciones = {"Continuar", "Guardar", "Salir"};
    private int opcionSeleccionada = 0;

    private float tiempoParpadeo = 0;
    private boolean mostrarColor = true;

    private GlyphLayout layout;

    public PantallaPausa(EnterTheAbyssPrincipal juego, PantallaJuego pantallaJuego) {
        this.juego = juego;
        this.pantallaJuego = pantallaJuego;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.2f); // Tamaño grande

        fondoPausa = new Texture("Fondos/ImagenPantallaPausa.PNG");
        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tiempoParpadeo += delta;
        if (tiempoParpadeo > 0.5f) {
            mostrarColor = !mostrarColor;
            tiempoParpadeo = 0;
        }

        manejarInput();

        batch.begin();

        batch.draw(fondoPausa, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f - 80; // Posición vertical base

        for (int i = 0; i < opciones.length; i++) {
            String texto = opciones[i];
            layout.setText(font, texto);
            float x = centerX - layout.width / 2f;
            float y = centerY + (opciones.length - 1 - i) * 60;

            if (i == opcionSeleccionada && mostrarColor) {
                font.setColor(Color.YELLOW);
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
            switch (opcionSeleccionada) {
                case 0:
                    juego.setScreen(pantallaJuego);
                    break;
                case 1:
                    System.out.println("Función guardar aún no implementada.");
                    break;
                case 2: // Salir
                    Gdx.app.exit();
                    break;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(pantallaJuego);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fondoPausa.dispose();
    }
}
