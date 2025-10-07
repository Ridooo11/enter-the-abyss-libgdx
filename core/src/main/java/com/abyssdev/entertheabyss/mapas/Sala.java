package com.abyssdev.entertheabyss.mapas;

import com.abyssdev.entertheabyss.personajes.Enemigo;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;

public class Sala {

    private String id;
    private TiledMap mapa;
    private OrthogonalTiledMapRenderer renderer;
    private Array<Rectangle> colisiones;
    private Array<ZonaTransicion> zonasTransicion;
    private ArrayList<Enemigo> enemigos;
    private Array<SpawnPoint> spawnPoints;
    private int cantidadEnemigos;
    private boolean enemigosGenerados = false;
    private float anchoTiles, altoTiles;
    private static final float TILE_SIZE = 16f;

    public Sala(String id, String rutaTmx, int cantidadEnemigos) {
        this.id = id;
        this.cantidadEnemigos = cantidadEnemigos;
        cargarMapa(rutaTmx);
        cargarColisiones();
        cargarZonasTransicion();
        cargarSpawnPoints();
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
        colisiones = new Array<>();
        MapObjects objetos = mapa.getLayers().get("colisiones").getObjects();

        for (MapObject objeto : objetos) {
            if (!(objeto instanceof RectangleMapObject)) continue;
            if (!objeto.getProperties().containsKey("colision")) continue;

            RectangleMapObject rectObj = (RectangleMapObject) objeto;
            Rectangle rect = rectObj.getRectangle();
            colisiones.add(new Rectangle(
                rect.x / TILE_SIZE,
                rect.y / TILE_SIZE,
                rect.width / TILE_SIZE,
                rect.height / TILE_SIZE
            ));
        }
    }

    private void cargarZonasTransicion() {
        zonasTransicion = new Array<>();
        if (mapa.getLayers().get("transiciones") == null) return;

        MapObjects objetos = mapa.getLayers().get("transiciones").getObjects();
        for (MapObject objeto : objetos) {
            if (!(objeto instanceof RectangleMapObject)) continue;
            if (!objeto.getProperties().containsKey("destino")) continue;

            RectangleMapObject rectObj = (RectangleMapObject) objeto;
            Rectangle rect = rectObj.getRectangle();
            String destino = objeto.getProperties().get("destino", String.class);
            String spawnName = objeto.getProperties().get("spawn_centro", "default", String.class); // propiedad clave
            //boolean pasaMapa = objeto.getProperties().get("pasaMapa", false, Boolean.class);
            zonasTransicion.add(new ZonaTransicion(
                rect.x / TILE_SIZE,
                rect.y / TILE_SIZE,
                rect.width / TILE_SIZE,
                rect.height / TILE_SIZE,
                destino,
                spawnName
                //pasaMapa
            ));
        }
    }

    public void generarEnemigos() {
        enemigos = new ArrayList<>();

        for (int i = 0; i < cantidadEnemigos; i++) {
            final int delay = i;

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    float x = MathUtils.random(2f, getAnchoMundo() - 2f);
                    float y = MathUtils.random(2f, getAltoMundo() - 2f);
                    enemigos.add(new Enemigo(x, y));
                }
            }, delay * 1.5f);
        }
    }

    private void cargarSpawnPoints() {
        spawnPoints = new Array<>();
        if (mapa.getLayers().get("spawns") == null) return;

        MapObjects objetos = mapa.getLayers().get("spawns").getObjects();
        for (MapObject objeto : objetos) {
            if (!(objeto instanceof PointMapObject)) continue;

            PointMapObject pointObj = (PointMapObject) objeto;
            Vector2 point = pointObj.getPoint();

            float x = point.x / TILE_SIZE;
            float y = point.y / TILE_SIZE;

            String name = objeto.getProperties().get("name", "default", String.class);
            String salaId = objeto.getProperties().get("sala_id", id, String.class);

            spawnPoints.add(new SpawnPoint(x, y, name, salaId));
        }
    }
    public boolean hayEnemigosVivos() {
        if (this.enemigos == null) return false;
        for (Enemigo e : this.enemigos) {
            if (!e.debeEliminarse()) return true;
        }
        return false;
    }

    // GETTERS
    public String getId() { return id; }
    public TiledMap getMapa() { return mapa; }
    public OrthogonalTiledMapRenderer getRenderer() { return renderer; }
    public Array<Rectangle> getColisiones() { return colisiones; }
    public Array<ZonaTransicion> getZonasTransicion() { return zonasTransicion; }
    public ArrayList<Enemigo> getEnemigos() { return enemigos; }
    public float getAnchoMundo() { return anchoTiles; }
    public float getAltoMundo() { return altoTiles; }
    public Array<SpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }

    public void dispose() {
        if (mapa != null) mapa.dispose();
        if (renderer != null) renderer.dispose();
    }

}
