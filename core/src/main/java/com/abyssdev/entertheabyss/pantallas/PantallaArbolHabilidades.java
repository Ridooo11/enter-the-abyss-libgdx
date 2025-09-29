package com.abyssdev.entertheabyss.pantallas;

import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;
import com.abyssdev.entertheabyss.habilidades.Habilidad;
import com.abyssdev.entertheabyss.personajes.Jugador;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
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

import java.util.HashMap;
import java.util.Map;

public class PantallaArbolHabilidades extends Pantalla {

    private final PantallaJuego pantallaJuego;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Texture fondo;
    private Jugador jugador;
    private Map<String, Habilidad> habilidades = new HashMap<>();
    private int filaSeleccionada = 0;
    private int columnaSeleccionada = 0;
    private final int MAX_FILAS = 3;
    private final int MAX_COLUMNAS = 2;

    private float tiempoParpadeo = 0;
    private boolean mostrarColor = true;
    private GlyphLayout layout;

    // ✅ Nuevas variables para viewport y cámara
    private Viewport viewport;
    private OrthographicCamera camara;

    public PantallaArbolHabilidades(EnterTheAbyssPrincipal juego, PantallaJuego pantallaJuego, Jugador jugador) {
        super(juego);
        this.pantallaJuego = pantallaJuego;
        this.jugador = jugador;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        layout = new GlyphLayout();

        // Cargar el fondo
        fondo = new Texture("Fondos/FondoArbol.PNG");

        // Inicializar las habilidades
        habilidades.put("Vida", new Habilidad("Vida Extra", "Aumenta la salud máxima del jugador.", 50, "imagenes/corazon.png"));
        habilidades.put("Defensa", new Habilidad("Defensa", "Reduce el daño recibido.", 100, "imagenes/escudo.png"));
        habilidades.put("Regen", new Habilidad("Regeneración", "Regenera salud lentamente.", 150, "imagenes/corazonDorado.PNG"));
        habilidades.put("Ataque", new Habilidad("Fuerza", "Aumenta el daño de ataque.", 50, "imagenes/espada.PNG"));
        habilidades.put("Critico", new Habilidad("Ataque Veloz", "Aumenta la velocidad de ataque.", 100, "imagenes/espadaDoble.PNG"));
        habilidades.put("Combo", new Habilidad("Golpe Crítico", "Aumenta mas el daño de ataque.", 150, "imagenes/espadaRoja.PNG"));
        habilidades.put("Velocidad", new Habilidad("Velocidad", "Aumenta la velocidad de movimiento.", 50, "imagenes/botas.PNG"));
        habilidades.put("Evasion", new Habilidad("Velocidad II", "Aumenta la velocidad de movimiento.", 100, "imagenes/botas2.PNG"));
        habilidades.put("Rapidez", new Habilidad("Evasión", "Nueva habilidad de rodar.", 150, "imagenes/botasDoradas.PNG"));

        // Simular que algunas habilidades ya están desbloqueadas
        habilidades.get("Vida").desbloqueado = true;
        habilidades.get("Ataque").desbloqueado = true;
        habilidades.get("Velocidad").desbloqueado = true;

        // ✅ Configurar viewport y cámara
        camara = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camara); // Resolución base
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

        // ✅ Aplicar viewport
        viewport.apply();
        batch.setProjectionMatrix(camara.combined);

        // Dibujar el fondo
        batch.begin();
        batch.draw(fondo, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        // Dibujar la interfaz del árbol de habilidades
        dibujarArbolHabilidades();

        // Dibujar texto de información
        batch.begin();
        // Título
        font.getData().setScale(2.0f);
        font.setColor(Color.WHITE);
        layout.setText(font, "Árbol de Habilidades");
        font.draw(batch, layout, (viewport.getWorldWidth() - layout.width) / 2, viewport.getWorldHeight() - 40);

        // Nivel y puntos de habilidad del jugador (simulados)
        // Mostrar monedas y nivel del jugador
        font.getData().setScale(1.2f);
        font.setColor(Color.YELLOW);
        font.draw(batch, "Monedas: " + jugador.getMonedas(), 50, 30); // ✅ Monedas reales
        font.draw(batch, "Nivel: " + 5, 50, 50);

        batch.end();
    }

