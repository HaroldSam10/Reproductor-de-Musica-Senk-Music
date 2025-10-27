package com.example.senkmusic

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import java.io.IOException
import java.util.concurrent.TimeUnit

class Repro_Musenk : AppCompatActivity() {

    // --- Variables de UI ---
    private lateinit var textTitle: TextView
    private lateinit var textArtist: TextView
    private lateinit var albumArt: ImageView
    private lateinit var btnPlayPause: ImageButton
    private lateinit var volumeArc: CircularVolumeControl

    // ¡NUEVO! Variables para el SeekBar
    private lateinit var cardAlbumArtPlayer: CardView
    private lateinit var seekBarContainer: LinearLayout
    private lateinit var songSeekBar: SeekBar
    private lateinit var textCurrentTime: TextView
    private lateinit var textTotalTime: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var isSeekBarVisible = false // Controla la "vuelta"

    // --- Variables del Servicio ---
    private var musicService: MusicService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true

            updateUI(musicService?.getCurrentSong())
            updatePlayPauseButton()
            updateVolumeArc()

            // ¡NUEVO! Configura e inicia el SeekBar
            setupSeekBar()
            runOnUiThread(updateSeekBarTask)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_repro_musenk)

        // --- Ocultar barras del sistema ---
        supportActionBar?.hide()
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // ---

        // --- Encontrar Views (TODAS) ---
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
        volumeArc = findViewById(R.id.volumeArc)

        // ¡NUEVO! Views del SeekBar
        cardAlbumArtPlayer = findViewById(R.id.cardAlbumArtPlayer)
        seekBarContainer = findViewById(R.id.seekBarContainer)
        songSeekBar = findViewById(R.id.songSeekBar)
        textCurrentTime = findViewById(R.id.textCurrentTime)
        textTotalTime = findViewById(R.id.textTotalTime)

        // --- Programar Botones ---
        setupFullVolumeControls(btnVolUp, btnVolOff, volumeArc)
        setupClickListeners(btnClose, btnHome, btnWheelNext, btnWheelPrev)

        // ¡NUEVO! Lógica de la "vuelta"
        cardAlbumArtPlayer.setOnClickListener { toggleSeekBarVisibility() }
        seekBarContainer.setOnClickListener { toggleSeekBarVisibility() }

    } // Fin de onCreate

    // --- Tarea que se ejecuta cada segundo para actualizar el SeekBar ---
    private val updateSeekBarTask = object : Runnable {
        override fun run() {
            if (isBound && musicService != null) {
                val currentPosition = musicService!!.getCurrentPosition()
                songSeekBar.progress = currentPosition
                textCurrentTime.text = formatDuration(currentPosition.toLong())

                // Vuelve a ejecutarse a sí misma después de 1 segundo
                handler.postDelayed(this, 1000)
            }
        }
    }

    // --- Configuración de los botones (excepto Play) ---
    private fun setupClickListeners(btnClose: ImageButton, btnHome: ImageButton, btnNext: ImageButton, btnPrev: ImageButton) {
        btnPlayPause.setOnClickListener {
            musicService?.playPause()
            updatePlayPauseButton()
        }
        btnNext.setOnClickListener {
            musicService?.playNextSong()
            updateUI(musicService?.getCurrentSong())
            updatePlayPauseButton()
            setupSeekBar() // Resetea el seekbar para la nueva canción
        }
        btnPrev.setOnClickListener {
            musicService?.playPreviousSong()
            updateUI(musicService?.getCurrentSong())
            updatePlayPauseButton()
            setupSeekBar() // Resetea el seekbar para la nueva canción
        }
        btnClose.setOnClickListener {
            musicService?.stopMusicAndService()
            if (isBound) unbindService(connection)
            isBound = false
            handler.removeCallbacks(updateSeekBarTask) // Detiene el actualizador
            finish()
        }
        btnHome.setOnClickListener {
            finish() // La música sigue (el handler se detendrá en onStop)
        }
    }

    // --- ¡NUEVO! Configuración del SeekBar ---
    private fun setupSeekBar() {
        if (musicService == null) return

        val duration = musicService!!.getDuration()
        songSeekBar.max = duration
        textTotalTime.text = formatDuration(duration.toLong())

        songSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    textCurrentTime.text = formatDuration(progress.toLong())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            // Cuando el usuario suelta el dedo
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    musicService?.seekTo(it.progress) // Le decimos al servicio que salte
                }
            }
        })
    }

    // --- ¡NUEVO! Lógica de la "vuelta" (animación) ---
    private fun toggleSeekBarVisibility() {
        if (isSeekBarVisible) {
            // Ocultar SeekBar, mostrar Portada
            cardAlbumArtPlayer.animate().alpha(1f).duration = 300
            seekBarContainer.animate().alpha(0f).withEndAction {
                seekBarContainer.visibility = View.GONE
            }.duration = 300
        } else {
            // Ocultar Portada, mostrar SeekBar
            cardAlbumArtPlayer.animate().alpha(0f).duration = 300
            seekBarContainer.alpha = 0f
            seekBarContainer.visibility = View.VISIBLE
            seekBarContainer.animate().alpha(1f).duration = 300
        }
        isSeekBarVisible = !isSeekBarVisible
    }

    // --- ¡NUEVO! Formateador de tiempo ---
    private fun formatDuration(ms: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) -
                TimeUnit.MINUTES.toSeconds(minutes)
        return String.format("%d:%02d", minutes, seconds)
    }

    // --- Conectar y Desconectar el "cable" (y el Handler) ---
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
        // ¡Importante! Detiene el actualizador si la app se va a fondo
        handler.removeCallbacks(updateSeekBarTask)
    }

    // --- Funciones de UI (actualizadas) ---
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

    private fun updateVolumeArc() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val currentPercentage = currentVolume.toFloat() / maxVolume.toFloat()
        volumeArc.setProgress(currentPercentage)
    }

    private fun setupFullVolumeControls(btnUp: ImageButton, btnOff: ImageButton, arc: CircularVolumeControl) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        arc.onVolumeChanged = { newPercentage ->
            val newVolume = (newPercentage * maxVolume).toInt()
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
        }
        btnUp.setOnClickListener {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0)
            updateVolumeArc()
        }
        btnOff.setOnClickListener {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0)
            updateVolumeArc()
        }
    }
}