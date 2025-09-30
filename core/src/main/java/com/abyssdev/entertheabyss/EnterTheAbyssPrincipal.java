package com.abyssdev.entertheabyss;

import com.abyssdev.entertheabyss.pantallas.MenuInicio;
import com.abyssdev.entertheabyss.pantallas.PantallaGameOver;
import com.abyssdev.entertheabyss.personajes.Jugador;
import com.abyssdev.entertheabyss.ui.Sonidos;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.abyssdev.entertheabyss.ui.Sonidos;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class EnterTheAbyssPrincipal extends Game {
    public SpriteBatch batch; // SpriteBatch usado por todas las pantallas que va a tener el juego
    public Texture texturaBlanca; // Pixel de 1x1 usado para generar efecto "Fade" en las transiciones, se crea aca para que la usen todas las pantallas

    @Override
    public void create() {
        batch = new SpriteBatch();
        texturaBlanca = generarTexturaBlanca();
        Sonidos.cargar();
        setScreen(new MenuInicio(this));
    }

    public Texture generarTexturaBlanca() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture textura = new Texture(pixmap);
        pixmap.dispose();
        return textura;
    }

    @Override
    public void dispose() {
        batch.dispose();
        texturaBlanca.dispose();
        Sonidos.dispose();
    }
}
