package com.abyssdev.entertheabyss.pantallas;

import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;
import com.abyssdev.entertheabyss.personajes.Jugador;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import java.util.HashMap;
import java.util.Map;

public class PantallaArbolHabilidades extends Pantalla {

    private final PantallaJuego pantallaJuego;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Texture fondo;

    // Referencia al jugador para modificar sus atributos
    private Jugador jugador;

    // Estructura de datos para representar el árbol de habilidades
    private static class Habilidad {
        String nombre;
        String descripcion;
        int costo;
        Texture icono;
        // Otras propiedades como el nivel actual, si está desbloqueado, etc.
        boolean desbloqueado = false;
        int nivelActual = 0;
        int nivelMaximo = 3;

        public Habilidad(String nombre, String descripcion, int costo, String iconoPath) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.costo = costo;
            this.icono = new Texture(iconoPath);
        }
    }

    // El árbol de habilidades, dividido en 3 ramas principales
    private Map<String, Habilidad> habilidades = new HashMap<>();

    // Variables para la navegación
    private int filaSeleccionada = 0;
    private int columnaSeleccionada = 0;
    private final int MAX_FILAS = 3;
    private final int MAX_COLUMNAS = 2; // Para 3 columnas de habilidades

    private float tiempoParpadeo = 0;
    private boolean mostrarColor = true;

    private GlyphLayout layout;

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

        // Cargar el fondo y los íconos de las habilidades
        fondo = new Texture("Fondos/FondoArbol.PNG");

        // Inicializar las habilidades (usando iconos de ejemplo)
        habilidades.put("Vida", new Habilidad("Vida Extra", "Aumenta la salud máxima del jugador.", 50, "imagenes/corazon.png"));
        habilidades.put("Defensa", new Habilidad("Defensa", "Reduce el daño recibido.", 100, "imagenes/escudo.png"));
        habilidades.put("Regen", new Habilidad("Regeneración", "Regenera salud lentamente.", 150, "imagenes/corazonDorado.PNG"));
        habilidades.put("Ataque", new Habilidad("Fuerza", "Aumenta el daño de ataque.", 50, "imagenes/espada.PNG"));
        habilidades.put("Critico", new Habilidad("Ataque Veloz", "Aumenta la velocidad de ataque.", 100, "imagenes/espadaDoble.PNG"));
        habilidades.put("Combo", new Habilidad("Golpe Crítico", "Aumenta mas el daño de ataque.", 150, "imagenes/espadaRoja.PNG"));
        habilidades.put("Velocidad", new Habilidad("Velocidad", "Aumenta la velocidad de movimiento.", 50, "imagenes/botas.PNG"));
        habilidades.put("Evasion", new Habilidad("Velocidad II", "Aumenta la velocidad de movimiento.", 100, "imagenes/botas2.PNG"));
        habilidades.put("Rapidez", new Habilidad("Evasión", "Nueva habilidad de rodar.", 150, "imagenes/botasDoradas.PNG"));
        // Aquí puedes agregar más habilidades y conectarlas lógicamente.

        // Simular que algunas habilidades ya están desbloqueadas
        habilidades.get("Vida").desbloqueado = true;
        habilidades.get("Ataque").desbloqueado = true;
        habilidades.get("Velocidad").desbloqueado = true;
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

        // Dibujar el fondo
        batch.begin();
        batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Dibujar la interfaz del árbol de habilidades
        dibujarArbolHabilidades();

        // Dibujar texto de información
        batch.begin();
        // Título
        font.getData().setScale(2.0f);
        font.setColor(Color.WHITE);
        layout.setText(font, "Árbol de Habilidades");
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, Gdx.graphics.getHeight() - 40);

        // Nivel y puntos de habilidad del jugador (simulados)
        font.getData().setScale(1.2f);
        font.setColor(Color.YELLOW);
        font.draw(batch, "Puntos de Habilidad: 100", 50, 30);
        font.draw(batch, "Nivel: " + 5, 50, 50);

        batch.end();
    }

    private void dibujarArbolHabilidades() {
        float anchoPantalla = Gdx.graphics.getWidth();
        float altoPantalla = Gdx.graphics.getHeight();
        float margen = 50f;
        float anchoColumna = (anchoPantalla - margen * 4) / 3;
        float altoNodo = 90f;
        float espacioVertical = (altoPantalla - 200 - altoNodo * 3) / 2;

        // Necesario para que la transparencia funcione correctamente
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Dibujar el fondo de las columnas
        // Utiliza un color con un valor alfa (transparencia) para que se vea el fondo
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
        // Esto es una simulación. En una implementación real, se usaría un arreglo 2D de habilidades
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
        float anchoPantalla = Gdx.graphics.getWidth();
        float altoPantalla = Gdx.graphics.getHeight();
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
        batch.draw(habilidad.icono, x, y, altoNodo, altoNodo);
        batch.setColor(Color.WHITE); // Resetear el color

        // Dibujar el nombre de la habilidad debajo
        font.getData().setScale(0.8f);
        layout.setText(font, habilidad.nombre);
        font.draw(batch, layout, x + (altoNodo - layout.width) / 2, y - 10);
    }

    private void manejarInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            filaSeleccionada = (filaSeleccionada - 1 + MAX_FILAS) % MAX_FILAS;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            filaSeleccionada = (filaSeleccionada + 1) % MAX_FILAS;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            columnaSeleccionada = (columnaSeleccionada - 1 + MAX_COLUMNAS + 1) % (MAX_COLUMNAS + 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            columnaSeleccionada = (columnaSeleccionada + 1) % (MAX_COLUMNAS + 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            // Lógica para comprar la habilidad seleccionada
            // Aquí puedes acceder al jugador para restarle puntos y aplicar la mejora
            String[] ids = {"Vida", "Ataque", "Velocidad", "Defensa", "Critico", "Evasion", "Regen", "Combo", "Rapidez"};
            String habilidadId = ids[columnaSeleccionada + filaSeleccionada * (MAX_COLUMNAS + 1)];
            Habilidad habilidad = habilidades.get(habilidadId);

            // Simulación de compra
            if (habilidad.desbloqueado && habilidad.nivelActual < habilidad.nivelMaximo) {
                Gdx.app.log("Habilidades", "Habilidad comprada: " + habilidad.nombre);
                habilidad.nivelActual++;
            } else if (!habilidad.desbloqueado) {
                Gdx.app.log("Habilidades", "Habilidad bloqueada: " + habilidad.nombre);
            } else {
                Gdx.app.log("Habilidades", "Habilidad en nivel máximo: " + habilidad.nombre);
            }
        }

        // Volver al juego
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(pantallaJuego);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        fondo.dispose();
        for (Habilidad h : habilidades.values()) {
            if (h.icono != null) {
                h.icono.dispose();
            }
        }
    }
}
