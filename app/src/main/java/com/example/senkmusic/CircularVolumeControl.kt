package com.example.senkmusic

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class CircularVolumeControl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // --- Variables de dibujo (sin cambios) ---
    private val backgroundArcPaint: Paint
    private val progressArcPaint: Paint
    private val arcBounds = RectF()
    private var sweepAngle = 270f
    private val strokeWidth = 8f * resources.displayMetrics.density


    var onVolumeChanged: ((Float) -> Unit)? = null



    init {
        // --- Configuración de Pinceles (sin cambios) ---
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
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = strokeWidth / 2
        arcBounds.set(padding, padding, w - padding, h - padding)
    }

    // --- onDraw (sin cambios) ---
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(arcBounds, 135f, 270f, false, backgroundArcPaint)
        canvas.drawArc(arcBounds, 135f, sweepAngle, false, progressArcPaint)
    }


    fun setProgress(progress: Float) {
        sweepAngle = (progress * 270f).coerceIn(0f, 270f) // Asegura que esté en rango
        invalidate()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val centerX = width / 2f
        val centerY = height / 2f
        val x = event.x
        val y = event.y

       
        val radius = width / 2f
        val touchRadius = sqrt((x - centerX).pow(2) + (y - centerY).pow(2))
        val arcOuterRadius = radius
        val arcInnerRadius = radius - strokeWidth * 2.0 // Área táctil generosa
        if (touchRadius < arcInnerRadius || touchRadius > arcOuterRadius) {
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) { return super.onTouchEvent(event) }
            return false // Ignorar toque fuera
        }

        // Cálculo del Ángulo (0-360, 0 es ARRIBA, horario)
        var angle = Math.toDegrees(atan2(y - centerY, x - centerX).toDouble()) + 90
        if (angle < 0) { angle += 360 }

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {

                // Límites de la ZONA MUERTA INFERIOR
                val deadZoneStart = 135.0
                val deadZoneEnd = 225.0

                // Límite VISUAL de inicio
                val visualStartAngle = 135.0
                val totalArcDegrees = 270.0

                var calculatedSweepAngle: Double? = null

                // Si NO está en zona muerta...
                if (!(angle >= deadZoneStart && angle <= deadZoneEnd)) {

                    // Calculamos el progreso relativo al inicio VISUAL
                    if (angle >= 225) { // 225 a 360
                        calculatedSweepAngle = angle - 225.0 // Mapea 225->0, 360->135
                    } else { // 0 a <135 (pasó la zona muerta inicial)
                        calculatedSweepAngle = (360.0 - 225.0) + angle // Mapea 0->135, 135->270
                    }

                    // --- ¡AQUÍ ESTÁ EL ARREGLO! ---
                    // Solo procedemos si SÍ calculamos un ángulo (no era null)
                    if (calculatedSweepAngle != null) {
                        // Aseguramos rango 0-270
                        sweepAngle = calculatedSweepAngle.coerceIn(0.0, totalArcDegrees).toFloat()
                        invalidate() // Redibujar

                        // Gritamos porcentaje 0-1
                        val newPercentage = sweepAngle / totalArcDegrees.toFloat()
                        onVolumeChanged?.invoke(newPercentage.coerceIn(0f, 1f))
                    }

                }


                return true
            }
            else -> return super.onTouchEvent(event)
        }
    }
}