package com.example.senkmusic

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs // <-- Importante para el umbral
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class CircularVolumeControl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // --- Variables de dibujo (sin cambios) ---11
    private val backgroundArcPaint: Paint
    private val progressArcPaint: Paint
    private val arcBounds = RectF()
    private var sweepAngle = 270f
    private val strokeWidth = 8f * resources.displayMetrics.density

    // --- Listener (sin cambios) ---
    var onVolumeChanged: ((Float) -> Unit)? = null

    // --- ¡NUEVAS! Variables para el Umbral ---
    private var lastSentAngle = -1f // Guarda el último ángulo que SÍ actualizó el volumen
    private val angleThreshold = 2.5f // Umbral: Solo actualiza si cambia más de 2.5 grados

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

    // --- onSizeChanged (sin cambios) ---
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

    // --- setProgress (sin cambios) ---
    fun setProgress(progress: Float) {
        sweepAngle = (progress * 270f).coerceIn(0f, 270f) // Asegura que esté en rango
        invalidate()
    }

    // --- ¡onTouchEvent ACTUALIZADO con Umbral! ---
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val centerX = width / 2f
        val centerY = height / 2f
        val x = event.x
        val y = event.y

        // Chequeo de Radio (sin cambios)
        val radius = width / 2f
        val touchRadius = sqrt((x - centerX).pow(2) + (y - centerY).pow(2))
        val arcOuterRadius = radius - (strokeWidth / 4)
        val arcInnerRadius = radius - strokeWidth * 1.5
        if (touchRadius < arcInnerRadius || touchRadius > arcOuterRadius) {
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                return super.onTouchEvent(event)
            }
            return false // Ignorar toque fuera
        }

        // Cálculo del Ángulo (sin cambios)
        var angle = Math.toDegrees(atan2(y - centerY, x - centerX).toDouble()) + 90
        if (angle < 0) {
            angle += 360
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Al tocar, reseteamos el 'lastSentAngle' para forzar la primera actualización
                lastSentAngle = -1f
                // Calculamos el ángulo inicial y actualizamos INMEDIATAMENTE si está en zona activa
                handleAngleUpdate(angle)
                return true // ¡Sí, yo me encargo!
            }
            MotionEvent.ACTION_MOVE -> {
                // Al mover, actualizamos solo si el ángulo cambió lo suficiente
                handleAngleUpdate(angle)
                return true
            }
            // Permitir otros gestos si no es DOWN o MOVE
            else -> return super.onTouchEvent(event)
        }
    } // Fin de onTouchEvent


    // --- ¡NUEVA FUNCIÓN HELPER con Umbral! ---
    private fun handleAngleUpdate(currentAngle: Double) {
        // 1. Normalizamos el ángulo (sin cambios)
        var normalizedAngle = currentAngle - 135.0
        if (normalizedAngle < 0) {
            normalizedAngle += 360.0
        }

        // 2. Checamos si está DENTRO del rango activo (sin cambios)
        if (normalizedAngle <= 270.0) {

            // --- ¡LA CLAVE ESTÁ AQUÍ! ---
            // Solo actualizamos si es el primer toque O si el cambio es mayor al umbral
            if (lastSentAngle == -1f || abs(normalizedAngle - lastSentAngle) >= angleThreshold) {

                sweepAngle = normalizedAngle.toFloat().coerceIn(0f, 270f) // Actualizamos ángulo visual
                invalidate() // Redibujar

                // Gritamos el porcentaje
                val newPercentage = sweepAngle / 270f
                onVolumeChanged?.invoke(newPercentage)

                // Guardamos este ángulo como el último enviado
                lastSentAngle = normalizedAngle.toFloat()
            }
        }
        // Si está en zona muerta o no superó el umbral, no hacemos nada
    }
} // Fin de la clase