package com.example.senkmusic

import android.content.Intent
import android.os.Bundle
import android.os.Handler ///LIBRERIA PARA EL INICIO DE FOTOS DINAMICO
import android.os.Looper  ///LIBRERIA PARA EL INICIO DE FOTOS DINAMICO
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        // Iniciar la animación de cambio de fondo
        iniciarCambioFondo()

        //NUESTROS NODOS DE LOS ELEMETOS DEL LOGIN
        val edtUser = findViewById<EditText>(R.id.edtUser)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val btn_Inicio = findViewById<Button>(R.id.button)

        val enid = "harold1417"
        val enid2 = "renosenk1417"

        btn_Inicio.setOnClickListener{
            val user_input = edtUser.text.toString()
            val pass_input = edtPass.text.toString()

            if (user_input == enid && pass_input == enid2) {
                Toast.makeText(this, "TUS CREDENCIALES SON CORRECTAS", Toast.LENGTH_LONG).show()


                val intent = Intent(this@Login, index_music::class.java)
                startActivity(intent)

            } else {
                Toast.makeText(this, "TUS CREDENCIALES SON INCORRECTAS", Toast.LENGTH_LONG).show()
            }

        }


    }





















    private val fondos = arrayOf(
        R.drawable.car_login_1,
        R.drawable.car_login_2,
        R.drawable.car_login_3
    )

    private var index = 0

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