package com.abyssdev.entertheabyss.personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Jugador {
    private float x, y;
    private final float velocidad = 5f;

    private boolean arriba, abajo, izquierda, derecha;

    private Texture textura;

    public Jugador() {
        this.x = 100f;
        this.y = 100f;
        textura = new Texture("personajes/personaje.png");
    }

    public void actualizarMovimiento(float delta) {
        float dx = 0, dy = 0;
        if (arriba) dy += 1;
        if (abajo) dy -= 1;
        if (izquierda) dx -= 1;
        if (derecha) dx += 1;


        if (dx != 0 && dy != 0) {
            dx *= 0.7071f;
            dy *= 0.7071f;
        }

        x += dx * velocidad * delta;
        y += dy * velocidad * delta;
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, x, y, textura.getWidth() / 16f, textura.getHeight() / 16f);

    }

    public void dispose() {
        if (textura != null) {
            textura.dispose();
        }
    }



    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    // Setters desde el input
    public void moverArriba(boolean activo) { arriba = activo; }
    public void moverAbajo(boolean activo) { abajo = activo; }
    public void moverIzquierda(boolean activo) { izquierda = activo; }
    public void moverDerecha(boolean activo) { derecha = activo; }
}
