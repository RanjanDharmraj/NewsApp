package com.dreamsunited.newsapp.view.loadable

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.dreamsunited.newsapp.R

private const val DEFAULT_ANIMATION_DURATION = 1600
private const val DEFAULT_ANGLE = 0
private const val SHIMMER_MASK_DEFAULT_WIDTH = 1f
private const val SHIMMER_GRADIENT_DEFAULT_WIDTH = 0.66f

open class SkeletonShimmerLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attrs, defStyle) {

    private var shimmerMaskOffset: Int = 0
    private var isAnimationRunning: Boolean = false
    private var autoStart: Boolean = false
    private var animationDuration: Int = 0
    private var shimmerColor: Int = 0
    private var shimmerAngle: Int = 0
    private var shimmerWidth = 0f
    private var gradientRelativeWidth = 0f

    private var drawRect: Rect? = null
    private var shimmerPaint: Paint? = null
    private var shimmerAnimator: ValueAnimator? = null

    private var localMaskBitmap: Bitmap? = null
    private var maskBitmap: Bitmap? = null
    private var shimmerMaskCanvas: Canvas? = null

    private var startAnimationPreDrawListener: ViewTreeObserver.OnPreDrawListener? = null

    init {
        setWillNotDraw(false)
        val a = context.getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.SkeletonShimmer,
            0, 0)

        try {
            shimmerAngle = a.getInteger(R.styleable.SkeletonShimmer_skeleton_shimmer_angle, DEFAULT_ANGLE)
            animationDuration = a.getInteger(R.styleable.SkeletonShimmer_skeleton_shimmer_animation_duration, DEFAULT_ANIMATION_DURATION)
            shimmerColor = a.getColor(R.styleable.SkeletonShimmer_skeleton_shimmer_color, ContextCompat.getColor(context, R.color.shimmer_default_color))
            autoStart = a.getBoolean(R.styleable.SkeletonShimmer_skeleton_shimmer_auto_start, true)
            shimmerWidth = a.getFloat(R.styleable.SkeletonShimmer_skeleton_shimmer_width, SHIMMER_MASK_DEFAULT_WIDTH)
            gradientRelativeWidth = a.getFloat(R.styleable.SkeletonShimmer_skeleton_shimmer_gradient_relative_width, SHIMMER_GRADIENT_DEFAULT_WIDTH)
        } finally {
            a.recycle()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            autoStart = false
        }

