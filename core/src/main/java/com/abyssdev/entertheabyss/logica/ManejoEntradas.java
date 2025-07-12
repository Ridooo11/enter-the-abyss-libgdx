package com.abyssdev.entertheabyss.logica;

import com.abyssdev.entertheabyss.personajes.Jugador;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class ManejoEntradas implements InputProcessor {
    private Jugador jugador;

    public ManejoEntradas(Jugador jugador) {
        this.jugador = jugador;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.W: jugador.moverArriba(true); break;
            case Input.Keys.S: jugador.moverAbajo(true); break;
            case Input.Keys.A: jugador.moverIzquierda(true); break;
            case Input.Keys.D: jugador.moverDerecha(true); break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.W: jugador.moverArriba(false); break;
            case Input.Keys.S: jugador.moverAbajo(false); break;
            case Input.Keys.A: jugador.moverIzquierda(false); break;
            case Input.Keys.D: jugador.moverDerecha(false); break;
        }
        return true;
    }

    // Métodos que no usamos por ahora
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
