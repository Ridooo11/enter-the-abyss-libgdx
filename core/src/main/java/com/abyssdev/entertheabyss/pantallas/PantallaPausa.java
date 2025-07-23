package com.abyssdev.entertheabyss.pantallas;

import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PantallaPausa extends ScreenAdapter {

    private final EnterTheAbyssPrincipal juego;
    private final PantallaJuego pantallaJuego;
    private SpriteBatch batch;
    private BitmapFont font;

    public PantallaPausa(EnterTheAbyssPrincipal juego, PantallaJuego pantallaJuego) {
        this.juego = juego;
        this.pantallaJuego = pantallaJuego;
    }
    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // Usa la fuente por defecto, podés cargar una tuya
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Fondo negro
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "Presione P para continuar", Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            juego.setScreen(pantallaJuego);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

}
