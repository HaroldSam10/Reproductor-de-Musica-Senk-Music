package com.example.senkmusic

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class Repro_Musenk : AppCompatActivity() {

    // --- Variables de UI ---
    private lateinit var textTitle: TextView
    private lateinit var textArtist: TextView
    private lateinit var albumArt: ImageView
    private lateinit var btnPlayPause: ImageButton

    // --- Variables del Servicio ---
    private var musicService: MusicService? = null
    private var isBound = false // Nos dice si estamos conectados a la "bocina"

    // "ServiceConnection" es el "cable" que conecta el control remoto a la bocina
    private val connection = object : ServiceConnection {

        // Se llama cuando el cable se conecta
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true

            // ¡Ya estamos conectados! Actualizamos la UI con lo que esté sonando
            updateUI(musicService?.getCurrentSong())
            updatePlayPauseButton()
        }

        // Se llama si la conexión se pierde
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_repro_musenk)
        supportActionBar?.hide()

        // --- Encontrar Views ---
        textTitle = findViewById(R.id.textSongTitlePlayer)
        textArtist = findViewById(R.id.textArtistNamePlayer)
        albumArt = findViewById(R.id.imgAlbumArtPlayer)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        val btnWheelNext = findViewById<ImageButton>(R.id.btnWheelNext)
        val btnWheelPrev = findViewById<ImageButton>(R.id.btnWheelPrev)
        val btnClose = findViewById<ImageButton>(R.id.btnClosePlayer)
        val btnVolUp = findViewById<ImageButton>(R.id.btnVolUp)
        val btnVolOff = findViewById<ImageButton>(R.id.btnVolOff)
        val btnHome = findViewById<ImageButton>(R.id.btnWheelHome)

        // --- Programar Botones ---
        setupVolumeButtons(btnVolUp, btnVolOff)

        btnPlayPause.setOnClickListener {
            musicService?.playPause() // Le decimos al SERVICIO que se pause
            updatePlayPauseButton()
        }
        btnWheelNext.setOnClickListener {
            musicService?.playNextSong()
            // (El servicio tocará la siguiente y necesitamos que nos avise)
            // Por ahora, actualizamos la UI manualmente (idealmente el servicio nos avisaría)
            updateUI(musicService?.getCurrentSong())
            updatePlayPauseButton()
        }
        btnWheelPrev.setOnClickListener {
            musicService?.playPreviousSong()
            updateUI(musicService?.getCurrentSong())
            updatePlayPauseButton()
        }

        // --- ¡CUMPLIENDO TUS REGLAS! ---

        // 1. Botón 'X' (Mata la música y cierra)
        btnClose.setOnClickListener {
            musicService?.stopMusicAndService() // Le dice al servicio que se detenga y muera
            if (isBound) {
                unbindService(connection)
                isBound = false
            }
            finish() // Cierra el "control remoto"
        }

        // 2. Botón 'Home' de la rueda (Solo cierra el "control remoto")
        btnHome.setOnClickListener {
            finish() // La música sigue sonando
        }
    } // Fin de onCreate

    // --- Conectar y Desconectar el "cable" ---

    override fun onStart() {
        super.onStart()
        // Cuando la pantalla aparece, nos "conectamos" al servicio
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        // Cuando la pantalla se oculta (con 'Back' o 'Home'), nos "desconectamos"
        // ¡PERO EL SERVICIO (LA MÚSICA) SIGUE VIVO!
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    // --- Funciones de UI ---

    private fun updateUI(song: Song?) {
        if (song == null) return
        textTitle.text = song.title
        textArtist.text = song.artist
        try {
            val inputStream = assets.open(song.coverArt)
            val drawable = Drawable.createFromStream(inputStream, null)
            albumArt.setImageDrawable(drawable)
            inputStream?.close()
        } catch (e: IOException) {
            albumArt.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    private fun updatePlayPauseButton() {
        if (musicService?.isPlaying() == true) {
            btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    private fun setupVolumeButtons(btnUp: ImageButton, btnOff: ImageButton) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        btnUp.setOnClickListener {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
        }
        btnOff.setOnClickListener {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
        }
    }
}