package com.abyssdev.entertheabyss.pantallas;

import com.abyssdev.entertheabyss.EnterTheAbyssPrincipal;
import com.abyssdev.entertheabyss.logica.ManejoEntradas;
import com.abyssdev.entertheabyss.personajes.Jugador;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PantallaJuego extends Pantalla {

    private OrthographicCamera camara;
    private Viewport viewport;
    private Jugador jugador;
    private AssetManager assetManager;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer renderer;

    private final float TILE_SIZE = 16f; // en píxeles

    public PantallaJuego(EnterTheAbyssPrincipal juego) {
        super(juego);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camara);
        renderer.render();

        TiledMapTileLayer capaColisionable = (TiledMapTileLayer) mapa.getLayers().get("tileColision");

        jugador.update(delta, capaColisionable);
        actualizarCamara();

        juego.batch.setProjectionMatrix(camara.combined);
        juego.batch.begin();
        jugador.dibujar(juego.batch);
        juego.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(new PantallaPausa(juego, this));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        actualizarCamara();
    }

    @Override
    public void show() {
        // Cargamos el mapa. Creamos el render y la camara.
        renderer = new OrthogonalTiledMapRenderer(crearMapa("maps/sala1.tmx"), 1 / TILE_SIZE);
        camara = new OrthographicCamera();

        TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
        int mapaAncho = capa.getWidth();
        int mapaAlto = capa.getHeight();

        // Usamos FitViewport basado en tamaño del mapa
        float aspectRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        float viewportHeight = mapaAlto;
        float viewportWidth = viewportHeight * aspectRatio;

        if (viewportWidth > mapaAncho) {
            viewportWidth = mapaAncho;
            viewportHeight = viewportWidth / aspectRatio;
        }

        viewport = new FitViewport(viewportWidth, viewportHeight, camara);

        float mapaCentroX = mapaAncho / 2f;
        float mapaCentroY = mapaAlto / 2f;

        jugador = new Jugador();
        jugador.setX(mapaCentroX);
        jugador.setY(mapaCentroY);

        camara.position.set(jugador.getX(), jugador.getY(), 0);
        camara.update();

        Gdx.input.setInputProcessor(new ManejoEntradas(jugador));

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        mapa.dispose();
        assetManager.dispose();
        jugador.dispose();
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
        TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
        int mapaAncho = capa.getWidth();
        int mapaAlto = capa.getHeight();

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
