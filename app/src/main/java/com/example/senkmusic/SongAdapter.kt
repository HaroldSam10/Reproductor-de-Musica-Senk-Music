package com.example.senkmusic

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException

class SongAdapter(private val context: Context, private val songs: List<Song>) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songCover: ImageView = itemView.findViewById(R.id.imgSongCover)
        val songTitle: TextView = itemView.findViewById(R.id.textSongTitle)
        val artistName: TextView = itemView.findViewById(R.id.textArtistName)
        val songDuration: TextView = itemView.findViewById(R.id.textSongDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_song_recommendation, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount(): Int = songs.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]

        // Pone los datos de la canción en los TextViews
        holder.songTitle.text = song.title
        holder.artistName.text = song.artist
        holder.songDuration.text = song.duration

        // --- CÓDIGO PARA CARGAR DESDE ASSETS ---
        try {
            val assetManager = context.assets
            val inputStream = assetManager.open(song.coverArt)
            val drawable = Drawable.createFromStream(inputStream, null)
            holder.songCover.setImageDrawable(drawable)
            inputStream?.close()
        } catch (e: IOException) {
            holder.songCover.setImageResource(R.drawable.ic_launcher_background) // Imagen de error
            e.printStackTrace()
        }
        // --- FIN DEL CÓDIGO ---

        // El clic para abrir el reproductor
        holder.itemView.setOnClickListener {
            // 1. Prepara el Intent para el SERVICIO
            val serviceIntent = Intent(context, MusicService::class.java)

            // 2. Le pasamos la lista completa y la posición
            serviceIntent.putExtra("CURRENT_SONG_INDEX", position)
            serviceIntent.putParcelableArrayListExtra("SONG_LIST", ArrayList(songs))

            // 3. Encendemos la "bocina" (el servicio)
            context.startService(serviceIntent)

            // 4. ABRIMOS el "control remoto" (la pantalla del reproductor)
            val activityIntent = Intent(context, Repro_Musenk::class.java)
            context.startActivity(activityIntent)
        }
    }
}