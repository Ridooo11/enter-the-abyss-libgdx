package com.abyssdev.entertheabyss.personajes;

public enum Accion {

    ESTATICO(false,0f, false,0),
    CAMINAR(true, 0f, false,0),
    ATAQUE(false, 2f, true,10),
    HIT(false,0f, false,0),
    MUERTE(false,0f, false,0);

    private final boolean mueve;
    private final boolean ofensiva;
    private final float coldown;
    private final int danioBruto;

    Accion(boolean mueve, float coldown, boolean ofensiva,int danioBruto) {
        this.mueve = mueve;
        this.ofensiva = ofensiva;
        this.coldown = coldown;
        this.danioBruto = danioBruto;
    }

    public boolean getMueve() { return this.mueve; }
    public boolean getTipoAccion() { return this.ofensiva; }
    public float getColdown(){return this.coldown;}
    public int getDanioBruto(boolean esBoss) {
        if (esBoss) {
            return this.danioBruto * 3; // Boss hace el doble de daño
        }
        return this.danioBruto;
    }
}
