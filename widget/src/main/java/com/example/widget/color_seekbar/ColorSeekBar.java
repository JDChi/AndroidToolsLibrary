package com.example.widget.color_seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.widget.R;

/**
 * 带有渐变颜色的seekbar
 */
public class ColorSeekBar extends View {

    private float minThumbRadius = 16f;
    private int[] colorSeeds;
    private int canvasHeight = 40;
    private int barHeight = 20;
    private RectF rectF = new RectF();
    private Paint rectPaint = new Paint();

    private int mMax = 255;
    private int mMin = 0;

    private Paint thumbInnerPaint = new Paint();
    private Paint thumbPaint = new Paint();
    private LinearGradient colorGradient;
    private float thumbX = 24f;
    private float thumbY = (float)canvasHeight / 2;
    private float thumbBorder = 8f;
    private float thumbRadius = 16f;
    private float thumbBorderRadius = thumbRadius + thumbBorder;
    private int thumbInnerColor = Color.BLACK;
    private float paddingStart = 30f;
    private float paddingEnd = 30f;
    private float barCornerRadius = 8f;
    private float barLeft;
    private float barRight;
    private float barTop;
    private float barBottom;
    private int currentValue = -1;
    private boolean hasCurrentValue;


    private int mType = TYPE_ALPHA;
    /**
     * 透明度
     */
    public static final int TYPE_ALPHA = 0x01;
    /**
     * 饱和度
     */
    public static final int TYPE_SATURATION = 0x02;

    private  OnColorChangeListener colorChangeListener;

    public void setOnColorChangeListener(OnColorChangeListener l) {
        this.colorChangeListener = l;
    }

    interface OnColorChangeListener {
        void onColorChangeListener(int color);
    }

    public ColorSeekBar(Context context) {
        super(context , null);
        init(context , null);
    }

