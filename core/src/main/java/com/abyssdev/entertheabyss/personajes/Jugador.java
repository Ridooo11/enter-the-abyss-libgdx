package com.abyssdev.entertheabyss.personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle; // Importar Rectangle para el hitbox
import com.badlogic.gdx.utils.Array;

public class Jugador {
    private Vector2 posicion;
    private float ancho = 3f, alto = 3f;
    private final float velocidad = 5f;
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


        // ESTATICO
        mapaFilasAnimacion[Accion.ESTATICO.ordinal()][Direccion.ABAJO.ordinal()] = 0;   // Estático de frente
        mapaFilasAnimacion[Accion.ESTATICO.ordinal()][Direccion.DERECHA.ordinal()] = 1; // Estático hacia la derecha
        mapaFilasAnimacion[Accion.ESTATICO.ordinal()][Direccion.ARRIBA.ordinal()] = 2;   // Estático hacia arriba
        // IDLE hacia la izquierda usa la animación de IDLE hacia la derecha (fila 1)
        mapaFilasAnimacion[Accion.ESTATICO.ordinal()][Direccion.IZQUIERDA.ordinal()] = 1;

        // CAMINAR
        mapaFilasAnimacion[Accion.CAMINAR.ordinal()][Direccion.ABAJO.ordinal()] = 3;   // Caminar hacia abajo
        mapaFilasAnimacion[Accion.CAMINAR.ordinal()][Direccion.DERECHA.ordinal()] = 4; // Caminar hacia la derecha
        mapaFilasAnimacion[Accion.CAMINAR.ordinal()][Direccion.ARRIBA.ordinal()] = 5;   // Caminar hacia arriba
        // Caminar hacia la izquierda usa la animación de caminar hacia la derecha (fila 4)
        mapaFilasAnimacion[Accion.CAMINAR.ordinal()][Direccion.IZQUIERDA.ordinal()] = 4;

        // ATAQUE
        mapaFilasAnimacion[Accion.ATAQUE.ordinal()][Direccion.ABAJO.ordinal()] = 6;   // Atacar hacia abajo
        mapaFilasAnimacion[Accion.ATAQUE.ordinal()][Direccion.DERECHA.ordinal()] = 7; // Atacar hacia la derecha
        // Atacar hacia la izquierda usa la animación de atacar hacia la DERECHA (fila 7)
        mapaFilasAnimacion[Accion.ATAQUE.ordinal()][Direccion.IZQUIERDA.ordinal()] = 7;
        mapaFilasAnimacion[Accion.ATAQUE.ordinal()][Direccion.ARRIBA.ordinal()] = 8;   // Atacar hacia arriba

        // MUERTE
        for (int dir = 0; dir < Direccion.values().length; dir++) {
            mapaFilasAnimacion[Accion.MUERTE.ordinal()][dir] = 9; // Fila 9 para efecto de muerte
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
                // --- Lógica del Hitbox de Ataque ---
                if (!atacandoAplicado && estadoTiempo >= 0.05f) { // Activa el hitbox después de 0.05 segundos
                    actualizarHitboxAtaque();
                    atacandoAplicado = true;
                    tiempoHitboxActivo = 0;
                }
                if (atacandoAplicado) {
                    tiempoHitboxActivo += delta;
                    if (tiempoHitboxActivo >= duracionHitboxAtaque) {
                        hitboxAtaque.setSize(0, 0); // Desactiva el hitbox
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

        // --- Lógica de Volteo de Sprites ---
        boolean voltearX = false;
        if (direccionActual == Direccion.IZQUIERDA) {
            // Voltear si la acción es ESTATICO, CAMINAR, o ATAQUE,
            // ya que sus animaciones de IZQUIERDA reutilizan las de DERECHA.
            if (accionActual == Accion.ESTATICO || accionActual == Accion.CAMINAR || accionActual == Accion.ATAQUE) {
                voltearX = true;
            }
        }

        // Aplicar o revertir el volteo del frame
        TextureRegion frameParaDibujar = new TextureRegion(frameADibujar); // Crea una COPIA

        if (frameParaDibujar.isFlipX() && !voltearX) {
            frameParaDibujar.flip(true, false); // Desvoltear si estaba volteado y no debería estarlo
        } else if (!frameParaDibujar.isFlipX() && voltearX) {
            frameParaDibujar.flip(true, false); // Voltear si no estaba volteado y debería estarlo
        }

        batch.draw(frameParaDibujar, posicion.x, posicion.y, ancho, alto);
    }

    private void actualizarHitboxAtaque() {
        float hitboxWidth = 30f;
        float hitboxHeight = 30f;
        float offsetX = 0;
        float offsetY = 0;

        switch (direccionActual) {
            case ABAJO:
                offsetX = (ancho - hitboxWidth) / 2;
                offsetY = -hitboxHeight;
                break;
            case ARRIBA:
                offsetX = (ancho - hitboxWidth) / 2;
                offsetY = alto;
                break;
            case IZQUIERDA:
                offsetX = -hitboxWidth;
                offsetY = (alto - hitboxHeight) / 2;
                break;
            case DERECHA:
                offsetX = ancho;
                offsetY = (alto - hitboxHeight) / 2;
                break;
        }
        hitboxAtaque.set(posicion.x + offsetX, posicion.y + offsetY, hitboxWidth, hitboxHeight);
        Gdx.app.log("Jugador", "Hitbox de ataque activado en: " + hitboxAtaque.toString());
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


    public void dispose() {
        if (hojaSprite != null) {
            hojaSprite.dispose();
        }
    }

    public float getX() { return posicion.x; }
    public float getY() { return posicion.y; }
    public Vector2 getPosicion() { return posicion; }
    public float getAncho() { return this.ancho; }
    public float getAlto() { return this.alto; }
    public Rectangle getHitboxAtaque() { return hitboxAtaque; }

    public void setX(float x) { this.posicion.x = x; }
    public void setY(float y) { this.posicion.y = y; }
    public void setPosicion(float x, float y) { this.posicion.set(x, y); }

    public void moverArriba(boolean activo) { this.arriba = activo; }
    public void moverAbajo(boolean activo) { this.abajo = activo; }
    public void moverIzquierda(boolean activo) { this.izquierda = activo; }
    public void moverDerecha(boolean activo) { this.derecha = activo; }
}
