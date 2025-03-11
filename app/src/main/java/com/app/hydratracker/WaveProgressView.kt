package com.app.hydratracker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.graphics.Shader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

class WaveProgressView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val wavePath = Path()

    private var progress = 500f  // Current water level (ml)
    private var maxProgress = 1000f  // Max water level
    private var waveOffset = 0f  // Used for animation
    private var waveHeight = 20f  // Height of waves

    init {
        wavePaint.color = Color.parseColor("#03A9F4") // Water color
        wavePaint.style = Paint.Style.FILL

        startWaveAnimation()
    }

    private fun startWaveAnimation() {
        val animator = ValueAnimator.ofFloat(0f, 360f)
        animator.duration = 2000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener {
            waveOffset = (it.animatedValue as Float)
            invalidate()
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val radius = width / 2f
        val waterLevel = height - (progress / maxProgress) * height

        paint.color = Color.parseColor("#B3E5FC")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        canvas.drawCircle(radius, radius, radius - 4f, paint)

        val circlePath = Path()
        circlePath.addCircle(radius, radius, radius - 4f, Path.Direction.CW)

        canvas.save()
        canvas.clipPath(circlePath)

        wavePath.reset()
        val waveWidth = width.toFloat()
        wavePath.moveTo(-waveWidth, waterLevel)

        for (i in -waveWidth.toInt() until (waveWidth * 2).toInt() step 20) {
            val x = i.toFloat()
            val y = (Math.sin(Math.toRadians((x + waveOffset).toDouble())) * waveHeight).toFloat() + waterLevel
            wavePath.lineTo(x, y)
        }

        wavePath.lineTo(waveWidth * 2, height.toFloat())
        wavePath.lineTo(-waveWidth, height.toFloat())
        wavePath.close()

        canvas.drawPath(wavePath, wavePaint)

        canvas.restore()

        paint.color = Color.WHITE
        paint.textSize = 40f
        paint.style = Paint.Style.FILL
        val text = "${progress.toInt()}ml"
        val textWidth = paint.measureText(text)
        canvas.drawText(text, radius - textWidth / 2, radius + 20, paint)
    }

    fun setProgress(value: Float) {
        progress = value
        invalidate()
    }
    fun setMaxProgress(value: Float) {
        maxProgress = value
        invalidate()
    }
}