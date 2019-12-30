package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dp

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ImageView(context, attrs, defStyleAttr) {

    companion object {
        val DEFAULT_BORDER_COLOR = Color.WHITE
        val DEFAULT_BORDER_WIDTH = 2.dp()
    }

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = DEFAULT_BORDER_WIDTH

    private lateinit var mBitmapShader: Shader
    private var mShaderMatrix: Matrix

    private var mBitmapDrawBounds: RectF
    private var mStrokeBounds: RectF

    private var mBitmap: Bitmap? = null

    private var mBitmapPaint: Paint
    private var mStrokePaint: Paint

    private var mInitialized: Boolean = false

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = typedArray.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = typedArray.getDimension(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH.toFloat()).toInt()
            typedArray.recycle()
        }

        mShaderMatrix = Matrix()

        mBitmapPaint = Paint(ANTI_ALIAS_FLAG)

        mStrokePaint = Paint(ANTI_ALIAS_FLAG)
        mStrokePaint.color = borderColor
        mStrokePaint.style = Paint.Style.STROKE
        mStrokePaint.strokeWidth = borderWidth.toFloat()

        mStrokeBounds = RectF()
        mBitmapDrawBounds = RectF()

        mInitialized = true

        setupBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setupBitmap()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        setupBitmap()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val halfStrokeWidth = mStrokePaint.strokeWidth / 2f
        updateCircleDrawBounds(mBitmapDrawBounds)
        mStrokeBounds.set(mBitmapDrawBounds)
        mStrokeBounds.inset(halfStrokeWidth, halfStrokeWidth)

        updateBitmapSize()
    }

    override fun onDraw(canvas: Canvas) {
        drawBitmap(canvas)
        drawStroke(canvas)
    }

    @Dimension fun getBorderWidth(): Int {
        return borderWidth
    }

    fun setBorderWidth(@Dimension dp :Int) {
        borderWidth = dp
        invalidate()
    }

    fun getBorderColor(): Int {
        return borderColor
    }

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        // TODO
        borderColor = resources.getColor(colorId)
        invalidate()
    }

    private fun drawStroke(canvas: Canvas) {
        if (mStrokePaint.strokeWidth > 0f) {
            canvas.drawOval(mStrokeBounds, mStrokePaint)
        }
    }

    private fun drawBitmap(canvas: Canvas) {
        canvas.drawOval(mBitmapDrawBounds, mBitmapPaint)
    }

    private fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()

        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter = contentWidth.coerceAtMost(contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }

    private fun setupBitmap() {
        if (!mInitialized) {
            return
        }

        mBitmap = getBitmapFromDrawable(drawable)
        if (mBitmap == null) {
            return
        }

        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.shader = mBitmapShader

        updateBitmapSize()
    }

    private fun updateBitmapSize() {
        if (mBitmap == null) {
            return
        }

        val dx: Float
        val dy: Float
        val scale: Float

        if (mBitmap!!.width < mBitmap!!.height) {
            scale = mBitmapDrawBounds.width() / mBitmap!!.width
            dx = mBitmapDrawBounds.left
            dy = mBitmapDrawBounds.top - (mBitmap!!.height * scale / 2f) + (mBitmapDrawBounds.width() / 2f)
        } else {
            scale = mBitmapDrawBounds.height() / mBitmap!!.height
            dx = mBitmapDrawBounds.left - (mBitmap!!.width * scale / 2f) + (mBitmapDrawBounds.width() / 2f)
            dy = mBitmapDrawBounds.top
        }

        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(dx, dy)
        mBitmapShader.setLocalMatrix(mShaderMatrix)
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

}