package com.abyssdev.entertheabyss.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Sonidos {

    // ✅ Música
    private static Music musicaMenu;
    private static Music musicaJuego;
    private static Music musicaDerrota; // ✅ Nueva

    // ✅ Efectos
    private static Sound sonidoAtaque;

    public static void cargar() {
        // Música
        musicaMenu = Gdx.audio.newMusic(Gdx.files.internal("sonidos/musica/menu.mp3"));
        musicaJuego = Gdx.audio.newMusic(Gdx.files.internal("sonidos/musica/juego.mp3"));
        musicaDerrota = Gdx.audio.newMusic(Gdx.files.internal("sonidos/musica/game_over.mp3")); // ✅ Cargar

        // Configurar música
        musicaMenu.setLooping(true);
        musicaJuego.setLooping(true);
        musicaDerrota.setLooping(false); // ✅ No repetir en Game Over

        musicaMenu.setVolume(0.5f);
        musicaJuego.setVolume(0.5f);
        musicaDerrota.setVolume(0.7f); // ✅ Volumen más alto para impacto

        // Efectos
        sonidoAtaque = Gdx.audio.newSound(Gdx.files.internal("sonidos/efectos/espada.mp3"));
    }

    // ✅ Métodos de música
    public static void reproducirMusicaMenu() {
        pararTodaMusica();
        musicaMenu.play();
    }

    public static void reproducirMusicaJuego() {
        pararTodaMusica();
        musicaJuego.play();
    }

    public static void reproducirMusicaDerrota() { // ✅ Nuevo
        pararTodaMusica();
        musicaDerrota.play();
    }

    public static void pararTodaMusica() {
        musicaMenu.stop();
        musicaJuego.stop();
        musicaDerrota.stop();
    }

    // ✅ Efectos
    public static void reproducirAtaque() {
        if (sonidoAtaque != null) {
            sonidoAtaque.play(0.8f);
        }
    }

    public static void dispose() {
        if (musicaMenu != null) musicaMenu.dispose();
        if (musicaJuego != null) musicaJuego.dispose();
        if (musicaDerrota != null) musicaDerrota.dispose(); // ✅ Liberar
        if (sonidoAtaque != null) sonidoAtaque.dispose();
    }
}
