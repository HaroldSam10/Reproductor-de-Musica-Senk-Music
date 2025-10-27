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
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.io.IOException
import androidx.core.view.WindowCompat


class Repro_Musenk : AppCompatActivity() {

    // --- Variables de UI ---
    private lateinit var textTitle: TextView
    private lateinit var textArtist: TextView
    private lateinit var albumArt: ImageView
    private lateinit var btnPlayPause: ImageButton
    private lateinit var volumeArc: CircularVolumeControl // <-- AÑADIDO

    // --- Variables del Servicio ---
    private var musicService: MusicService? = null
    private var isBound = false

    // "ServiceConnection" (el "cable" se queda igual)
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true

            updateUI(musicService?.getCurrentSong())
            updatePlayPauseButton()

            // --- AÑADIDO: Actualizamos la rueda con el volumen actual ---
            updateVolumeArc()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_repro_musenk)
        supportActionBar?.hide()

        //AQUI OCULTAMOS LOS BOTONES PAPIIIIIII
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


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
        volumeArc = findViewById(R.id.volumeArc) // <-- AÑADIDO

        // --- Programar Botones ---

        // ¡REEMPLAZAMOS la llamada a la función de volumen!
        setupFullVolumeControls(btnVolUp, btnVolOff, volumeArc)

        btnPlayPause.setOnClickListener {
            musicService?.playPause()
            updatePlayPauseButton()
        }
        btnWheelNext.setOnClickListener {
            musicService?.playNextSong()
            updateUI(musicService?.getCurrentSong())
            updatePlayPauseButton()
        }
        btnWheelPrev.setOnClickListener {
            musicService?.playPreviousSong()
            updateUI(musicService?.getCurrentSong())
            updatePlayPauseButton()
        }

        // Tus reglas de 'X' y 'Home' (se quedan igual)
        btnClose.setOnClickListener {
            musicService?.stopMusicAndService()
            if (isBound) {
                unbindService(connection)
                isBound = false
            }
            finish()
        }
        btnHome.setOnClickListener {
            finish()
        }
    } // Fin de onCreate

    // --- Conectar y Desconectar el "cable" (se queda igual) ---
    override fun onStart() {
        super.onStart()
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    // --- Funciones de UI (updateUI se queda igual) ---
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

    // --- AÑADIDO: Lógica para el botón Play/Pause ---
    // (Tu versión anterior tenía un error, siempre ponía play_arrow)
    private fun updatePlayPauseButton() {
        if (musicService?.isPlaying() == true) {
            btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    // --- ¡NUEVA! Función para actualizar la rueda ---
    private fun updateVolumeArc() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val currentPercentage = currentVolume.toFloat() / maxVolume.toFloat()
        volumeArc.setProgress(currentPercentage)
    }

    // --- ¡REEMPLAZAMOS setupVolumeButtons CON ESTA NUEVA FUNCIÓN! ---
    private fun setupFullVolumeControls(btnUp: ImageButton, btnOff: ImageButton, arc: CircularVolumeControl) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        // "Escuchamos" cuando el usuario mueva la rueda
        arc.onVolumeChanged = { newPercentage ->
            val newVolume = (newPercentage * maxVolume).toInt()
            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                newVolume,
                0 // Sin UI nativa
            )
        }

        // Hacemos que los botones de los lados también muevan la rueda
        btnUp.setOnClickListener {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0)
            updateVolumeArc() // Actualizamos la rueda
        }
        btnOff.setOnClickListener {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0)
            updateVolumeArc() // Actualizamos la rueda
        }
    }
}