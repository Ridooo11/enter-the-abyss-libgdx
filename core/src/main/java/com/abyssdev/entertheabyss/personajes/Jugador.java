package com.abyssdev.entertheabyss.personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Jugador {
    private Vector2 posicion;
    private float ancho = 3f, alto = 3f;
    private final float velocidad = 5f;

    private int vida = 100;
    private int vidaMaxima = 100;
    private int municionActual = 30;
    private int municionMaxima = 30;
    private int monedas = 0;

    // HITBOX
    private final float anchoHitbox = 1f;
    private final float altoHitbox = 1f;
    private final float offsetHitboxX = 1f;
    private final float offsetHitboxY = .5f;

    // MOVIMIENTO
    private boolean arriba, abajo, izquierda, derecha;
    private Texture hojaSprite;
    private Animation<TextureRegion>[][] animaciones;
    private float estadoTiempo;
    private Direccion direccionActual = Direccion.ABAJO;
    private Accion accionActual = Accion.ESTATICO;

    private static final int FRAME_WIDTH = 48;
    private static final int FRAME_HEIGHT = 48;
    private static final int FRAMES_PER_ANIMATION = 3;

    private int[][] mapaFilasAnimacion;

    private Rectangle hitboxAtaque;
    private boolean atacandoAplicado;
    private float duracionHitboxAtaque = 0.1f;
    private float tiempoHitboxActivo;

    public Jugador() {
        this.posicion = new Vector2(100, 100);
        hojaSprite = new Texture("personajes/player.png");
        inicializarMapaFilas();
        cargarAnimaciones();
        hitboxAtaque = new Rectangle(0, 0, 0, 0);
    }

    private void inicializarMapaFilas() {
        mapaFilasAnimacion = new int[Accion.values().length][Direccion.values().length];
        mapaFilasAnimacion[Accion.ESTATICO.ordinal()][Direccion.ABAJO.ordinal()] = 0;
        mapaFilasAnimacion[Accion.ESTATICO.ordinal()][Direccion.DERECHA.ordinal()] = 1;
        mapaFilasAnimacion[Accion.ESTATICO.ordinal()][Direccion.ARRIBA.ordinal()] = 2;
        mapaFilasAnimacion[Accion.ESTATICO.ordinal()][Direccion.IZQUIERDA.ordinal()] = 1;
        mapaFilasAnimacion[Accion.CAMINAR.ordinal()][Direccion.ABAJO.ordinal()] = 3;
        mapaFilasAnimacion[Accion.CAMINAR.ordinal()][Direccion.DERECHA.ordinal()] = 4;
        mapaFilasAnimacion[Accion.CAMINAR.ordinal()][Direccion.ARRIBA.ordinal()] = 5;
        mapaFilasAnimacion[Accion.CAMINAR.ordinal()][Direccion.IZQUIERDA.ordinal()] = 4;
        mapaFilasAnimacion[Accion.ATAQUE.ordinal()][Direccion.ABAJO.ordinal()] = 6;
        mapaFilasAnimacion[Accion.ATAQUE.ordinal()][Direccion.DERECHA.ordinal()] = 7;
        mapaFilasAnimacion[Accion.ATAQUE.ordinal()][Direccion.IZQUIERDA.ordinal()] = 7;
        mapaFilasAnimacion[Accion.ATAQUE.ordinal()][Direccion.ARRIBA.ordinal()] = 8;
        for (int dir = 0; dir < Direccion.values().length; dir++) {
            mapaFilasAnimacion[Accion.MUERTE.ordinal()][dir] = 9;
        }
    }

    private void cargarAnimaciones() {
        TextureRegion[][] regiones = TextureRegion.split(hojaSprite, FRAME_WIDTH, FRAME_HEIGHT);
        animaciones = new Animation[Accion.values().length][Direccion.values().length];

        for (Accion accion : Accion.values()) {
            for (Direccion dir : Direccion.values()) {
                int filaSpriteSheet = mapaFilasAnimacion[accion.ordinal()][dir.ordinal()];
                if (filaSpriteSheet >= regiones.length) {
                    Gdx.app.error("Jugador", "Error: La fila " + filaSpriteSheet +
                        " para la acción " + accion.name() +
                        " y dirección " + dir.name() +
                        " excede las filas disponibles en la hoja de sprites (" + regiones.length + ").");
                    animaciones[accion.ordinal()][dir.ordinal()] = new Animation<>(0.1f, new TextureRegion[1]);
                    continue;
                }

                TextureRegion[] frames = new TextureRegion[FRAMES_PER_ANIMATION];
                for (int i = 0; i < Math.min(FRAMES_PER_ANIMATION, regiones[filaSpriteSheet].length); i++) {
                    frames[i] = regiones[filaSpriteSheet][i];
                }

                float frameDuration = 0.2f;
                Animation.PlayMode playMode = Animation.PlayMode.LOOP;

                switch (accion) {
                    case ESTATICO:
                        frameDuration = 0.2f;
                        playMode = Animation.PlayMode.NORMAL;
                        if (frames.length > 1) {
                            playMode = Animation.PlayMode.LOOP;
                        } else {
                            frames = new TextureRegion[]{regiones[filaSpriteSheet][0]};
                        }
                        break;
                    case CAMINAR:
                        frameDuration = 0.15f;
                        playMode = Animation.PlayMode.LOOP;
                        break;
                    case ATAQUE:
                        frameDuration = 0.1f;
                        playMode = Animation.PlayMode.NORMAL;
                        break;
                    case MUERTE:
                        frameDuration = 0.15f;
                        playMode = Animation.PlayMode.NORMAL;
                        break;
                }
                animaciones[accion.ordinal()][dir.ordinal()] = new Animation<>(frameDuration, frames);
                animaciones[accion.ordinal()][dir.ordinal()].setPlayMode(playMode);
            }
        }
    }

    public void update(float delta, Array<Rectangle> colisiones) {
        float dx = 0, dy = 0;
        if (arriba) dy += 1;
        if (abajo) dy -= 1;
        if (izquierda) dx -= 1;
        if (derecha) dx += 1;

        if (dx != 0 && dy != 0) {
            dx *= 0.7071f;
            dy *= 0.7071f;
        }

        float nuevaX = posicion.x + dx * velocidad * delta;
        float nuevaY = posicion.y + dy * velocidad * delta;

        Rectangle hitboxX = new Rectangle(nuevaX + offsetHitboxX, posicion.y + offsetHitboxY, anchoHitbox, altoHitbox);
        Rectangle hitboxY = new Rectangle(posicion.x + offsetHitboxX, nuevaY + offsetHitboxY, anchoHitbox, altoHitbox);

        boolean colisionX = false;
        for (Rectangle r : colisiones) {
            if (r.overlaps(hitboxX)) {
                colisionX = true;
                break;
            }
        }
        if (!colisionX) {
            posicion.x = nuevaX;
        }

        boolean colisionY = false;
        for (Rectangle r : colisiones) {
            if (r.overlaps(hitboxY)) {
                colisionY = true;
                break;
            }
        }
        if (!colisionY) {
            posicion.y = nuevaY;
        }

        boolean estaMoviendo = (dx != 0 || dy != 0);

        if (accionActual != Accion.MUERTE) {
            if (accionActual == Accion.ATAQUE) {
                estadoTiempo += delta;
                if (!atacandoAplicado && estadoTiempo >= 0.05f) {
                    actualizarHitboxAtaque();
                    atacandoAplicado = true;
                    tiempoHitboxActivo = 0;
                }
                if (atacandoAplicado) {
                    tiempoHitboxActivo += delta;
                    if (tiempoHitboxActivo >= duracionHitboxAtaque) {
                        hitboxAtaque.setSize(0, 0);
                    }
                }

                if (animaciones[accionActual.ordinal()][direccionActual.ordinal()].isAnimationFinished(estadoTiempo)) {
                    accionActual = Accion.ESTATICO;
                    estadoTiempo = 0;
                    atacandoAplicado = false;
                    hitboxAtaque.setSize(0, 0);
                }
            } else if (estaMoviendo) {
                if (accionActual != Accion.CAMINAR) {
                    estadoTiempo = 0;
                }
                accionActual = Accion.CAMINAR;
                estadoTiempo += delta;

                if (Math.abs(dx) > Math.abs(dy)) {
                    direccionActual = dx > 0 ? Direccion.DERECHA : Direccion.IZQUIERDA;
                } else {
                    direccionActual = dy > 0 ? Direccion.ARRIBA : Direccion.ABAJO;
                }
            } else {
                accionActual = Accion.ESTATICO;
                estadoTiempo += delta;
            }
        } else {
            estadoTiempo += delta;
        }
    }

    public void dibujar(SpriteBatch batch) {
        Animation<TextureRegion> currentAnimation = animaciones[accionActual.ordinal()][direccionActual.ordinal()];
        TextureRegion frameADibujar = currentAnimation.getKeyFrame(estadoTiempo);

        boolean voltearX = false;
        if (direccionActual == Direccion.IZQUIERDA) {
            if (accionActual == Accion.ESTATICO || accionActual == Accion.CAMINAR || accionActual == Accion.ATAQUE) {
                voltearX = true;
            }
        }

        TextureRegion frameParaDibujar = new TextureRegion(frameADibujar);
        if (frameParaDibujar.isFlipX() && !voltearX) {
            frameParaDibujar.flip(true, false);
        } else if (!frameParaDibujar.isFlipX() && voltearX) {
            frameParaDibujar.flip(true, false);
        }

        batch.draw(frameParaDibujar, posicion.x, posicion.y, ancho, alto);
    }

    private void actualizarHitboxAtaque() {
        float hitboxWidth = 0.5f;
        float hitboxHeight = 0.5f;
        float offsetX = 0;
        float offsetY = 0;

        switch (direccionActual) {
            case ABAJO:
                offsetX = (ancho - hitboxWidth) / 2;
                offsetY = -0.5f; // ✅ Justo debajo del jugador (punta de la espada)
                break;
            case ARRIBA:
                offsetX = (ancho - hitboxWidth) / 2;
                offsetY = alto; // ✅ Justo encima del jugador
                break;
            case IZQUIERDA:
                offsetX = -0.5f; // ✅ Justo a la izquierda (punta de la espada)
                offsetY = (alto - hitboxHeight) / 2;
                break;
            case DERECHA:
                offsetX = ancho; // ✅ Justo a la derecha (punta de la espada)
                offsetY = (alto - hitboxHeight) / 2;
                break;
        }
        hitboxAtaque.set(posicion.x + offsetX, posicion.y + offsetY, hitboxWidth, hitboxHeight);
        System.out.println("[Jugador] Hitbox de ataque: " + hitboxAtaque.toString()); // ✅ Verificar posición
    }

    public void atacar() {
        if (accionActual != Accion.ATAQUE && accionActual != Accion.MUERTE) {
            accionActual = Accion.ATAQUE;
            estadoTiempo = 0;
            atacandoAplicado = false;
            tiempoHitboxActivo = 0;
        }
    }

    public void morir() {
        if (accionActual != Accion.MUERTE) {
            accionActual = Accion.MUERTE;
            estadoTiempo = 0;
            atacandoAplicado = false;
            hitboxAtaque.setSize(0,0);
        }
    }

    // --- GETTERS ---
    public int getVida() { return this.vida; }
    public int getVidaMaxima() { return this.vidaMaxima; }
    public int getMunicionActual() { return this.municionActual; }
    public int getMunicionMaxima() { return this.municionMaxima; }
    public int getMonedas() { return this.monedas; }
    public float getX() { return posicion.x; }
    public float getY() { return posicion.y; }
    public Vector2 getPosicion() { return posicion; }
    public float getAncho() { return this.ancho; }
    public float getAlto() { return this.alto; }
    public Rectangle getHitboxAtaque() { return hitboxAtaque; }

    // --- SETTERS ---
    public void setVida(int vida) {
        this.vida = Math.max(0, Math.min(vida, vidaMaxima));
    }

    public void modificarVida(int cantidad) {
        setVida(this.vida + cantidad);
    }

    public void setMunicionActual(int municion) {
        this.municionActual = Math.max(0, Math.min(municion, municionMaxima));
    }

    public void modificarMunicion(int cantidad) {
        setMunicionActual(this.municionActual + cantidad);
    }

    public void setMonedas(int monedas) {
        this.monedas = Math.max(0, monedas);
    }

    public void modificarMonedas(int cantidad) {
        setMonedas(this.monedas + cantidad);
    }

    public void dispose() {
        if (hojaSprite != null) {
            hojaSprite.dispose();
        }
    }

    public void setX(float x) { this.posicion.x = x; }
    public void setY(float y) { this.posicion.y = y; }
    public void setPosicion(float x, float y) { this.posicion.set(x, y); }

    public void moverArriba(boolean activo) { this.arriba = activo; }
    public void moverAbajo(boolean activo) { this.abajo = activo; }
    public void moverIzquierda(boolean activo) { this.izquierda = activo; }
    public void moverDerecha(boolean activo) { this.derecha = activo; }

    public void recibirDanio(int i) {
        this.vida -= i;
        Gdx.app.log("Jugador", "Vida actual: " + this.vida);
    }
}
