package com.abyssdev.entertheabyss.mapas;


import com.badlogic.gdx.math.Rectangle;

public class ZonaTransicion extends Rectangle {

    public String destinoSalaId;
    public String tipo;

    public ZonaTransicion(float x, float y, float w, float h, String destino) {
        super(x, y, w, h);
        this.destinoSalaId = destino;
    }
}
