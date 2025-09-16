package com.abyssdev.entertheabyss.mapas;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;

public class Sala {

    private String id;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer renderer;
    private Array<Rectangle> colisiones;
    private Array<ZonaTransicion> zonasTransicion;
    private float anchoTiles, altoTiles;
    private static final float TILE_SIZE = 16f;

    public Sala(String id, String rutaTmx) {
        this.id = id;
        cargarMapa(rutaTmx);
        cargarColisiones();
        cargarZonasTransicion();
    }

    private void cargarMapa(String ruta) {
        TmxMapLoader loader = new TmxMapLoader();
        mapa = loader.load(ruta);
        renderer = new OrthogonalTiledMapRenderer(mapa, 1f / TILE_SIZE);

        com.badlogic.gdx.maps.tiled.TiledMapTileLayer capaBase =
            (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) mapa.getLayers().get(0);
        anchoTiles = capaBase.getWidth();
        altoTiles = capaBase.getHeight();
    }

    private void cargarColisiones() {
        colisiones = new Array<Rectangle>();
        MapObjects objetos = mapa.getLayers().get("colisiones").getObjects();

        for (MapObject objeto : objetos) {
            if (!(objeto instanceof RectangleMapObject)) continue;
            if (!objeto.getProperties().containsKey("colision")) continue;

            RectangleMapObject rectObj = (RectangleMapObject) objeto;
            Rectangle rect = rectObj.getRectangle();
            Rectangle rectEscalado = new Rectangle(
                rect.x / TILE_SIZE,
                rect.y / TILE_SIZE,
                rect.width / TILE_SIZE,
                rect.height / TILE_SIZE
            );
            colisiones.add(rectEscalado);
        }
    }

    private void cargarZonasTransicion() {
        zonasTransicion = new Array<ZonaTransicion>();
        MapObjects objetos = null;

        // Verificar si existe la capa "transiciones"
        if (mapa.getLayers().get("transiciones") != null) {
            objetos = mapa.getLayers().get("transiciones").getObjects();
        } else {
            return; // No hay transiciones definidas
        }

        for (MapObject objeto : objetos) {
            if (!(objeto instanceof RectangleMapObject)) continue;
            if (!objeto.getProperties().containsKey("destino")) continue;

            RectangleMapObject rectObj = (RectangleMapObject) objeto;
            Rectangle rect = rectObj.getRectangle();
            String destino = objeto.getProperties().get("destino", String.class);

            ZonaTransicion zona = new ZonaTransicion(
                rect.x / TILE_SIZE,
                rect.y / TILE_SIZE,
                rect.width / TILE_SIZE,
                rect.height / TILE_SIZE,
                destino
            );
            zonasTransicion.add(zona);
        }
    }

    // GETTERS
    public String getId() { return id; }
    public TiledMap getMapa() { return mapa; }
    public OrthogonalTiledMapRenderer getRenderer() { return renderer; }
    public Array<Rectangle> getColisiones() { return colisiones; }
    public Array<ZonaTransicion> getZonasTransicion() { return zonasTransicion; }
    public float getAnchoMundo() { return anchoTiles; }
    public float getAltoMundo() { return altoTiles; }

    public void dispose() {
        if (mapa != null) mapa.dispose();
        if (renderer != null) renderer.dispose();
    }
}
