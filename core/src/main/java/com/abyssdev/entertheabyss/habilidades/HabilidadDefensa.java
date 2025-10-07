package com.abyssdev.entertheabyss.habilidades;

import com.abyssdev.entertheabyss.personajes.Jugador;

public class HabilidadDefensa extends Habilidad {
    public HabilidadDefensa() {
        super("Defensa", "Reduce el daño recibido.", 5, "imagenes/escudo.png");
    }

    @Override
    public void aplicar(Jugador jugador) {
        jugador.reducirDanioRecibido(0.2f); // 20% menos daño
    }
}
