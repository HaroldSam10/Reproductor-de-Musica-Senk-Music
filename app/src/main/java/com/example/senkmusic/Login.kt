package com.example.senkmusic

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {

    private val fondos = arrayOf(
        R.drawable.car_login_1,
        R.drawable.car_login_2,
        R.drawable.car_login_3
    )

    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)



        // Iniciar la animación de cambio de fondo
        iniciarCambioFondo()
    }


    ///FONDOS DINAMICOSSS
    private fun cambiarFondo() {
        val imageView = findViewById<ImageView>(R.id.imgFondo)
        imageView.setImageResource(fondos[index])
        index = (index + 1) % fondos.size
    }

    private fun iniciarCambioFondo() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                cambiarFondo()
                handler.postDelayed(this, 4000) // Cambia fondo cada 5 segundos
            }
        })
    }
    //////////////


}