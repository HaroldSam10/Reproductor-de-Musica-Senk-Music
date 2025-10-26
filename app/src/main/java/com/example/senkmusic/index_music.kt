package com.example.senkmusic

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class index_music : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_index_music)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.hide()


        // 1. Encontrar el RecyclerView en nuestro XML
        // ¡OJO! Asegúrate de que el ID en tu XML sea "Recomendaciones"
        val recommendationsRecyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.Recomendaciones)


        //AQUI VAN LOS CUMBIONES SI O NO RAZOTA
        val dummySongs = listOf(
            Song("ESTA FORMA DE AMARTE", "Jorge Dominguez", "2:42","portadas/jorge.jpg" , R.raw.esta_forma_de_amarte),
            Song("ENSEÑAME A OLVIDAR", "Aventura", "5:48", "portadas/aventura.jpg" , R.raw.ensename_a_olvidar), //
            Song("SIRENA", "Sin Bandera", "4:15", "portadas/sinbandera_sirena.jpeg", R.raw.sirena),
            Song("NO CAPEA", "Xavi, Grupo Frontera", "3:20", "portadas/no_capea.jpg", R.raw.no_capea)
        )

        // 3. Crear el Adaptador y dárselo al RecyclerView
        val adapter = SongAdapter(this, dummySongs)
        recommendationsRecyclerView.adapter = adapter
        recommendationsRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

    }
}