    private void dibujarArbolHabilidades() {
        float anchoPantalla = viewport.getWorldWidth();
        float altoPantalla = viewport.getWorldHeight();
        float margen = 50f;
        float anchoColumna = (anchoPantalla - margen * 4) / 3;
        float altoNodo = 90f;
        float espacioVertical = (altoPantalla - 200 - altoNodo * 3) / 2;

        // Necesario para que la transparencia funcione correctamente
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(camara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dibujar el fondo de las columnas
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.6f);
        for (int i = 0; i < 3; i++) {
            float x = margen + i * (anchoColumna + margen);
            shapeRenderer.rect(x, margen + 20, anchoColumna, altoPantalla - 150);
        }
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND); // Deshabilitar el blending después de usarlo

        // Iniciar el ShapeRenderer para las líneas de conexión
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.DARK_GRAY);

        // Dibujar líneas de conexión
        float[] centroX = new float[3];
        for (int i = 0; i < 3; i++) {
            centroX[i] = margen + i * (anchoColumna + margen) + anchoColumna / 2;
        }

        float centroY_Nivel0 = altoPantalla - 100 - altoNodo / 2;
        float centroY_Nivel1 = centroY_Nivel0 - espacioVertical - altoNodo;
        float centroY_Nivel2 = centroY_Nivel1 - espacioVertical - altoNodo;

        // Líneas entre nivel 0 y 1
        for (int i = 0; i < 3; i++) {
            shapeRenderer.line(centroX[i], centroY_Nivel0, centroX[i], centroY_Nivel1);
        }

        // Líneas entre nivel 1 y 2
        for (int i = 0; i < 3; i++) {
            shapeRenderer.line(centroX[i], centroY_Nivel1, centroX[i], centroY_Nivel2);
        }

        shapeRenderer.end();

        batch.begin();

        // Dibujar los nodos de habilidad
        dibujarNodo(0, 0, "Vida");
        dibujarNodo(1, 0, "Ataque");
        dibujarNodo(2, 0, "Velocidad");

        dibujarNodo(0, 1, "Defensa");
        dibujarNodo(1, 1, "Critico");
        dibujarNodo(2, 1, "Evasion");

        dibujarNodo(0, 2, "Regen");
        dibujarNodo(1, 2, "Combo");
        dibujarNodo(2, 2, "Rapidez");

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

        // Dibujar el icono
        Color color = Color.WHITE;
        if (columna == columnaSeleccionada && fila == filaSeleccionada) {
            color = mostrarColor ? Color.YELLOW : Color.WHITE;
        } else if (!habilidad.desbloqueado) {
            color = Color.DARK_GRAY;
        }

        batch.setColor(color);
        batch.draw(habilidad.getIcono(), x, y, altoNodo, altoNodo);
        batch.setColor(Color.WHITE); // Resetear el color

        // Dibujar el nombre de la habilidad debajo
        font.getData().setScale(0.8f);
        layout.setText(font, habilidad.getNombre());
        font.draw(batch, layout, x + (altoNodo - layout.width) / 2, y - 10);
    }

    private void manejarInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            filaSeleccionada = Math.max(0, filaSeleccionada - 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            filaSeleccionada = Math.min(MAX_FILAS - 1, filaSeleccionada + 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            columnaSeleccionada = Math.max(0, columnaSeleccionada - 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            columnaSeleccionada = Math.min(MAX_COLUMNAS, columnaSeleccionada + 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            String[] ids = {"Vida", "Ataque", "Velocidad", "Defensa", "Critico", "Evasion", "Regen", "Combo", "Rapidez"};
            String habilidadId = ids[columnaSeleccionada + filaSeleccionada * (MAX_COLUMNAS + 1)];
            Habilidad habilidad = habilidades.get(habilidadId);

            if (habilidad != null && habilidad.desbloqueado && habilidad.nivelActual < habilidad.nivelMaximo) {
                Gdx.app.log("Habilidades", "Habilidad comprada: " + habilidad.getNombre());
                habilidad.nivelActual++;
            } else if (habilidad != null && !habilidad.desbloqueado) {
                Gdx.app.log("Habilidades", "Habilidad bloqueada: " + habilidad.getNombre());
            } else if (habilidad != null) {
                Gdx.app.log("Habilidades", "Habilidad en nivel máximo: " + habilidad.getNombre());
            }
        }

        // Volver al juego
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(pantallaJuego);
        }
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
        for (Habilidad h : habilidades.values()) {
            if (h.getIcono() != null) {
                h.getIcono().dispose();
            }
        }
    }
}
