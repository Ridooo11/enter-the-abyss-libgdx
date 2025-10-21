package com.abyssdev.entertheabyss.personajes;

public enum TipoEnemigoVelocidad {
    ENEMIGO(2.5f),
    BOSS(1.5f);

    private float velocidad;

    TipoEnemigoVelocidad(float velocidad) {
        this.velocidad = velocidad;

    }

    public float getVelocidad() { return this.velocidad; }

}
