package com.abyssdev.entertheabyss.pantallas;

import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;
import com.abyssdev.entertheabyss.mapas.Mapa;
import com.abyssdev.entertheabyss.mapas.Sala;
import com.abyssdev.entertheabyss.mapas.SpawnPoint;
import com.abyssdev.entertheabyss.mapas.ZonaTransicion;
import com.abyssdev.entertheabyss.logica.ManejoEntradas;
import com.abyssdev.entertheabyss.personajes.Enemigo;
import com.abyssdev.entertheabyss.personajes.Jugador;
import com.abyssdev.entertheabyss.ui.Hud;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.abyssdev.entertheabyss.ui.Sonidos;

import java.util.ArrayList;

public class PantallaJuego extends Pantalla {

    private OrthographicCamera camara;
    private Viewport viewport;
    private Jugador jugador;
    private Mapa mapaActual;
    private Sala salaActual;
    private ManejoEntradas inputProcessor;

    // Transicion
    private boolean enTransicion = false;
    private boolean faseSubida = true;
    private float fadeAlpha = 0f;
    private float fadeSpeed = 2f;
    private String salaDestinoId = null;
    private Texture texturaFade;

    // HUD
    private Hud hud;
    private boolean yaInicializado = false;

    public PantallaJuego(Game juego, SpriteBatch batch) {
        super(juego, batch);
    }
    //cambio a game, agregar por constructor el batch.
    //
     //
     //
     //
    @Override
    public void show() {
        if (!yaInicializado) {
            jugador = new Jugador();
            mapaActual = new Mapa("mazmorra1");
            mapaActual.agregarSala(new Sala("sala1", "maps/mapa1_sala1.tmx"));
            mapaActual.agregarSala(new Sala("sala2", "maps/mapa1_sala2.tmx"));
            //mapaActual.agregarSala(new Sala("sala3", "maps/mapa1_sala3.tmx"));

            camara = new OrthographicCamera();
            viewport = new FitViewport(32, 32 * (9f / 16f), camara);
            texturaFade = generarTextura();
            cambiarSala("sala1");
            hud = new Hud(jugador, viewport);
            inputProcessor = new ManejoEntradas(jugador);
            yaInicializado = true;
        } else {
            actualizarCamara();
        }
        Gdx.input.setInputProcessor(inputProcessor);
        Sonidos.reproducirMusicaJuego();
    }

    private void cambiarSala(String destinoId) {
        Sala salaDestino = mapaActual.getSala(destinoId);
        if (salaDestino == null) {
            Gdx.app.error("PantallaJuego", "Sala destino no encontrada: " + destinoId);
            return;
        }

        Sala salaAnterior = salaActual;
        salaActual = salaDestino;
        mapaActual.establecerSalaActual(destinoId);

        if (enTransicion && salaDestinoId != null) {
            for (ZonaTransicion zona : salaAnterior.getZonasTransicion()) {
                if (zona.destinoSalaId.equals(destinoId)) {
                    SpawnPoint spawn = null;
                    for (SpawnPoint sp : salaDestino.getSpawnPoints()) {
                        if (sp.name.equals(zona.spawnName) && sp.salaId.equals(destinoId)) {
                            spawn = sp;
                            break;
                        }
                    }

                    if (spawn != null) {
                        jugador.setX(spawn.x);
                        jugador.setY(spawn.y);
                    } else {
                        if (!salaDestino.getSpawnPoints().isEmpty()) {
                            SpawnPoint fallback = salaDestino.getSpawnPoints().first();
                            jugador.setX(fallback.x);
                            jugador.setY(fallback.y);
                        } else {
                            centrarJugadorEnSala();
                        }
                    }
                    break;
                }
            }
        } else {
            SpawnPoint defaultSpawn = null;
            for (SpawnPoint sp : salaDestino.getSpawnPoints()) {
                if (sp.name.equals("default") && sp.salaId.equals(destinoId)) {
                    defaultSpawn = sp;
                    break;
                }
            }
            if (defaultSpawn != null) {
                jugador.setX(defaultSpawn.x);
                jugador.setY(defaultSpawn.y);
            } else {
                centrarJugadorEnSala();
            }
        }

        camara.position.set(jugador.getX(), jugador.getY(), 0);
        camara.update();
        salaActual.getRenderer().setView(camara);

        if (salaActual.getEnemigos() == null || salaActual.getEnemigos().isEmpty()) {
            salaActual.generarEnemigos(5);
        }
    }

    private void centrarJugadorEnSala() {
        float centroX = salaActual.getAnchoMundo() / 2f;
        float centroY = salaActual.getAltoMundo() / 2f;
        jugador.setX(centroX);
        jugador.setY(centroY);
    }