        if (autoStart && visibility == View.VISIBLE) {
            startShimmerAnimation()
        }
    }

    fun recalculate() {
        drawRect = calculateBitmapMaskRect()
    }

    private fun getShimmerAnimator(): Animator? {
        if (shimmerAnimator != null) {
            return shimmerAnimator
        }

        if (drawRect == null) {
            drawRect = calculateBitmapMaskRect()
        }

        drawRect?.run {
            val endX = width
            val startX = if (width > width()) {
                -endX
            } else {
                width()
            }

            val shimmerBitmapWidth = width()
            val shimmerAnimationLength = endX - startX

            shimmerAnimator = ValueAnimator.ofInt(0, shimmerAnimationLength)
            shimmerAnimator?.run {

                duration = animationDuration.toLong()
                repeatCount = ValueAnimator.INFINITE

                addUpdateListener { animation ->
                    val translationX = animation.animatedValue as Int
                    shimmerMaskOffset = startX + translationX

                    if (shimmerMaskOffset + shimmerBitmapWidth >= 0) {
                        invalidate()
                    }
                }
            }

        }

        return shimmerAnimator
    }

    /*
     *  The relative positions [0..1] of each corresponding color in the colors array
     * */
    private fun getColorGradientRelativePositions(): FloatArray {
        val colorDistribution = FloatArray(4)

        colorDistribution[0] = 0f
        colorDistribution[1] = 0.5f - gradientRelativeWidth / 2f
        colorDistribution[2] = 0.5f + gradientRelativeWidth / 2f
        colorDistribution[3] = 1f

        return colorDistribution
    }


    override fun onDetachedFromWindow() {
        resetShimmering()
        stopShimmerAnimation()
        super.onDetachedFromWindow()
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (!isAnimationRunning || width <= 0 || height <= 0) {
            super.dispatchDraw(canvas)
        } else {
            dispatchDrawShimmer(canvas)
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == View.VISIBLE) {
            if (autoStart) {
                startShimmerAnimation()
            }
        } else {
            stopShimmerAnimation()
        }
    }

    open fun startShimmerAnimation(delayMillis: Long = 0) {
        if (delayMillis > 0) {
            startShimmerAnimationWithDelay(delayMillis)
            return
        }
        if (isAnimationRunning) {
            return
        }

        if (width == 0) {
            startAnimationPreDrawListener = object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    startShimmerAnimation()
                    return true
                }
            }

            viewTreeObserver.addOnPreDrawListener(startAnimationPreDrawListener)

            return
        }

        val animator = getShimmerAnimator()
        animator?.start()
        isAnimationRunning = true
    }

    private fun startShimmerAnimationWithDelay(delayMillis: Long) {
        if (isAnimationRunning) return
        if (width == 0) return
        val animator = getShimmerAnimator() as? ValueAnimator
        animator?.repeatCount = 0
        animator?.removeAllListeners()
        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (isAnimationRunning) {
                    postDelayed({ startShimmerAnimationWithDelay(delayMillis) }, delayMillis)
                    isAnimationRunning = false
                }
            }
        })
        animator?.start()
        isAnimationRunning = true
    }

    open fun stopShimmerAnimation() {
        if (startAnimationPreDrawListener != null) {
            viewTreeObserver.removeOnPreDrawListener(startAnimationPreDrawListener)
        }

        resetShimmering()
    }

    private fun dispatchDrawShimmer(canvas: Canvas) {
        super.dispatchDraw(canvas)

        localMaskBitmap = getShimmerMaskBitmap()
        if (localMaskBitmap == null) {
            return
        }

        if (shimmerMaskCanvas == null) {
            shimmerMaskCanvas = Canvas(localMaskBitmap!!)
        }

        shimmerMaskCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        shimmerMaskCanvas?.save()
        shimmerMaskCanvas?.translate(-shimmerMaskOffset.toFloat(), 0.toFloat())

        super.dispatchDraw(shimmerMaskCanvas)

        shimmerMaskCanvas?.restore()

        drawShimmer(canvas)

        localMaskBitmap = null
    }

    private fun drawShimmer(destinationCanvas: Canvas) {
        createShimmerPaint()

        destinationCanvas.save()

        destinationCanvas.translate(shimmerMaskOffset.toFloat(), 0.toFloat())
        drawRect?.run {
            shimmerPaint?.run {
                destinationCanvas.drawRect(left.toFloat(), 0.toFloat(), width().toFloat(), height().toFloat(), this)
            }
        }

        destinationCanvas.restore()
    }

    private fun resetShimmering() {
        if (shimmerAnimator != null) {
            shimmerAnimator?.end()
            shimmerAnimator?.removeAllUpdateListeners()
        }

        shimmerAnimator = null
        shimmerPaint = null
        isAnimationRunning = false

        releaseBitMaps()
    }

    private fun releaseBitMaps() {
        shimmerMaskCanvas = null

        if (maskBitmap != null) {
            maskBitmap?.recycle()
            maskBitmap = null
        }
    }

    private fun getShimmerMaskBitmap(): Bitmap? {
        if (maskBitmap == null) {
            maskBitmap = try {
                drawRect?.takeIf { it.width() > 0 && height > 0 }?.let {
                    Bitmap.createBitmap(it.width(), height, Bitmap.Config.ALPHA_8)
                }
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
                return null
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
        return maskBitmap
    }

    private fun createShimmerPaint() {
        if (shimmerPaint != null) {
            return
        }

        val shimmerEdgeGradient = removeAlphaFromColor(shimmerColor)
        val shimmerLineWidth = width / 3 * shimmerWidth
        val yPosition = (if (0 <= shimmerAngle) height else 0).toFloat()

        val gradient = LinearGradient(
            0f, yPosition,
            Math.cos(Math.toRadians(shimmerAngle.toDouble())).toFloat() * shimmerLineWidth,
            yPosition + Math.sin(Math.toRadians(shimmerAngle.toDouble())).toFloat() * shimmerLineWidth,
            intArrayOf(shimmerEdgeGradient, shimmerColor, shimmerColor, shimmerEdgeGradient),
            getColorGradientRelativePositions(),
            Shader.TileMode.CLAMP)

        localMaskBitmap?.let {

            val maskBitmapShader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            val composeShader = ComposeShader(gradient, maskBitmapShader, PorterDuff.Mode.DST_IN)

            shimmerPaint = Paint()
            shimmerPaint?.run {
                isAntiAlias = true
                isDither = true
                isFilterBitmap = true
                shader = composeShader
            }
        }

    }


    private fun removeAlphaFromColor(actualColor: Int): Int {
        return Color.argb(0, Color.red(actualColor), Color.green(actualColor), Color.blue(actualColor))
    }

    private fun calculateBitmapMaskRect(): Rect {
        return Rect(0, 0, calculateMaskWidth(), height)
    }

    private fun calculateMaskWidth(): Int {
        val shimmerLineBottomWidth = width / 3 * shimmerWidth / Math.cos(Math.toRadians(Math.abs(shimmerAngle).toDouble()))
        val shimmerLineRemainingTopWidth = height * Math.tan(Math.toRadians(Math.abs(shimmerAngle).toDouble()))

        return (shimmerLineBottomWidth + shimmerLineRemainingTopWidth).toInt()
    }

}