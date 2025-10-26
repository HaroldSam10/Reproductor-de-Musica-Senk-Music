package com.example.senkmusic

import android.app.Service
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import java.io.IOException

class MusicService : Service() {

    // --- Variables del Servicio ---
    private var mediaPlayer: MediaPlayer? = null
    private var songList: ArrayList<Song> = ArrayList()
    private var currentSongIndex: Int = -1

    // "Binder" es el "conector" que le damos a las Activities (el control remoto)
    private val binder = MusicBinder()

    // --- Clase interna Binder ---
    inner class MusicBinder : Binder() {
        // Esta función le da al "control remoto" acceso a la "bocina"
        fun getService(): MusicService = this@MusicService
    }

    // --- Ciclo de vida del Servicio ---
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Aquí es donde recibimos la lista por PRIMERA VEZ
        val index = intent?.getIntExtra("CURRENT_SONG_INDEX", -1) ?: -1
        val list = intent?.getParcelableArrayListExtra<Song>("SONG_LIST")

        if (index != -1 && list != null) {
            this.songList = list
            this.currentSongIndex = index
            playSong(currentSongIndex)
        }

        return START_STICKY // Si Android mata el servicio, que intente revivirlo
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // --- FUNCIONES DE CONTROL (Las llamará el "control remoto") ---

    fun playSong(index: Int) {
        if (index < 0 || index >= songList.size) return
        currentSongIndex = index
        val song = songList[currentSongIndex]

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        mediaPlayer = MediaPlayer.create(this, song.musicFile)
        mediaPlayer?.start()

        // Cuando se acabe, toca la siguiente
        mediaPlayer?.setOnCompletionListener { playNextSong() }
    }

    fun playNextSong() {
        var nextIndex = currentSongIndex + 1
        if (nextIndex >= songList.size) {
            nextIndex = 0 // Loop
        }
        playSong(nextIndex)
    }

    fun playPreviousSong() {
        var prevIndex = currentSongIndex - 1
        if (prevIndex < 0) {
            prevIndex = songList.size - 1 // Loop
        }
        playSong(prevIndex)
    }

    fun playPause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
        }
    }

    fun stopMusicAndService() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopSelf() // Le dice al servicio que se destruya a sí mismo
    }

    // --- FUNCIONES DE ESTADO (Para que el "control remoto" sepa qué pasa) ---

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun getCurrentSong(): Song? {
        return if (currentSongIndex != -1) {
            songList[currentSongIndex]
        } else {
            null
        }
    }
}