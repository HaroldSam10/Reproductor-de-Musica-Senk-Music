package com.example.senkmusic

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.atan2

class CircularVolumeControl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // --- Variables de dibujo ---
    private val backgroundArcPaint: Paint
    private val progressArcPaint: Paint
    private val arcBounds = RectF()
    private var sweepAngle = 270f
    private val strokeWidth = 8f * resources.displayMetrics.density

    // --- ¡NUEVO! El "listener" para gritar el porcentaje ---
    var onVolumeChanged: ((Float) -> Unit)? = null

    init {
        // ... (Tu código de 'init' para los 'Paint' se queda igual) ...
        backgroundArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = this@CircularVolumeControl.strokeWidth
            color = ContextCompat.getColor(context, R.color.white)
            alpha = 70
            strokeCap = Paint.Cap.ROUND
        }
        progressArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = this@CircularVolumeControl.strokeWidth
            color = ContextCompat.getColor(context, R.color.white)
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // ... (Tu código de 'onSizeChanged' se queda igual) ...
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = strokeWidth / 2
        arcBounds.set(padding, padding, w - padding, h - padding)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // ... (Tu código de 'onDraw' se queda igual) ...
        canvas.drawArc(arcBounds, 135f, 270f, false, backgroundArcPaint)
        canvas.drawArc(arcBounds, 135f, sweepAngle, false, progressArcPaint)
    }

    // --- ¡NUEVO! Función para que la Activity actualice el arco ---
    fun setProgress(progress: Float) {
        // progress es un porcentaje (0.0 a 1.0)
        // Lo convertimos a nuestro ángulo (0 a 270)
        sweepAngle = progress * 270f
        invalidate() // Le decimos al View que se redibuje
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Obtenemos el centro del View
        val centerX = width / 2f
        val centerY = height / 2f

        // Obtenemos la posición del dedo
        val x = event.x
        val y = event.y

        // Calculamos el ángulo (igual que antes)
        var angle = Math.toDegrees(atan2(y - centerY, x - centerX).toDouble()) + 90
        if (angle < 0) {
            angle += 360
        }

        when (event.action) {
            // ¡EL CAMBIO! Al TOCAR, solo "confirmamos" que manejaremos el gesto
            MotionEvent.ACTION_DOWN -> {
                return true // ¡Sí, yo me encargo!
            }

            // ¡SOLO AL MOVER, cambiamos el volumen!
            MotionEvent.ACTION_MOVE -> {
                var newSweepAngle: Float

                if (angle >= 135 && angle <= 360) {
                    newSweepAngle = (angle - 135).toFloat()
                } else if (angle >= 0 && angle <= 45) {
                    newSweepAngle = (angle + (360 - 135)).toFloat()
                } else {
                    return true // Sigue en la zona muerta
                }

                sweepAngle = newSweepAngle
                invalidate()

                // Gritamos el porcentaje
                val newPercentage = sweepAngle / 270f
                onVolumeChanged?.invoke(newPercentage)

                return true
            }
        }
        return false
    }
}