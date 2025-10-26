package com.example.senkmusic

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

// 1. Heredamos de 'View' (el componente básico de Android)
// 2. Añadimos '@JvmOverloads' para que Android Studio pueda usarlo en el XML
class CircularVolumeControl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // --- (Aquí irá toda nuestra lógica de dibujo y tacto) ---

    // onDraw es la función que "dibuja" el componente
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // (Por ahora no dibujamos nada)
    }
}