package com.example.senkmusic

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException

class CarouselAdapter(private val context: Context, private val songs: List<Song>) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    // 1. "ViewHolder": Solo tiene una referencia a la imagen
    class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songCover: ImageView = itemView.findViewById(R.id.imgAlbumCover)
    }

    // 2. "onCreateViewHolder": Infla el "molde" (item_album_cover.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_album_cover, parent, false)
        return CarouselViewHolder(view)
    }

    // 3. "getItemCount": Cuántos elementos hay
    override fun getItemCount(): Int = songs.size

    // 4. "onBindViewHolder": Conecta los datos con el XML
    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val song = songs[position]

        // --- Carga la portada desde ASSETS ---
        try {
            val assetManager = context.assets
            val inputStream = assetManager.open(song.coverArt)
            val drawable = Drawable.createFromStream(inputStream, null)
            holder.songCover.setImageDrawable(drawable)
            inputStream?.close()
        } catch (e: IOException) {
            holder.songCover.setImageResource(R.drawable.ic_launcher_background)
            e.printStackTrace()
        }

        // --- ¡EL CLIC! (Hace lo mismo que el otro adapter) ---
        holder.itemView.setOnClickListener {
            // 1. Prepara el Intent para el SERVICIO
            val serviceIntent = Intent(context, MusicService::class.java)

            // 2. Le pasamos la lista COMPLETA de "Tus Escuchados"
            serviceIntent.putExtra("CURRENT_SONG_INDEX", position)
            serviceIntent.putParcelableArrayListExtra("SONG_LIST", ArrayList(songs))

            // 3. Encendemos la "bocina" (el servicio)
            context.startService(serviceIntent)

            // 4. ABRIMOS el "control remoto"
            val activityIntent = Intent(context, Repro_Musenk::class.java)
            context.startActivity(activityIntent)
        }
    }
}
