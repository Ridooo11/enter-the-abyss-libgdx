package com.abyssdev.entertheabyss.personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Jugador {
    private float x, y;
    private float ancho = 16f, alto = 16f;
    private final float velocidad = 5f;

    private boolean arriba, abajo, izquierda, derecha;

    private Texture textura;

    public Jugador() {
        this.x = 100f;
        this.y = 100f;
        textura = new Texture("personajes/personaje.png");
    }

    public void update(float delta, TiledMapTileLayer capaColision) {
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


    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public float getAlto() { return this.alto;}
    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }
    public void moverArriba(boolean activo) { this.arriba = activo; }
    public void moverAbajo(boolean activo) { this.abajo = activo; }
    public void moverIzquierda(boolean activo) { this.izquierda = activo; }
    public void moverDerecha(boolean activo) { this.derecha = activo; }
}
