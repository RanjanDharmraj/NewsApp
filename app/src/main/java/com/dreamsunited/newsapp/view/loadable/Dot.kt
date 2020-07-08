package com.dreamsunited.newsapp.view.loadable

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint

class Dot(private val radius: Int, defaultColor: Int) {
    val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var dotX: Float = 0.0f
    var dotY: Float = 0.0f
    var positionAnimator: ValueAnimator? = null
    var colorAnimator: ValueAnimator? = null

    init {
        paint.style = Paint.Style.FILL
        paint.color = defaultColor
    }

    fun setColor(color: Int) {
        paint.color = color
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(dotX, dotY, radius.toFloat(), paint)
    }
}