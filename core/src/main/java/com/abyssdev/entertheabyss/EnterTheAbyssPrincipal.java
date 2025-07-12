package com.abyssdev.entertheabyss;

import com.abyssdev.entertheabyss.personajes.Jugador;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class EnterTheAbyssPrincipal extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture texturaPersonaje;
    private Jugador jugador;

    @Override
    public void create() {
        batch = new SpriteBatch();
        texturaPersonaje = new Texture("personajes/personaje_principal.png");
        jugador = new Jugador();
        jugador.setX(100);
        jugador.setY(100);
    }

    @Override
    public void render() {
        // Limpia pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Manejo de entrada
        jugador.moverArriba(Gdx.input.isKeyPressed(Input.Keys.W));
        jugador.moverAbajo(Gdx.input.isKeyPressed(Input.Keys.S));
        jugador.moverIzquierda(Gdx.input.isKeyPressed(Input.Keys.A));
        jugador.moverDerecha(Gdx.input.isKeyPressed(Input.Keys.D));

        // Actualiza posición
        jugador.actualizar(Gdx.graphics.getDeltaTime());
        batch.begin();
        batch.draw(texturaPersonaje, jugador.getX(), jugador.getY());
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        texturaPersonaje.dispose();
    }
}
