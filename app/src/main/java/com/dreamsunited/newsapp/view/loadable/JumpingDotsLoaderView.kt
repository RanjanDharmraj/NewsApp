package com.dreamsunited.newsapp.view.loadable

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.dreamsunited.newsapp.R
import java.lang.ref.WeakReference

const val DELAY_BETWEEN_DOTS = 100
const val JUMP_ANIMATION_DURATION = 1000
const val COLOR_ANIMATION_DURATION = 1000
private const val NUMBER_OF_DOTS = 3
private const val DOTS_ROTATION_DEGREES = 18F

class JumpingDotsLoaderView @JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private lateinit var dots: ArrayList<Dot>
    private var radius: Int = 0
    private var jumpHeight: Int = 0
    private var dotSpace: Int = 0
    private var startY: Float = 0.0f
    private var endY: Float = 0.0f
    private var colorDark: Int = 0
    private var colorMedium: Int = 0
    private var colorLight: Int = 0
    private var jumpDuration: Int = 0
    private var colorDuration: Int = 0
    private var jumpDelay: Int = 0

    init {
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.JumpingDotsLoaderView, 0, 0)
            try {
                radius = a.getDimension(
                    R.styleable.JumpingDotsLoaderView_dot_radius,
                    resources.getDimension(R.dimen.JumpingDotsLoaderView_dots_radius_default)).toInt()
                jumpHeight = a.getDimensionPixelSize(R.styleable.JumpingDotsLoaderView_jump_height,
                    resources.getDimensionPixelSize(R.dimen.JumpingDotsLoaderView_jump_height_default))
                dotSpace = a.getDimensionPixelSize(R.styleable.JumpingDotsLoaderView_dots_space,
                    resources.getDimensionPixelSize(R.dimen.JumpingDotsLoaderView_dots_space_default))
                colorDark = a.getColor(R.styleable.JumpingDotsLoaderView_color_dark, ContextCompat.getColor(context, R.color.color_ffffff_ff))
                colorMedium = a.getColor(R.styleable.JumpingDotsLoaderView_color_medium, ContextCompat.getColor(context, R.color.color_ffffff_f2))
                colorLight = a.getColor(R.styleable.JumpingDotsLoaderView_color_light, ContextCompat.getColor(context, R.color.color_ffffff_33))

                jumpDuration = a.getInt(R.styleable.JumpingDotsLoaderView_jump_duration, JUMP_ANIMATION_DURATION)
                colorDuration = a.getInt(R.styleable.JumpingDotsLoaderView_color_duration, COLOR_ANIMATION_DURATION)
                jumpDelay = a.getInt(R.styleable.JumpingDotsLoaderView_jump_start_delay, DELAY_BETWEEN_DOTS)
                dots = ArrayList()
                for (i in 0 until NUMBER_OF_DOTS) {
                    dots.add(Dot(radius, colorDark))
                }
            } finally {
                a.recycle()
            }
        }

        setDefaultDotColors()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setAnimate(true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setAnimate(false)
    }

    //region These functions are used for binding directly in the xml, please be careful while renaming these functions
    fun setAnimate(startAnimation: Boolean) {
        if (startAnimation) {
            if (!isAnimationRunning()) startAnimation()
        } else {
            postStopAnimation()
        }
    }

    fun setDotRotationPercentage(percent: Float) {
        if (isAnimationRunning()) stopAnimation()

        val adjusted = when {
            percent > 1F -> 1F
            percent < 0.6 -> 0F
            else -> (percent - 0.6F) / 0.4F
        }

        rotation = -adjusted * DOTS_ROTATION_DEGREES
    }

    fun setColorDark(@ColorRes colorResId: Int) {
        colorDark = ContextCompat.getColor(context, colorResId)
    }

    fun setColorMedium(@ColorRes colorResId: Int) {
        colorMedium = ContextCompat.getColor(context, colorResId)
    }

    fun setColorLight(@ColorRes colorResId: Int) {
        colorLight = ContextCompat.getColor(context, colorResId)
    }
    //endregion

    private fun postStopAnimation() {
        post { stopAnimation() }
    }

    private fun setDefaultDotColors() {
        dots[0].setColor(colorDark)
        dots[1].setColor(colorMedium)
        dots[2].setColor(colorLight)
    }

    private fun resetRotationWithAnimation(nextStep: () -> Unit) {
        val rotationAnimator = animate().rotation(0F)
        rotationAnimator.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                rotation = 0f
                nextStep()
            }

            override fun onAnimationCancel(animation: Animator?) {
                rotation = 0f
            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        rotationAnimator.duration = context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        rotationAnimator.start()
    }

    private fun isAnimationRunning(): Boolean {
        val dot = dots[0]
        dot.positionAnimator?.let {
            return it.isRunning
        }
        return false
    }

    private fun startAnimation() {
        resetRotationWithAnimation {
            resetDots()
            rotation = 0F
            dots.forEach {
                it.positionAnimator?.start()
                it.colorAnimator?.start()
            }
        }
    }


    fun stopAnimation() {
        rotation = 0F

        for (i in 0 until dots.size) {
            val dot = dots[i]
            dot.positionAnimator?.repeatCount = 0
            dot.positionAnimator?.end()
            dot.colorAnimator?.repeatCount = 0
            dot.colorAnimator?.end()
        }

        setDefaultDotColors()
    }

    private fun resetDots() {
        for (i in 0 until dots.size) {
            val dot = dots[i]
            dot.setColor(colorDark)
            dot.positionAnimator = createPositionAnimatorForDot(dot)
            dot.positionAnimator?.startDelay = (DELAY_BETWEEN_DOTS * i).toLong()
            dot.colorAnimator = createColorAnimatorForDot(dot)
            dot.colorAnimator?.startDelay = (DELAY_BETWEEN_DOTS * i).toLong()
        }
    }


    private fun createPositionAnimatorForDot(dot: Dot): ValueAnimator {
        val animator = ValueAnimator.ofFloat(
            startY, endY, startY
        )
        animator.duration = JUMP_ANIMATION_DURATION.toLong()
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener(PositionUpdater(dot, this))
        animator.interpolator = LinearOutSlowInInterpolator()
        return animator
    }

    private fun createColorAnimatorForDot(dot: Dot): ValueAnimator {

        val animator = ValueAnimator.ofObject(
            ArgbEvaluator(), colorDark,
            colorLight, colorDark)
        animator.duration = COLOR_ANIMATION_DURATION.toLong()
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener(ColorUpdateListener(dot, this))
        animator.interpolator = LinearOutSlowInInterpolator()
        return animator

    }

    class ColorUpdateListener(private val dot: Dot, jumpingDotsView: JumpingDotsLoaderView) : ValueAnimator.AnimatorUpdateListener {
        private val dotsViewRef: WeakReference<JumpingDotsLoaderView> = WeakReference(jumpingDotsView)

        override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
            val animatedValue = valueAnimator.animatedValue as Int
            dot.setColor(animatedValue)
            val dotLoader = dotsViewRef.get()
            dotLoader?.invalidate()
        }
    }

    class PositionUpdater(private val dot: Dot, jumpingDotsView: JumpingDotsLoaderView) :
        ValueAnimator.AnimatorUpdateListener {
        private val dotsViewRef: WeakReference<JumpingDotsLoaderView> = WeakReference(jumpingDotsView)

        override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
            dot.dotY = valueAnimator.animatedValue as Float
            val dotLoader = dotsViewRef.get()

            dotLoader?.invalidate()
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        dots.forEach { it.draw(canvas) }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int
        val desiredHeight = jumpHeight + paddingTop + paddingBottom

        height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> Math.min(desiredHeight, heightSize)
            MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> desiredHeight
        }
        val desiredWidth = 2 * radius + (dotSpace + 2 * radius) * (dots.size - 1)
        width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> Math.min(desiredWidth, widthSize)
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> desiredWidth
        }

        startY = (height - radius).toFloat()
        endY = radius.toFloat()

        for (i in 0 until dots.size) {
            dots[i].dotX = radius + i * dotSpace.toFloat() + i * 2 * radius
            dots[i].dotY = (jumpHeight - radius).toFloat()
        }

        setMeasuredDimension(width, height)

        pivotX = dots[NUMBER_OF_DOTS - 1].dotX
        pivotY = (height / 2).toFloat()
    }
}