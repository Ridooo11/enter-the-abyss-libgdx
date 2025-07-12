package com.abyssdev.entertheabyss.personajes;

public class Jugador {
    private float x, y;
    private float velocidad = 200f;

    private boolean arriba, abajo, izquierda, derecha;

    public void actualizar(float delta) {
        if (arriba) y += velocidad * delta;
        if (abajo) y -= velocidad * delta;
        if (izquierda) x -= velocidad * delta;
        if (derecha) x += velocidad * delta;
    }

    // Setters para movimiento
    public void moverArriba(boolean activo) { arriba = activo; }
    public void moverAbajo(boolean activo) { abajo = activo; }
    public void moverIzquierda(boolean activo) { izquierda = activo; }
    public void moverDerecha(boolean activo) { derecha = activo; }

    // Getters para posición
    public float getX() { return x; }
    public float getY() { return y; }

    // Setters para posición inicial
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
}
