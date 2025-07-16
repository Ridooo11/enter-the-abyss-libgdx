package com.abyssdev.entertheabyss.pantallas;

import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;
import com.abyssdev.entertheabyss.logica.ManejoEntradas;
import com.abyssdev.entertheabyss.personajes.Jugador;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PantallaJuego extends Pantalla {

    public PantallaJuego(EnterTheAbyssPrincipal juego) {
        super(juego);
    }

    private OrthographicCamera camara;
    private Viewport viewport;
    private Jugador jugador;
    private AssetManager assetManager;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer renderer;

    private final float TILE_SIZE = 16f;


    private final int MAP_WIDTH_TILES = 30;
    private final int MAP_HEIGHT_TILES = 30;
    private final int SALA_X = 0;
    private final int SALA_Y = MAP_HEIGHT_TILES - 12;
    private final int SALA_ANCHO = 12;
    private final int SALA_ALTO = 12;

    @Override
    public void show() {

        camara = new OrthographicCamera();

        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("maps/sala.tmx", TiledMap.class);
        assetManager.finishLoading();

        mapa = assetManager.get("maps/sala.tmx");


        float aspectRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();


        float viewportHeight = SALA_ALTO;
        float viewportWidth = viewportHeight * aspectRatio;


        if (viewportWidth > SALA_ANCHO) {
            viewportWidth = SALA_ANCHO;
            viewportHeight = viewportWidth / aspectRatio;
        }

        viewport = new FitViewport(viewportWidth, viewportHeight, camara);


        jugador = new Jugador();
        jugador.setX(SALA_X + 1f);
        jugador.setY(SALA_Y + 1f);


        camara.position.set(SALA_X + SALA_ANCHO / 2f, SALA_Y + SALA_ALTO / 2f, 0);
        camara.update();

        renderer = new OrthogonalTiledMapRenderer(mapa, 1 / TILE_SIZE);

        Gdx.input.setInputProcessor(new ManejoEntradas(jugador));
    }

    @Override
    public void render(float delta) {
        jugador.actualizarMovimiento(delta);
        actualizarCamara();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camara);
        renderer.render();

        juego.batch.setProjectionMatrix(camara.combined);
        juego.batch.begin();
        jugador.dibujar(juego.batch);
        juego.batch.end();
    }

    private void actualizarCamara() {
        float x = jugador.getX();
        float y = jugador.getY();

        float viewportHalfWidth = camara.viewportWidth / 2f;
        float viewportHalfHeight = camara.viewportHeight / 2f;


        x = Math.max(x, SALA_X + viewportHalfWidth);
        x = Math.min(x, SALA_X + SALA_ANCHO - viewportHalfWidth);

        y = Math.max(y, SALA_Y + viewportHalfHeight);
        y = Math.min(y, SALA_Y + SALA_ALTO - viewportHalfHeight);

        camara.position.set(x, y, 0);
        camara.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        renderer.dispose();
        mapa.dispose();
        assetManager.dispose();
        jugador.dispose();
    }
}
