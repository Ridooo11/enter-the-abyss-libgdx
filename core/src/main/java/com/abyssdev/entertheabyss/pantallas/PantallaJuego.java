package com.abyssdev.entertheabyss.pantallas;

import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;
import com.abyssdev.entertheabyss.mapas.Mapa;
import com.abyssdev.entertheabyss.mapas.Sala;
import com.abyssdev.entertheabyss.mapas.ZonaTransicion;
import com.abyssdev.entertheabyss.logica.ManejoEntradas;
import com.abyssdev.entertheabyss.personajes.Enemigo;
import com.abyssdev.entertheabyss.personajes.Jugador;
import com.abyssdev.entertheabyss.ui.Hud;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class PantallaJuego extends Pantalla {

    private OrthographicCamera camara;
    private Viewport viewport;
    private Jugador jugador;
    private Mapa mapaActual;
    private Sala salaActual;

    private ArrayList<Enemigo> enemigos;

    // ✅ Fade entre salas
    private boolean enTransicion = false;
    private float fadeAlpha = 0f;
    private float fadeSpeed = 2f; // Ajusta para hacerlo más rápido o lento
    private String salaDestinoId = null;

    // ✅ HUD
    private Hud hud;
    private boolean yaInicializado = false;

    public PantallaJuego(EnterTheAbyssPrincipal juego) {
        super(juego);
    }

    @Override
    public void show() {
        if (!yaInicializado) {
            jugador = new Jugador();

            mapaActual = new Mapa("mazmorra1");
            mapaActual.agregarSala(new Sala("sala1", "maps/mapa1_sala1.tmx"));
            mapaActual.agregarSala(new Sala("sala2", "maps/mapa1_sala2.tmx"));

            camara = new OrthographicCamera();
            viewport = new FitViewport(16,16 * (9f / 16f), camara);

            cambiarSala("sala1");
            generarEnemigos();

            hud = new Hud(jugador, viewport);
            Gdx.input.setInputProcessor(new ManejoEntradas(jugador));

            yaInicializado = true;
        } else {
            Gdx.input.setInputProcessor(new ManejoEntradas(jugador));
            actualizarCamara();
        }
    }

    private void generarEnemigos() {
        enemigos = new ArrayList<Enemigo>();
        float anchoSala = salaActual.getAnchoMundo();
        float altoSala = salaActual.getAltoMundo();

        int cantidadEnemigos = 5; // Puedes variar por sala usando propiedades en Tiled más adelante
        for (int i = 0; i < cantidadEnemigos; i++) {
            float x = MathUtils.random(2f, anchoSala - 2f);
            float y = MathUtils.random(2f, altoSala - 2f);
            enemigos.add(new Enemigo(x, y));
        }
    }

    private void cambiarSala(String destinoId) {
        Sala salaDestino = mapaActual.getSala(destinoId);
        if (salaDestino == null) {
            Gdx.app.error("PantallaJuego", "Sala destino no encontrada: " + destinoId);
            return;
        }

        mapaActual.establecerSalaActual(destinoId);
        salaActual = salaDestino;

        // ✅ Centrar jugador en la sala (por ahora, luego con spawn point)
        centrarJugadorEnSala();

        // ✅ Calcular tamaño del viewport basado en la sala
        float anchoSala = salaActual.getAnchoMundo();
        float altoSala = salaActual.getAltoMundo();

        float aspectRatio = 16f / 9f;

        float viewportHeight = altoSala;
        float viewportWidth = viewportHeight * aspectRatio;

        if (viewportWidth > anchoSala) {
            viewportWidth = anchoSala;
            viewportHeight = viewportWidth / aspectRatio;
        }


        viewport.update((int)viewportWidth, (int)viewportHeight, true);


        camara.position.set(jugador.getX(), jugador.getY(), 0);
        camara.update();


        generarEnemigos();
    }

    private void centrarJugadorEnSala() {
        float centroX = salaActual.getAnchoMundo() / 2f;
        float centroY = salaActual.getAltoMundo() / 2f;
        jugador.setX(centroX);
        jugador.setY(centroY);
    }

    private void verificarTransiciones() {
        if (enTransicion) return;

        Rectangle hitboxJugador = jugador.getHitbox();

        for (ZonaTransicion zona : salaActual.getZonasTransicion()) {
            if (hitboxJugador.overlaps(zona)) {
                String destinoId = zona.destinoSalaId;
                Sala salaDestino = mapaActual.getSala(destinoId);

                if (salaDestino != null) {
                    // ✅ INICIAR FADE OUT
                    enTransicion = true;
                    salaDestinoId = destinoId;
                    fadeAlpha = 0f;
                    break;
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        salaActual.getRenderer().setView(camara);
        salaActual.getRenderer().render();

        // Actualizar enemigos
        for (int i = enemigos.size() - 1; i >= 0; i--) {
            Enemigo enemigo = enemigos.get(i);
            if (enemigo.actualizar(delta, jugador.getPosicion(), salaActual.getColisiones(), enemigos)) {
                jugador.recibirDanio(10);
                if (jugador.getVida() <= 0) {
                    juego.setScreen(new PantallaGameOver(juego));
                    return;
                }
            }
            if (enemigo.debeEliminarse()) {
                enemigos.remove(i);
            }
        }

        // Actualizar jugador
        jugador.update(delta, salaActual.getColisiones());

        // Verificar transiciones
        verificarTransiciones();

        // Manejar fade
        if (enTransicion) {
            if (fadeAlpha < 1f) {
                // FADE OUT
                fadeAlpha += fadeSpeed * delta;
                if (fadeAlpha >= 1f) {
                    fadeAlpha = 1f;
                    // ✅ CAMBIAR SALA CUANDO ESTÉ NEGRO
                    cambiarSala(salaDestinoId);
                }
            } else {
                // FADE IN
                fadeAlpha -= fadeSpeed * delta;
                if (fadeAlpha <= 0f) {
                    fadeAlpha = 0f;
                    enTransicion = false;
                    salaDestinoId = null;
                }
            }
        }


        actualizarCamara();


        juego.batch.setProjectionMatrix(camara.combined);
        juego.batch.begin();

        // Dibujar enemigos
        for (Enemigo enemigo : enemigos) {
            enemigo.renderizar(juego.batch);
        }

        // Dibujar jugador
        jugador.dibujar(juego.batch);

        // ✅ DIBUJAR FADE OVERLAY
        if (enTransicion) {
            juego.batch.setColor(0, 0, 0, fadeAlpha);
           // juego.batch.draw(juego.texturaBlanca, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
            juego.batch.setColor(1, 1, 1, 1); // Reset color
        }

        juego.batch.end();


        if (hud != null) {
            hud.update();
            hud.draw(juego.batch);
        }

        // Controles (bloqueados durante fade)
        if (!enTransicion) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                juego.setScreen(new PantallaPausa(juego, this));
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                juego.setScreen(new PantallaArbolHabilidades(juego, this, jugador));
            }
        }
    }

    private void actualizarCamara() {
        float x = jugador.getX();
        float y = jugador.getY();

        float halfWidth = camara.viewportWidth / 2f;
        float halfHeight = camara.viewportHeight / 2f;

        float limiteDerecho = salaActual.getAnchoMundo() - halfWidth;
        float limiteSuperior = salaActual.getAltoMundo() - halfHeight;

        x = Math.max(halfWidth, Math.min(x, limiteDerecho));
        y = Math.max(halfHeight, Math.min(y, limiteSuperior));

        camara.position.set(x, y, 0);
        camara.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        actualizarCamara();
    }

    @Override
    public void hide() {
        // No destruir nada aquí
    }

    @Override
    public void dispose() {
        if (mapaActual != null) {
            mapaActual.dispose();
        }
        if (hud != null) {
            hud.dispose();
        }
        if (jugador != null) {
            jugador.dispose();
        }
        // Enemigos no tienen dispose() por defecto, pero si usan texturas, deberías agregarlo
    }
}
