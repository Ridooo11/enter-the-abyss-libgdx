package com.abyssdev.entertheabyss.pantallas;

import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;
import com.abyssdev.entertheabyss.habilidades.*;
import com.abyssdev.entertheabyss.personajes.Jugador;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Map;

public class PantallaArbolHabilidades extends Pantalla {

    private final PantallaJuego pantallaJuego;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Texture fondo;
    private Jugador jugador;
    private Map<String, Habilidad> habilidades; // Ya no es new HashMap<>()
    private int filaSeleccionada = 0;
    private int columnaSeleccionada = 0;

    private float tiempoParpadeo = 0;
    private boolean mostrarColor = true;
    private GlyphLayout layout;

    private String mensaje = "";
    private float tiempoMensaje = 0;

    private Viewport viewport;
    private OrthographicCamera camara;

    private final String[][] NODOS = {
        {"Vida",     "Ataque",   "Velocidad"},
        {"Defensa",  "Combo",    "Velocidad II"},
        {"Regen",    "Critico",  "Evasion"}
    };

    // 🔹 Constructor modificado: recibe habilidades desde PantallaJuego
    public PantallaArbolHabilidades(Game juego, SpriteBatch batch, PantallaJuego pantallaJuego, Jugador jugador, Map<String, Habilidad> habilidades) {
        super(juego, batch);
        this.pantallaJuego = pantallaJuego;
        this.jugador = jugador;
        this.habilidades = habilidades; // ← Usa las instancias existentes
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        layout = new GlyphLayout();

        fondo = new Texture("Fondos/FondoArbol.PNG");



        camara = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camara);
        viewport.apply();
        camara.position.set(camara.viewportWidth / 2f, camara.viewportHeight / 2f, 0);
        camara.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tiempoParpadeo += delta;
        if (tiempoParpadeo > 0.5f) {
            mostrarColor = !mostrarColor;
            tiempoParpadeo = 0;
        }

        manejarInput();

        viewport.apply();
        batch.setProjectionMatrix(camara.combined);

        batch.begin();
        batch.draw(fondo, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        dibujarArbolHabilidades();

        batch.begin();
        font.getData().setScale(2.0f);
        font.setColor(Color.WHITE);
        layout.setText(font, "Árbol de Habilidades");
        font.draw(batch, layout, (viewport.getWorldWidth() - layout.width) / 2, viewport.getWorldHeight() - 40);

        font.getData().setScale(1.2f);
        font.setColor(Color.YELLOW);
        font.draw(batch, "Monedas: " + jugador.getMonedas(), 50, 50);

        if (!mensaje.isEmpty()) {
            font.setColor(Color.CYAN);
            font.draw(batch, mensaje, 400, 100);
        }

        batch.end();

        if (tiempoMensaje > 0) {
            tiempoMensaje -= delta;
            if (tiempoMensaje <= 0) mensaje = "";
        }
    }

    private void dibujarArbolHabilidades() {
        float anchoPantalla = viewport.getWorldWidth();
        float altoPantalla = viewport.getWorldHeight();
        float margen = 50f;
        float anchoColumna = (anchoPantalla - margen * 4) / 3;
        float altoNodo = 90f;
        float espacioVertical = (altoPantalla - 200 - altoNodo * 3) / 2;

        shapeRenderer.setProjectionMatrix(camara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.6f);
        for (int i = 0; i < 3; i++) {
            float x = margen + i * (anchoColumna + margen);
            shapeRenderer.rect(x, margen + 20, anchoColumna, altoPantalla - 150);
        }
        shapeRenderer.end();

        batch.begin();
        dibujarNodo(0, 0, "Vida");
        dibujarNodo(1, 0, "Ataque");
        dibujarNodo(2, 0, "Velocidad");
        dibujarNodo(0, 1, "Defensa");
        dibujarNodo(1, 1, "Combo");
        dibujarNodo(2, 1, "Velocidad II");
        dibujarNodo(0, 2, "Regen");
        dibujarNodo(1, 2, "Critico");
        dibujarNodo(2, 2, "Evasion");
        batch.end();
    }

    private void dibujarNodo(int columna, int fila, String habilidadId) {
        Habilidad habilidad = habilidades.get(habilidadId);
        float anchoPantalla = viewport.getWorldWidth();
        float altoPantalla = viewport.getWorldHeight();
        float margen = 50f;
        float anchoColumna = (anchoPantalla - margen * 4) / 3;
        float altoNodo = 80f;
        float espacioVertical = (altoPantalla - 200 - altoNodo * 3) / 2;

        float x = margen + columna * (anchoColumna + margen) + (anchoColumna - altoNodo) / 2;
        float y = altoPantalla - 100 - fila * (altoNodo + espacioVertical) - altoNodo;

        Color color = Color.WHITE;
        if (columna == columnaSeleccionada && fila == filaSeleccionada)
            color = mostrarColor ? Color.YELLOW : Color.WHITE;

        batch.setColor(color);
        batch.draw(habilidad.getIcono(), x, y, altoNodo, altoNodo);
        batch.setColor(Color.WHITE);

        font.getData().setScale(0.8f);
        layout.setText(font, habilidad.getNombre());
        font.draw(batch, layout, x + (altoNodo - layout.width) / 2, y - 10);
    }

    private void manejarInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            filaSeleccionada = Math.max(0, filaSeleccionada - 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            filaSeleccionada = Math.min(NODOS.length - 1, filaSeleccionada + 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            columnaSeleccionada = Math.max(0, columnaSeleccionada - 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            columnaSeleccionada = Math.min(NODOS[0].length - 1, columnaSeleccionada + 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (filaSeleccionada >= 0 && filaSeleccionada < NODOS.length &&
                columnaSeleccionada >= 0 && columnaSeleccionada < NODOS[0].length) {

                String habilidadId = NODOS[filaSeleccionada][columnaSeleccionada];
                Habilidad habilidad = habilidades.get(habilidadId);
                if (habilidad != null) {
                    intentarCompra(habilidad);
                } else {
                    mostrarMensaje("Habilidad no disponible: " + habilidadId);
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            juego.setScreen(pantallaJuego);
        }
    }

    private void intentarCompra(Habilidad habilidad) {
        if (habilidad.comprada) {
            mostrarMensaje("Ya has comprado esta habilidad.");
            return;
        }

        if (jugador.getMonedas() < habilidad.getCosto()) {
            mostrarMensaje("Monedas insuficientes.");
            return;
        }

        jugador.modificarMonedas(-habilidad.getCosto());
        habilidad.comprada = true;
        habilidad.aplicar(jugador);
        mostrarMensaje("¡Compra exitosa! " + habilidad.getNombre() + " mejorada.");
    }

    private void mostrarMensaje(String msg) {
        mensaje = msg;
        tiempoMensaje = 2f;
        Gdx.app.log("ÁrbolHabilidades", msg);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camara.position.set(camara.viewportWidth / 2f, camara.viewportHeight / 2f, 0);
        camara.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        fondo.dispose();

    }
}
