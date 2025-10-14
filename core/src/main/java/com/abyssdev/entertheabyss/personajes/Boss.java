package com.abyssdev.entertheabyss.personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

public class Boss extends Enemigo {

//    private float tiempoEspecial;   // para habilidades especiales
    private boolean enFuria;        // fase extra del jefe
    private float velocidadBoss = TipoEnemigoVelocidad.BOSS.getVelocidad();
    public Boss(float x, float y) {
        super(x, y);
        this.vidaMaxima = 200;
        this.vida = 200;
//        this.tiempoEspecial = 0;
        this.estado = Accion.ESTATICO;
        this.velocidad.set(velocidadBoss, velocidadBoss);
        this.enFuria = false;
        this.hojaSprite = new Texture("personajes/esqueletoEnemigo.png");
        cargarAnimaciones();
    }

    @Override
    public boolean actualizar(float delta, Vector2 posicionJugador, Array<Rectangle> colisionesMapa, ArrayList<Enemigo> otrosEnemigos) {
        tiempoEstado += delta;
        tiempoDesdeUltimoAtaque += delta;
        tiempoDesdeUltimoGolpe += delta;

        if (estado == Accion.MUERTE && animMuerte.isAnimationFinished(tiempoEstado)) {
            eliminar = true;
            return false;
        }

        if (estado == Accion.HIT && animHit.isAnimationFinished(tiempoEstado)) {
            cambiarEstado(Accion.ESTATICO);
        }

        if (estado != Accion.HIT && estado != Accion.MUERTE) {
            Vector2 direccion = new Vector2(posicionJugador).sub(posicion);
            float distancia = direccion.len();

            if (distancia < 0.8f) {
                velocidad.setZero();
                if (tiempoDesdeUltimoAtaque >= Accion.ATAQUE.getColdown()) {
                    cambiarEstado(Accion.ATAQUE);
                    tiempoDesdeUltimoAtaque = 0;
                    Gdx.app.log("Boss", "¡Boss ataca!");
                    return true;
                }
            } else {
                cambiarEstado(Accion.CAMINAR);
                direccion.nor();
                velocidad.set(direccion.scl(TipoEnemigoVelocidad.BOSS.getVelocidad()));
                haciaIzquierda = velocidad.x < 0;

                Vector2 nuevaPos = new Vector2(posicion).add(velocidad.x * delta, velocidad.y * delta);
                Rectangle rectNuevo = new Rectangle(nuevaPos.x, nuevaPos.y, 6.1f, 6.1f);

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
                    cambiarEstado(Accion.CAMINAR);
                    posicion.set(nuevaPos);
                } else {
                    cambiarEstado(Accion.ESTATICO);
                    velocidad.setZero();
                }
            }
        }

//        tiempoEspecial += delta;
        if (this.getVida() <= this.getVidaMaxima() / 2 && !enFuria) {
            activarFuria();
        }

//        if (tiempoEspecial >= 8f) {
//            usarAtaqueEspecial(posicionJugador);
//            tiempoEspecial = 0;
//        }

        return false;
    }

    private void activarFuria() {
        enFuria = true;
        Gdx.app.log("Boss", "¡El jefe entra en modo furia!");
//        float velocidadBoss = TipoEnemigoVelocidad.BOSS.getVelocidad();
//        this.velocidad.set(velocidadBoss, velocidadBoss);
    }

//    private void usarAtaqueEspecial(Vector2 jugador) {
//        Gdx.app.log("Boss", "Lanza un ataque especial hacia el jugador.");
//        // Podés cambiar su estado temporalmente a un ataque especial o crear una animación distinta
//    }
    @Override
    public Rectangle getRectangulo() {
        return new Rectangle(posicion.x, posicion.y, 6.1f, 6.1f);
    }
    // 🔹 Si necesitás redefinir alguna animación o comportamiento gráfico
//    @Override
//    protected void cargarAnimaciones() {
//        // Divide la hoja en frames de 128x128
//        TextureRegion[][] regiones = TextureRegion.split(hojaSprite, 64, 64);
//
//        // Fila 0: idle (6 frames)
//        this.animIdle = crearAnimacion(regiones[0], 6, 0.15f, Animation.PlayMode.LOOP);
//
//        // Fila 1: caminar (8 frames)
//        this.animCaminar = crearAnimacion(regiones[1], 8, 0.1f, Animation.PlayMode.LOOP);
//
//        // Fila 2: atacar (10 frames)
//        this.animAtacar = crearAnimacion(regiones[2], 10, 0.08f, Animation.PlayMode.NORMAL);
//
//        // Fila 3: recibir golpe (4 frames)
//        this.animHit = crearAnimacion(regiones[3], 4, 0.1f, Animation.PlayMode.NORMAL);
//
//        // Fila 4: morir (12 frames)
//        this.animMuerte = crearAnimacion(regiones[4], 12, 0.12f, Animation.PlayMode.NORMAL);
//    }
    @Override
    public void renderizar(SpriteBatch batch) {
        TextureRegion frame = obtenerFrameActual();
        float width = 6f;  // Boss más grande que enemigo normal
        float height = 6f;
        float drawX = this.posicion.x;
        float drawY = this.posicion.y;

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
            case MUERTE:
                return animMuerte.getKeyFrame(tiempoEstado);
            case ESTATICO:
            default:
                return animIdle.getKeyFrame(tiempoEstado);
        }
    }

}
