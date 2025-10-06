package com.abyssdev.entertheabyss.pantallas;

import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PantallaPausa extends Pantalla {

    private final PantallaJuego pantallaJuego;
    private BitmapFont font;
    private Texture fondoPausa;

    private final String[] opciones = {"Continuar", "Opciones", "Salir"};
    private int opcionSeleccionada = 0;

    private float tiempoParpadeo = 0;
    private boolean mostrarColor = true;

    private GlyphLayout layout;
    private Viewport viewport;
    private OrthographicCamera camara;

    public PantallaPausa(EnterTheAbyssPrincipal juego, PantallaJuego pantallaJuego) {
        super(juego);
        this.pantallaJuego = pantallaJuego;
    }

    @Override
    public void show() {
        font = new BitmapFont();
        font.getData().setScale(2.2f);

        camara = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camara);
        viewport.apply();
        camara.position.set(camara.viewportWidth / 2f, camara.viewportHeight / 2f, 0);
        camara.update();

        fondoPausa = new Texture("Fondos/pausa2.PNG");
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

        camara.update();
        juego.batch.setProjectionMatrix(camara.combined);

        float ancho = viewport.getWorldWidth();
        float alto = viewport.getWorldHeight();

        juego.batch.begin();
        juego.batch.draw(fondoPausa, 0, 0, ancho, alto);

        float centerX = 400f;
        float centerY = 300f;

        for (int i = 0; i < opciones.length; i++) {
            String texto = opciones[i];
            layout.setText(font, texto);
            float x = centerX - layout.width / 2f;
            float y = centerY + (opciones.length - 1 - i) * 60 - 20;

            if (i == opcionSeleccionada && mostrarColor) {
                font.setColor(Color.YELLOW);
            } else {
                font.setColor(Color.WHITE);
            }

            font.draw(juego.batch, texto, x, y);
        }

        juego.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
                    juego.setScreen(pantallaJuego);
                    break;
                case 1:
                    juego.setScreen(new PantallaOpciones(juego, this));
                    break;
                case 2:
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
        font.dispose();
        fondoPausa.dispose();
    }
}
