package com.abyssdev.entertheabyss.pantallas;

import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;
import com.abyssdev.entertheabyss.logica.ManejoEntradas;
import com.abyssdev.entertheabyss.personajes.Enemigo;
import com.abyssdev.entertheabyss.personajes.Jugador;
import com.abyssdev.entertheabyss.ui.Hud; // ✅ Import del HUD
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
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
    private AssetManager assetManager;
    private TiledMap mapa;
    private int mapaAncho;
    private int mapaAlto;
    private OrthogonalTiledMapRenderer renderer;
    private ArrayList<Enemigo> enemigos;
    private Array<Rectangle> rectangulosColision = new Array<>();

    private final float TILE_SIZE = 16f;

    // ✅ NUEVO: Instancia del HUD
    private Hud hud;

    public PantallaJuego(EnterTheAbyssPrincipal juego) {
        super(juego);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camara);
        renderer.render();

        // COLISIONES
        MapObjects objetos = mapa.getLayers().get("colisiones").getObjects();

        for (MapObject objeto : objetos) {
            if (!(objeto instanceof RectangleMapObject)) continue;

            if (objeto.getProperties().containsKey("colision") && objeto.getProperties().get("colision", Boolean.class)) {
                Rectangle rectOriginal = ((RectangleMapObject) objeto).getRectangle();
                Rectangle rectEscalado = new Rectangle(
                    rectOriginal.x / TILE_SIZE,
                    rectOriginal.y / TILE_SIZE,
                    rectOriginal.width / TILE_SIZE,
                    rectOriginal.height / TILE_SIZE
                );
                rectangulosColision.add(rectEscalado);
            }
        }


        for (int i = enemigos.size() - 1; i >= 0; i--) {
            Enemigo enemigo = enemigos.get(i);
            enemigo.actualizar(delta, jugador.getPosicion(), rectangulosColision, enemigos);
            if (enemigo.debeEliminarse()) {
                enemigos.remove(i);
            }
        }

        jugador.update(delta, rectangulosColision); // Pasamos el ArrayList con los objetos que son colisionables

        jugador.update(delta, rectangulosColision);

        actualizarCamara();

        juego.batch.setProjectionMatrix(camara.combined);
        juego.batch.begin();
        for (Enemigo enemigo : enemigos) {
            enemigo.renderizar(juego.batch);
        }
        jugador.dibujar(juego.batch);
        juego.batch.end();

        // ✅ DIBUJAR EL HUD (siempre al final, encima de todo)
        if (hud != null) {
            hud.update();
            hud.draw(juego.batch);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(new PantallaPausa(juego, this));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            juego.setScreen(new PantallaArbolHabilidades(juego, this, jugador));
        }
    }

    @Override
    public void show() {
        renderer = new OrthogonalTiledMapRenderer(crearMapa("maps/sala1.tmx"), 1 / TILE_SIZE);
        camara = new OrthographicCamera();

        TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
        mapaAncho = capa.getWidth();
        mapaAlto = capa.getHeight();
        enemigos = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            float x = MathUtils.random(1f, mapaAncho - 2f);
            float y = MathUtils.random(1f, mapaAlto - 2f);
            enemigos.add(new Enemigo(x, y));
        }

        float aspectRatio = 16f / 9f;
        float viewportHeight = mapaAlto;
        float viewportWidth = viewportHeight * aspectRatio;

        if (viewportWidth > mapaAncho) {
            viewportWidth = mapaAncho;
            viewportHeight = viewportWidth / aspectRatio;
        }

        viewport = new FitViewport(viewportWidth, viewportHeight, camara);

        if (jugador == null) {
            float mapaCentroX = mapaAncho / 2f;
            float mapaCentroY = mapaAlto / 2f;
            jugador = new Jugador();
            jugador.setX(mapaCentroX);
            jugador.setY(mapaCentroY);
        }

        camara.position.set(jugador.getX(), jugador.getY(), 0);
        camara.update();

        Gdx.input.setInputProcessor(new ManejoEntradas(jugador));

        // ✅ Inicializar el HUD con el viewport
        hud = new Hud(jugador, viewport);
    }

    @Override
    public void hide() {
        // No hacer nada aquí para no perder el estado del juego
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        actualizarCamara();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        mapa.dispose();
        assetManager.dispose();
        jugador.dispose();

        // ✅ LIBERAR RECURSOS DEL HUD
        if (hud != null) {
            hud.dispose();
        }
    }

    private TiledMap crearMapa(String rutaArchivo) {
        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load(rutaArchivo, TiledMap.class);
        assetManager.finishLoading();
        mapa = assetManager.get(rutaArchivo);
        return mapa;
    }

    private void actualizarCamara() {
        float x = jugador.getX();
        float y = jugador.getY();

        float halfWidth = camara.viewportWidth / 2f;
        float halfHeight = camara.viewportHeight / 2f;

        x = Math.max(halfWidth, x);
        x = Math.min(mapaAncho - halfWidth, x);

        y = Math.max(halfHeight, y);
        y = Math.min(mapaAlto - halfHeight, y);

        camara.position.set(x, y, 0);
        camara.update();
    }
}
