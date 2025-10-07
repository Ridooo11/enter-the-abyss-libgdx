package com.abyssdev.entertheabyss.habilidades;

import com.badlogic.gdx.graphics.Texture;

public class Habilidad {
    private String nombre;
    private String descripcion;
    private int costo;
    private Texture icono;
    public boolean desbloqueado = false;
    public int nivelActual = 0;
    public int nivelMaximo = 3;
    public boolean comprada = false;

    public Habilidad(String nombre, String descripcion, int costo, String iconoPath) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costo = costo;
        this.icono = new Texture(iconoPath);
    }

    public String getNombre() {
        return this.nombre;
    }

    public Texture getIcono() {
        return this.icono;
    }

    public int getCosto(){return this.costo;}
}
