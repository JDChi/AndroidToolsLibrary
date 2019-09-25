package com.divyanshu.colorseekbar

import android.content.Context

import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ArrayRes
import com.example.widget.R

class ColorSeekBar(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val minThumbRadius = 16f
    private var colorSeeds = intArrayOf(Color.parseColor("#000000"),
            Color.parseColor("#ffffff"))
    private var canvasHeight: Int = 40
    private var barHeight: Int = 20
    private var rectf: RectF = RectF()
    private var rectPaint: Paint = Paint()
    /**
     * thumb内部
     */
    private var thumbInnerPaint: Paint = Paint()
    private var thumbPaint: Paint = Paint()
    private lateinit var colorGradient: LinearGradient
    private var thumbX: Float = 24f
    private var thumbY: Float = (canvasHeight / 2).toFloat()
    private var thumbBorder: Float = 8f
    private var thumbRadius: Float = 16f
    private var thumbBorderRadius: Float = thumbRadius + thumbBorder
    private var thumbInnerColor = Color.BLACK
    private var paddingStart = 30f
    private var paddingEnd = 30f
    private var barCornerRadius: Float = 8f
    private var oldThumbRadius = thumbRadius
    private var oldThumbBorderRadius = thumbBorderRadius
    private var colorChangeListener: OnColorChangeListener? = null
    private var mMax: Int = 255
    private var mMin: Int = 0

    companion object{
        /**
         * 透明度类型
         */
        val TYPE_ALPHA = 0x01
        /**
         * 饱和度类型
         */
        val TYPE_SATURATION = 0x02
    }


    private var type: Int = TYPE_ALPHA

    init {
        attributeSet.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ColorSeekBar)
            val colorsId = typedArray.getResourceId(R.styleable.ColorSeekBar_colorSeeds, 0)
            if (colorsId != 0) colorSeeds = getColorsById(colorsId)
            barCornerRadius = typedArray.getDimension(R.styleable.ColorSeekBar_cornerRadius, 8f)
            barHeight = typedArray.getDimension(R.styleable.ColorSeekBar_barHeight, 20f).toInt()
            thumbBorder = typedArray.getDimension(R.styleable.ColorSeekBar_thumbBorder, 8f)
            thumbInnerColor = typedArray.getColor(R.styleable.ColorSeekBar_thumbBorderColor, Color.WHITE)
            typedArray.recycle()
        }
        rectPaint.isAntiAlias = true

        thumbInnerPaint.isAntiAlias = true
        thumbInnerPaint.color = thumbInnerColor

        thumbPaint.isAntiAlias = true

        thumbRadius = (barHeight / 2).toFloat().let {
            if (it < minThumbRadius) minThumbRadius else it
        }
        thumbBorderRadius = thumbRadius + thumbBorder
        canvasHeight = (thumbBorderRadius * 3).toInt()
        thumbY = (canvasHeight / 2).toFloat()

        oldThumbRadius = thumbRadius
        oldThumbBorderRadius = thumbBorderRadius
    }

    private fun getColorsById(@ArrayRes id: Int): IntArray {
        if (isInEditMode) {
            val s = context.resources.getStringArray(id)
            val colors = IntArray(s.size)
            for (j in s.indices) {
                colors[j] = Color.parseColor(s[j])
            }
            return colors
        } else {
            val typedArray = context.resources.obtainTypedArray(id)
            val colors = IntArray(typedArray.length())
            for (j in 0 until typedArray.length()) {
                colors[j] = typedArray.getColor(j, Color.BLACK)
            }
            typedArray.recycle()
            return colors
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //color bar position
        val barLeft: Float = paddingStart
        val barRight: Float = width.toFloat() - paddingEnd
        val barTop: Float = ((canvasHeight / 2) - (barHeight / 2)).toFloat()
        val barBottom: Float = ((canvasHeight / 2) + (barHeight / 2)).toFloat()

        //画bar
        rectf.set(barLeft, barTop, barRight, barBottom)
        canvas?.drawRoundRect(rectf, barCornerRadius, barCornerRadius, rectPaint)

        if (thumbX < barLeft) {
            thumbX = barLeft
        } else if (thumbX > barRight) {
            thumbX = barRight
        }
        val color = pickColor(thumbX, width)
        thumbPaint.color = color

        // 画滑块
        canvas?.drawCircle(thumbX, thumbY, thumbBorderRadius, thumbPaint)
        canvas?.drawCircle(thumbX, thumbY, thumbRadius, thumbInnerPaint)
    }


    /**
     * 获取当前颜色
     */
    private fun pickColor(position: Float, canvasWidth: Int): Int {

        val value = mMax - ((position - paddingStart) / (canvasWidth - (paddingStart + paddingEnd)) * mMax)
        Log.d("ColorSeekBar" , "value $value")
        when {
            value <= mMin -> return colorSeeds[colorSeeds.size - 1]
            value >= mMax -> return colorSeeds[0]
            else -> {
                if(type == TYPE_ALPHA){
                    return rgbWithAlpha(thumbPaint.color , value.toInt())
                }else{
                    return rgbWithSaturation(thumbPaint.color , value.toInt())
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //如果类型为透明度
        if (type == TYPE_ALPHA) {
            colorSeeds = intArrayOf(rgbWithAlpha(thumbPaint.color, mMax), rgbWithAlpha(thumbPaint.color, mMin))
            colorGradient = LinearGradient(0f, 0f, w.toFloat(), 0f, colorSeeds, null, Shader.TileMode.CLAMP)
        } else {
            colorSeeds = intArrayOf(rgbWithSaturation(thumbPaint.color, mMax), rgbWithSaturation(thumbPaint.color, mMin))
            colorGradient = LinearGradient(0f, 0f, w.toFloat(), 0f, colorSeeds, null, Shader.TileMode.CLAMP)
        }

        //设置渐变效果
        rectPaint.shader = colorGradient
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, canvasHeight)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(true)
                event.x.let {
                    thumbX = it
                    invalidate()
                }
                colorChangeListener?.onColorChangeListener(getColor())
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }

    fun getColor() = thumbPaint.color

    fun setColor(color: Int) {
        thumbPaint.color = color
        invalidate()
    }

    /**
     * 设置类型
     * 值为 TYPE_ALPHA or TYPE_SATURATION
     */
    fun setType(type: Int) {
        this.type = type
        invalidate()
    }

    fun setMax(max: Int) {
        this.mMax = max
        invalidate()
    }

    fun setMin(min: Int) {
        this.mMin = min
        invalidate()
    }

    fun setOnColorChangeListener(onColorChangeListener: OnColorChangeListener) {
        this.colorChangeListener = onColorChangeListener
    }

    interface OnColorChangeListener {

        fun onColorChangeListener(color: Int)
    }

    /**
     * 修改透明度
     *
     * @param color
     * @param alpha
     * @return 修改后的颜色值
     */
    private fun rgbWithAlpha(color: Int, alpha: Int): Int {
        var currentAlpha = alpha
        if (currentAlpha < 0) {
            currentAlpha = 0
        } else if (currentAlpha > 255) {
            currentAlpha = 255
        }

        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        return Color.argb(currentAlpha, red, green, blue)
    }

    /**
     * 修改饱和度
     *
     * @param color
     * @param saturation
     * @return 修改后的颜色值
     */
    private fun rgbWithSaturation(color: Int, saturation: Int): Int {
        var currentSaturation = saturation
        if (currentSaturation < 0) {
            currentSaturation = 0
        } else if (currentSaturation > 255) {
            currentSaturation = 255
        }

        val hsv = floatArrayOf(0f, 0f, 0f)
        Color.colorToHSV(color, hsv)

        //饱和度的取值范围为 0 - 1
        hsv[1] = currentSaturation.toFloat() / 255
        return Color.HSVToColor(hsv)
    }


}