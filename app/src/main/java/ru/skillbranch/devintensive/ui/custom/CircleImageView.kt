package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpToPx
import kotlin.math.max


class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BG_COLOR = Color.WHITE
        private const val DEFAULT_SIZE = 40
    }

    @Px
    var borderWidth: Float = context.dpToPx(DEFAULT_BORDER_WIDTH)
    @ColorInt
    private var borderColor: Int = DEFAULT_BORDER_COLOR
    private var bgColor: Int = DEFAULT_BG_COLOR
    private var initials: String? = null

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val avatarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val initialsPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()
    private val borderRect = Rect()
    private var size = 0

    private var isAvatarMode = true

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderWidth = ta.getDimension(
                R.styleable.CircleImageView_cv_borderWidth,
                context.dpToPx(DEFAULT_BORDER_WIDTH)
            )

            borderColor = ta.getColor(
                R.styleable.CircleImageView_cv_borderColor,
                DEFAULT_BORDER_COLOR
            )

            bgColor = getAttrOfTheme(R.attr.colorAccent)

            ta.recycle()
        }

        scaleType = ScaleType.CENTER_CROP
        setup()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.e("AvatarImageView", "onMeasure")
        val initSize = resolveDefaultSize(widthMeasureSpec)
        val maxSize = max(initSize, size)
        setMeasuredDimension(maxSize, maxSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.e("AvatarImageView", "onSizeChanged: ")
        if (w == 0) return
        with(viewRect) {
            left = 0
            top = 0
            right = w
            bottom = h
        }

        prepareShader(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        Log.e("AvatarImageView", "onDraw: ")
        // NOT allocate, ONLY draw

        if (drawable != null && isAvatarMode) drawAvatar(canvas)
        else drawInitials(canvas)

        //resize rect
        val half = (borderWidth / 2).toInt()
        with(borderRect) {
            set(viewRect)
            inset(half, half)
        }
        canvas.drawOval(borderRect.toRectF(), borderPaint)
    }

    override fun onSaveInstanceState(): Parcelable? {
        Log.e("AvatarImageView", "onSaveInstanceState $id")
        val savedState = SavedState(super.onSaveInstanceState())
        with(savedState) {
            ssIsAvatarMode = isAvatarMode
            ssBorderWidth = borderWidth
            ssBorderColor = borderColor
        }
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        Log.e("AvatarImageView", "onRestoreInstanceState $id")
        super.onRestoreInstanceState(state)
        if (state is SavedState) {
            state.also {
                isAvatarMode = it.ssIsAvatarMode
                borderWidth = it.ssBorderWidth
                borderColor = it.ssBorderColor
            }

            with(borderPaint) {
                color = borderColor
                strokeWidth = borderWidth
            }
        }
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        if (isAvatarMode) prepareShader(width, height)
        Log.e("AvatarImageView", "setImageBitmap")
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (isAvatarMode) prepareShader(width, height)
        Log.e("AvatarImageView", "setImageDrawable")
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        if (isAvatarMode) prepareShader(width, height)
        Log.e("AvatarImageView", "setImageResource")
    }

    fun setInitials(initials: String?) {
        Log.e("AvatarImageView", "setInitials : $initials")
        this.initials = initials
        if (initials != null) invalidate()
    }

    fun setBorderColor(@ColorInt color: Int) {
        Log.e("AvatarImageView", "setBorderColor : $color")
        borderColor = color
        borderPaint.color = borderColor
        invalidate()
    }

    fun setBorderWidth(@Dimension width: Int) {
        Log.e("AvatarImageView", "setBorderColor : $width")
        borderWidth = context.dpToPx(width)
        borderPaint.strokeWidth = borderWidth
        invalidate()
    }

    private fun setup() {
        with(borderPaint) {
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderColor
        }
    }

    private fun prepareShader(w: Int, h: Int) {
        if (w == 0 || drawable == null) return
        val srcBm = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)
        avatarPaint.shader = BitmapShader(srcBm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }

    private fun resolveDefaultSize(spec: Int): Int {
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> context.dpToPx(DEFAULT_SIZE).toInt()/// resolveDefaultSize()
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec) //from spec
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec) //from spec
            else -> MeasureSpec.getSize(spec)
        }
    }

    private fun drawAvatar(canvas: Canvas) {
        canvas.drawOval(viewRect.toRectF(), avatarPaint)
    }

    private fun drawInitials(canvas: Canvas) {
        initialsPaint.color = bgColor
        canvas.drawOval(viewRect.toRectF(), initialsPaint)

        if (initials == null) {
            return
        }

        with(initialsPaint) {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = height * 0.33f
        }

        val offsetY = (initialsPaint.descent() + initialsPaint.ascent()) / 2
        canvas.drawText(
            initials!!,
            viewRect.exactCenterX(),
            viewRect.exactCenterY() - offsetY,
            initialsPaint
        )
    }

    private fun getAttrOfTheme(attrId: Int): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(attrId, value, true)
        return value.data
    }

    private class SavedState : BaseSavedState, Parcelable {
        var ssIsAvatarMode: Boolean = true
        var ssBorderWidth: Float = 0f
        var ssBorderColor: Int = 0

        constructor(superState: Parcelable?) : super(superState)

        constructor(src: Parcel) : super(src) {
            //restore state from parcel
            ssIsAvatarMode = src.readInt() == 1
            ssBorderWidth = src.readFloat()
            ssBorderColor = src.readInt()
        }

        override fun writeToParcel(dst: Parcel, flags: Int) {
            //write state to parcel
            super.writeToParcel(dst, flags)
            dst.writeInt(if (ssIsAvatarMode) 1 else 0)
            dst.writeFloat(ssBorderWidth)
            dst.writeInt(ssBorderColor)
        }

        override fun describeContents() = 0

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }

}