    public ColorSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context , attrs);
    }

    private void init(Context context , AttributeSet attrs){

        TypedArray typedArray = context.obtainStyledAttributes(attrs , R.styleable.ColorSeekBar);
        barCornerRadius = typedArray.getDimension(R.styleable.ColorSeekBar_cornerRadius, 8f);
        barHeight = (int)typedArray.getDimension(R.styleable.ColorSeekBar_barHeight, 20f);
        thumbBorder = typedArray.getDimension(R.styleable.ColorSeekBar_thumbBorder, 8f);
        thumbInnerColor = typedArray.getColor(R.styleable.ColorSeekBar_thumbBorderColor, Color.WHITE);
        typedArray.recycle();

        rectPaint.setAntiAlias(true);
        thumbInnerPaint.setAntiAlias(true);
        thumbInnerPaint.setColor(thumbInnerColor);
        thumbPaint.setAntiAlias(true);

        if(thumbRadius < minThumbRadius){
            thumbRadius = minThumbRadius;
        }else {
            thumbRadius = (float)barHeight / 2;
        }

        thumbBorderRadius = thumbRadius + thumbBorder;
        canvasHeight = (int) (thumbBorderRadius * 3);
        thumbY = (float)canvasHeight / 2;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                getParent().requestDisallowInterceptTouchEvent(true);
                thumbX = event.getX();
                invalidate();

                if (colorChangeListener != null) {
                    colorChangeListener.onColorChangeListener(getColor());
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //如果类型为透明度
        if (mType == TYPE_ALPHA) {
            colorSeeds = new int[]{rgbWithAlpha(thumbPaint.getColor(), mMax), rgbWithAlpha(thumbPaint.getColor(), mMin)};
            colorGradient = new LinearGradient(0f, 0f, w, 0f, colorSeeds, null, Shader.TileMode.CLAMP);
        } else {
            colorSeeds = new int[]{rgbWithSaturation(thumbPaint.getColor(), mMax), rgbWithSaturation(thumbPaint.getColor(), mMin)};
            colorGradient = new LinearGradient(0f, 0f, w, 0f, colorSeeds, null, Shader.TileMode.CLAMP);
        }

        //设置渐变效果
        rectPaint.setShader(colorGradient);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, canvasHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

         barLeft = paddingStart;
         barRight = getWidth() - paddingEnd;
         barTop = (float)((canvasHeight / 2 ) - (barHeight / 2));
         barBottom = (float)((canvasHeight / 2) + (barHeight / 2));

        rectF.set(barLeft , barTop , barRight , barBottom);
        canvas.drawRoundRect(rectF , barCornerRadius, barCornerRadius, rectPaint);

        //如果有设置当前值，调用setCurrentValue()
        if(hasCurrentValue){
            thumbX = (int)((float)currentValue / 255.0  * (barRight - barLeft));
            hasCurrentValue = false;
        }else {
            if (thumbX < barLeft) {
                thumbX = barLeft;
            } else if (thumbX > barRight) {
                thumbX = barRight;
            }
        }

        int color = pickColor(thumbX, getWidth());
        thumbPaint.setColor(color);

        // 画滑块
        canvas.drawCircle(thumbX, thumbY, thumbBorderRadius, thumbPaint);
        canvas.drawCircle(thumbX, thumbY, thumbRadius, thumbInnerPaint);
    }

    /**
     * 获取颜色
     * @param position
     * @param canvasWidth
     * @return
     */
    private int pickColor(float position , int canvasWidth){
        //value 的范围为 min 到 max
        float value = mMax - ((position - paddingStart) / (canvasWidth - (paddingStart + paddingEnd)) * mMax);
        if(mType == TYPE_ALPHA){
            if(value <= mMin){
                return rgbWithAlpha(thumbPaint.getColor() , mMin);
            }else if(value >= mMax){
                return rgbWithAlpha(thumbPaint.getColor() , mMax);
            }else {
                return rgbWithAlpha(thumbPaint.getColor() , (int) value);
            }
        }else {
            if(value <= mMin){
                return rgbWithSaturation(thumbPaint.getColor() , mMin);
            }else if(value >= mMax){
                return rgbWithSaturation(thumbPaint.getColor() , mMax);
            }else {
                return rgbWithSaturation(thumbPaint.getColor() , (int) value);
            }
        }
    }



    /**
     * 修改透明度
     *
     * @param color
     * @param currentAlpha
     * @return 修改后的颜色值
     */
    private int rgbWithAlpha(int color, int currentAlpha) {
        if (currentAlpha < 0) {
            currentAlpha = 0;
        } else if (currentAlpha > 255) {
            currentAlpha = 255;
        }

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        int argb = Color.argb(currentAlpha, red, green, blue);
        return argb;
    }

    /**
     * 修改饱和度
     *
     * @param color
     * @param currentSaturation
     * @return 修改后的颜色值
     */
    private int rgbWithSaturation(int color, int currentSaturation) {
        if (currentSaturation < 0) {
            currentSaturation = 0;
        } else if (currentSaturation > 255) {
            currentSaturation = 255;
        }

        float[] hsv = {0, 0, 0};
        Color.colorToHSV(color, hsv);

        float saturation = (float) currentSaturation / 255;

        hsv[1] = saturation;
        int newColor = Color.HSVToColor(hsv);
        return newColor;
    }

    public int getColor(){
        return thumbPaint.getColor();
    }

    /**
     * 设置类型
     *
     * @param type
     */
    public void setType(int type) {
        this.mType = type;
        invalidate();
    }

    public void setColor(int color) {
        thumbPaint.setColor(color);
        invalidate();

    }

    public void setMin(int min) {
        if(min <= 0){
            this.mMin = 0;
        }else {
            this.mMin = min;
        }

        invalidate();

    }

    /**
     * 设置当前值
     * @param value
     */
    public void setCurrentValue(int value){
        hasCurrentValue = true;
        if(value < 0){
            currentValue = 0;
        }else if(value > 255){
            currentValue = 255;
        }else {
            currentValue = value;
        }
        invalidate();

    }

    public void setMax(int max) {
        if(max >= 255){
            this.mMax = 255;
        }else {
            this.mMax = max;
        }
        invalidate();
    }




}
