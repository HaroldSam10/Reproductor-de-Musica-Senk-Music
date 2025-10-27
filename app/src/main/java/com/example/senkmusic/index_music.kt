package com.example.senkmusic

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

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

        //AQUI OCULTAMOS LOS BOTONES PAPIIIIIII
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        ///ENCONTRAMOS EL CARRUSELLLL TUS ESCUCHADOSSSSSS
        val carouselRecyclerView =
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerTusEscuchados)
        val tusEscuchadosSongs = listOf(
            Song("CARELESS WHISPER", "George Michael", "5:00", "portadas/carreless_whisper.jpeg", R.raw.carreless_whisper),
            Song(
                "ENSEÑAME A OLVIDAR",
                "Aventura",
                "5:48",
                "portadas/aventura.jpg",
                R.raw.ensename_a_olvidar
            ),
            Song(
                "LA SOLEDAD",
                "Laura Pausini",
                "3:59",
                "portadas/soledad.jpeg",
                R.raw.soledad
            ),
            Song(
                "WORDS ARE DEAD",
                "Agnes Obell",
                "3:45",
                "portadas/aventine.jpg",
                R.raw.word_are_dead
            ),
            Song("USTED", "Luis Miguel", "3:43", "portadas/usted.jpg" , R.raw.usted),
        )

        //Crear el nuevo Adaptador y dárselo al RecyclerView
        val carouselAdapter = CarouselAdapter(this, tusEscuchadosSongs)
        carouselRecyclerView.adapter = carouselAdapter
        // HORIZONTAAL
        carouselRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)











        //////////////////RECOMNDADOSSSSSS
        // 1. Encontrar el RecyclerView en nuestro XML
        val recommendationsRecyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.Recomendaciones)


        //AQUI VAN LOS CUMBIONES SI O NO RAZOTA
        val dummySongs = listOf(
            Song("USTED", "Luis Miguel", "3:43", "portadas/usted.jpg" , R.raw.usted),
            Song("ESTA COBARDÍA", "Chiquetete", "3:40", "portadas/cobardia.jpg" , R.raw.esta_cobardia),
            Song("ENSEÑAME A OLVIDAR", "Aventura", "5:48", "portadas/aventura.jpg" , R.raw.ensename_a_olvidar),
            Song("SIRENA", "Sin Bandera", "4:15", "portadas/sinbandera_sirena.jpeg", R.raw.sirena),
            Song("ESTA FORMA DE AMARTE", "Jorge Dominguez", "2:42","portadas/jorge.jpg" , R.raw.esta_forma_de_amarte),
            Song("NO CAPEA", "Xavi, Grupo Frontera", "3:20", "portadas/no_capea.jpg", R.raw.no_capea),
            Song(
                "EL MEJOR PERFUME",
                "La Original Banda El Limón de Salvador Lizárraga",
                "2:32",
                "portadas/el_mejor_perfume.jpeg",
                R.raw.el_mejor_perfume
            ),
            Song(
                "ELLA ES",
                "Jorge Domínguez",
                "2:45",
                "portadas/jorge.jpg",
                R.raw.ella_es
            )
        )

        // 3. Crear el Adaptador y dárselo al RecyclerView
        val adapter = SongAdapter(this, dummySongs)
        recommendationsRecyclerView.adapter = adapter
        recommendationsRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

    }
}