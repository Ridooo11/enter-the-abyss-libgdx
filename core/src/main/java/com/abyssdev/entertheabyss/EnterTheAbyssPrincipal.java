package com.abyssdev.entertheabyss;

import com.abyssdev.entertheabyss.pantallas.MenuInicio;
import com.abyssdev.entertheabyss.pantallas.PantallaGameOver;
import com.abyssdev.entertheabyss.personajes.Jugador;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class EnterTheAbyssPrincipal extends Game {
    public SpriteBatch batch; // SpriteBatch usado por todas las pantallas que va a tener el juego

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MenuInicio(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