    private void verificarTransiciones() {
        if (enTransicion) return;

        Rectangle hitboxJugador = new Rectangle(
            jugador.getX() + jugador.getAncho() / 4f,
            jugador.getY(),
            jugador.getAncho() / 2f,
            jugador.getAlto()
        );

        for (ZonaTransicion zona : salaActual.getZonasTransicion()) {
            if (hitboxJugador.overlaps(zona)) {
//                if (!zona.pasaMapa) {
//                    return;
//                }
                if (salaActual.hayEnemigosVivos()) {
                    System.out.println("No se ha matado a todos los enemigos");
                    //Se podria mostrar algun mensaje al jugador
                    return;
                }
                String destinoId = zona.destinoSalaId;
                Sala salaDestino = mapaActual.getSala(destinoId);

                if (salaDestino != null) {
                    enTransicion = true;
                    faseSubida = true;
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

        ArrayList<Enemigo> enemigos = salaActual.getEnemigos();
        if (enemigos != null) {
            for (int i = enemigos.size() - 1; i >= 0; i--) {
                Enemigo enemigo = enemigos.get(i);
                if (enemigo.debeEliminarse()) {
                    jugador.modificarMonedas(10);
                    System.out.println("✅ Enemigo eliminado. Jugador recibe 10 monedas.");
                    enemigos.remove(i);
                    continue;
                }

                if (enemigo.actualizar(delta, jugador.getPosicion(), salaActual.getColisiones(), enemigos)) {
                    jugador.recibirDanio(10);
                    if (jugador.getVida() <= 0) {
                        Sonidos.reproducirMusicaDerrota(); // ✅ Reproducir música de derrota
                        juego.setScreen(new PantallaGameOver(juego,batch));
                        return;
                    }
                }
            }

            // ✅ Tercero: verificar ataque del jugador
            if (jugador.getHitboxAtaque().getWidth() > 0) {
                for (int i = enemigos.size() - 1; i >= 0; i--) {
                    Enemigo enemigo = enemigos.get(i);
                    if (!enemigo.debeEliminarse() && jugador.getHitboxAtaque().overlaps(enemigo.getRectangulo())) {
                        enemigo.recibirGolpe();
                    }
                }
            }
        }

        jugador.update(delta, salaActual.getColisiones());
        verificarTransiciones();

        if (enTransicion) {
            if (faseSubida) {
                fadeAlpha += fadeSpeed * delta;
                if (fadeAlpha >= 1f) {
                    fadeAlpha = 1f;
                    cambiarSala(salaDestinoId);
                    faseSubida = false;
                }
            } else {
                fadeAlpha -= fadeSpeed * delta;
                if (fadeAlpha <= 0f) {
                    fadeAlpha = 0f;
                    enTransicion = false;
                    salaDestinoId = null;
                }
            }
        }

        actualizarCamara();

        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        for (Enemigo enemigo : salaActual.getEnemigos()) {
            enemigo.renderizar(batch);
        }
        jugador.dibujar(batch);
        batch.end();

        if (hud != null) {
            hud.update();
            hud.draw(batch);
        }

        // Efecto FADE
        if (fadeAlpha > 0f) {
            batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.begin();

            batch.setColor(0, 0, 0, fadeAlpha);
            batch.draw(texturaFade, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


            batch.setColor(1, 1, 1, 1);

            batch.end();
        }

        if (!enTransicion) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                juego.setScreen(new PantallaPausa(juego,batch, this));
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
                juego.setScreen(new PantallaArbolHabilidades(juego,batch, this, jugador));
            }
        }
    }

    private void actualizarCamara() {
        float halfWidth = camara.viewportWidth / 2f;
        float halfHeight = camara.viewportHeight / 2f;

        float x = jugador.getX();
        float y = jugador.getY();

        float limiteIzquierdo = halfWidth;
        float limiteDerecho = Math.max(limiteIzquierdo, salaActual.getAnchoMundo() - halfWidth);
        float limiteInferior = halfHeight;
        float limiteSuperior = Math.max(limiteInferior, salaActual.getAltoMundo() - halfHeight);

        x = MathUtils.clamp(x, limiteIzquierdo, limiteDerecho);
        y = MathUtils.clamp(y, limiteInferior, limiteSuperior);

        camara.position.set(x, y, 0);
        camara.update();
    }

    public Texture generarTextura() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        Texture textura = new Texture(pixmap);
        pixmap.dispose();
        return textura;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        actualizarCamara();
    }

    @Override
    public void hide() {
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

        if (texturaFade != null) {
            texturaFade.dispose();
        }
    }
}
