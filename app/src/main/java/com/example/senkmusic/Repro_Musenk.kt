package com.example.senkmusic

import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class Repro_Musenk : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_repro_musenk)

        supportActionBar?.hide()

        // --- Referencias a los Views ---
        val textTitle = findViewById<TextView>(R.id.textSongTitlePlayer)
        val textArtist = findViewById<TextView>(R.id.textArtistNamePlayer)
        val albumArt = findViewById<ImageView>(R.id.imgAlbumArtPlayer)
        val btnClose = findViewById<ImageButton>(R.id.btnClosePlayer)
        val btnPlayPause = findViewById<ImageButton>(R.id.btnPlayPause)

        // --- Recibimos el "molde" ---
        val song = intent.getParcelableExtra<Song>("SONG_DATA")

        if (song != null) {

            // --- Ponemos la info de texto ---
            textTitle.text = song.title
            textArtist.text = song.artist

            // --- CÓDIGO PARA CARGAR PORTADA DESDE ASSETS ---
            try {
                val inputStream = assets.open(song.coverArt)
                val drawable = Drawable.createFromStream(inputStream, null)
                albumArt.setImageDrawable(drawable)
                inputStream?.close()
            } catch (e: IOException) {
                albumArt.setImageResource(R.drawable.ic_launcher_background) // Imagen de error
                e.printStackTrace()
            }
            // --- FIN DEL CÓDIGO ---

            // --- Lógica del MediaPlayer ---
            mediaPlayer = MediaPlayer.create(this, song.musicFile)
            mediaPlayer?.start()
            btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
        }

        // --- Lógica de los botones ---
        btnClose.setOnClickListener {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            finish()
        }

        btnPlayPause.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
            } else {
                mediaPlayer?.start()
                btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
            }
        }

        mediaPlayer?.setOnCompletionListener {
            btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}