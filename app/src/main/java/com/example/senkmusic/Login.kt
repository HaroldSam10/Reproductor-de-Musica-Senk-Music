package com.example.senkmusic

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    ///   "CARRUSEL" DEL LOGINNNNN
    val fondos = arrayOf(
        R.drawable.car_login_1,
        R.drawable.car_login_2,
        R.drawable.car_login_3
    )

    var index = 0

    fun cambiarFondo() {
        val imageView = findViewById<ImageView>(R.id.imgFondo)
        imageView.setImageResource(fondos[index])
        index = (index + 1) % fondos.size
    }

    Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
        override fun run() {
            cambiarFondo()
            Handler(Looper.getMainLooper()).postDelayed(this, 5000)
        }
    }, 5000)

    ////////////////////////////////////////////

}