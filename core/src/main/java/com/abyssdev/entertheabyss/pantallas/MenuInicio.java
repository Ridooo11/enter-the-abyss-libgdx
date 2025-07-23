package com.abyssdev.entertheabyss.pantallas;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;

public class MenuInicio extends ScreenAdapter {

    private final EnterTheAbyssPrincipal juego;
    private SpriteBatch batch;


    public MenuInicio(EnterTheAbyssPrincipal juego) {
        this.juego = juego;
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
        font.draw(batch, "Presione ENTER para comenzar", Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            juego.setScreen(new PantallaJuego(juego));
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
