package com.abyssdev.entertheabyss.personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class Enemigo {

    public enum Estado {
        IDLE, CAMINAR, ATAQUE, HIT, MUERTO
    }

    private static final float VELOCIDAD = 3f;
    private static final float TAMANO = 3f;
    private static final float DISTANCIA_ATAQUE = 1f;
    private static final float COOLDOWN_ATAQUE = 1f;

    private float tiempoDesdeUltimoAtaque = 0;
    private float tiempoDesdeUltimoGolpe = 0f;
    private static final float COOLDOWN_GOLPE = 0.1f;

    private Texture hojaSprite;
    private Vector2 posicion;
    private Vector2 velocidad;
    private Estado estado;
    private float tiempoEstado;

    private Animation<TextureRegion> animIdle;
    private Animation<TextureRegion> animCaminar;
    private Animation<TextureRegion> animAtacar;
    private Animation<TextureRegion> animHit;
    private Animation<TextureRegion> animMuerte;

    private boolean eliminar;
    private boolean haciaIzquierda;

    private int golpesRecibidos = 0;
    private static final int GOLPES_PARA_MORIR = 3;

    public Enemigo(float x, float y) {
        hojaSprite = new Texture("personajes/esqueletoEnemigo.png");
        posicion = new Vector2(x, y);
        velocidad = new Vector2(0, 0);
        estado = Estado.IDLE;
        tiempoEstado = 0;
        eliminar = false;
        haciaIzquierda = false;
        golpesRecibidos = 0;
        tiempoDesdeUltimoGolpe = 0;
        tiempoDesdeUltimoAtaque = 0;
        cargarAnimaciones();
    }

    private void cargarAnimaciones() {
        TextureRegion[][] regiones = TextureRegion.split(hojaSprite, 64, 64);
        animAtacar = crearAnimacion(regiones[0], 13, 0.07f, Animation.PlayMode.LOOP);
        animMuerte = crearAnimacion(regiones[1], 13, 0.1f, Animation.PlayMode.NORMAL);
        animCaminar = crearAnimacion(regiones[2], 12, 0.1f, Animation.PlayMode.LOOP);
        animIdle = crearAnimacion(regiones[3], 4, 0.2f, Animation.PlayMode.LOOP);
        animHit = crearAnimacion(regiones[4], 3, 0.1f, Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> crearAnimacion(TextureRegion[] frames, int cantidad, float duracion, Animation.PlayMode modo) {
        TextureRegion[] usados = new TextureRegion[cantidad];
        System.arraycopy(frames, 0, usados, 0, cantidad);
        Animation<TextureRegion> anim = new Animation<>(duracion, usados);
        anim.setPlayMode(modo);
        return anim;
    }

    public boolean actualizar(float delta, Vector2 posicionJugador, Array<Rectangle> colisionesMapa, ArrayList<Enemigo> otrosEnemigos) {
        tiempoEstado += delta;
        tiempoDesdeUltimoAtaque += delta;
        tiempoDesdeUltimoGolpe += delta;

        if (estado == Estado.MUERTO && animMuerte.isAnimationFinished(tiempoEstado)) {
            eliminar = true;
            return false;
        }

        if (estado == Estado.HIT && animHit.isAnimationFinished(tiempoEstado)) {
            cambiarEstado(Estado.IDLE);
        }

        if (estado != Estado.HIT && estado != Estado.MUERTO) {
            Vector2 direccion = new Vector2(posicionJugador).sub(posicion);
            float distancia = direccion.len();

            if (distancia < DISTANCIA_ATAQUE) {
                velocidad.setZero();
                if (tiempoDesdeUltimoAtaque >= COOLDOWN_ATAQUE) {
                    cambiarEstado(Estado.ATAQUE);
                    tiempoDesdeUltimoAtaque = 0;
                    Gdx.app.log("Enemigo", "¡Jugador ha sido atacado!");
                    return true;
                }
            } else {
                cambiarEstado(Estado.CAMINAR);
                direccion.nor();
                velocidad.set(direccion.scl(VELOCIDAD));
                haciaIzquierda = velocidad.x < 0;

                Vector2 nuevaPos = new Vector2(posicion).add(velocidad.x * delta, velocidad.y * delta);
                Rectangle rectNuevo = new Rectangle(nuevaPos.x, nuevaPos.y, TAMANO, TAMANO);

                boolean colisiona = false;
                for (Rectangle r : colisionesMapa) {
                    if (rectNuevo.overlaps(r)) {
                        colisiona = true;
                        break;
                    }
                }
                if (!colisiona) {
                    for (Enemigo otro : otrosEnemigos) {
                        if (otro != this && rectNuevo.overlaps(otro.getRectangulo())) {
                            colisiona = true;
                            break;
                        }
                    }
                }
                if (!colisiona) {
                    cambiarEstado(Estado.CAMINAR);
                    posicion.set(nuevaPos);
                } else {
                    cambiarEstado(Estado.IDLE);
                    velocidad.setZero();
                }
            }
        }
        return false;
    }

    public void renderizar(SpriteBatch batch) {
        TextureRegion frame = obtenerFrameActual();
        float width = TAMANO;
        float height = TAMANO;
        float drawX = posicion.x;
        float drawY = posicion.y;

        if (haciaIzquierda && !frame.isFlipX()) {
            frame.flip(true, false);
        } else if (!haciaIzquierda && frame.isFlipX()) {
            frame.flip(true, false);
        }

        batch.draw(frame, drawX, drawY, width, height);
    }

    private TextureRegion obtenerFrameActual() {
        switch (estado) {
            case CAMINAR:
                return animCaminar.getKeyFrame(tiempoEstado);
            case ATAQUE:
                return animAtacar.getKeyFrame(tiempoEstado);
            case HIT:
                return animHit.getKeyFrame(tiempoEstado);
            case MUERTO:
                return animMuerte.getKeyFrame(tiempoEstado);
            case IDLE:
            default:
                return animIdle.getKeyFrame(tiempoEstado);
        }
    }

    public void recibirGolpe() {
        if (tiempoDesdeUltimoGolpe < COOLDOWN_GOLPE) return;

        golpesRecibidos++;
        cambiarEstado(Estado.HIT);
        System.out.println("🔥 Enemigo recibió golpe " + golpesRecibidos + "/" + GOLPES_PARA_MORIR); // ✅ Verificar golpes

        if (golpesRecibidos >= GOLPES_PARA_MORIR) {
            morir();
        }

        tiempoDesdeUltimoGolpe = 0f;
    }

    public void morir() {
        cambiarEstado(Estado.MUERTO);
    }

    private void cambiarEstado(Estado nuevo) {
        if (estado != nuevo) {
            estado = nuevo;
            tiempoEstado = 0;
            if (nuevo != Estado.CAMINAR) {
                velocidad.setZero();
            }
        }
    }

    public Rectangle getRectangulo() {
        return new Rectangle(posicion.x, posicion.y, TAMANO, TAMANO);
    }

    public boolean debeEliminarse() {
        return eliminar;
    }

    public Vector2 getPosicion() {
        return posicion;
    }
}
