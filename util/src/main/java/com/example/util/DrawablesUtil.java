package com.example.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.StateSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DrawablesUtil {


    private DrawablesUtil() {
    }

    public static DrawablesUtil.GradientResource shape() {
        return new DrawablesUtil.GradientResource();
    }

    public static DrawablesUtil.StateListResource selector() {
        return new DrawablesUtil.StateListResource();
    }

    public static DrawablesUtil.ColorStateListResource colorSelector() {
        return new DrawablesUtil.ColorStateListResource();
    }

    public static DrawablesUtil.LevelListResource levels() {
        return new DrawablesUtil.LevelListResource();
    }

    public static DrawablesUtil.LayerResource layers() {
        return new DrawablesUtil.LayerResource();
    }

    public static DrawablesUtil.ClipResource clip() {
        return new DrawablesUtil.ClipResource();
    }

    public static DrawablesUtil.InsetResource inset() {
        return new DrawablesUtil.InsetResource();
    }

    public static DrawablesUtil.BitmapResource bitmap() {
        return new DrawablesUtil.BitmapResource();
    }

    public static void setBackground(View view, Drawable bg) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(bg);
        } else {
            view.setBackgroundDrawable(bg);
        }

    }

    private static int dp2px(Context ctx, float dpValue) {
        if (dpValue == 0.0F) {
            return 0;
        } else {
            float density = ctx.getResources().getDisplayMetrics().density;
            return (int)(dpValue * density + 0.5F);
        }
    }

    private static int px2dp(Context ctx, float pxValue) {
        if (pxValue == 0.0F) {
            return 0;
        } else {
            float density = ctx.getResources().getDisplayMetrics().density;
            return (int)(pxValue / density + 0.5F);
        }
    }

    public static class BitmapResource {
        private Resources mResources;
        private Bitmap mBitmap;
        private String mFilePath;
        private InputStream mInputStream;
        private Shader.TileMode mTileMode;

        private BitmapResource() {
        }

        public DrawablesUtil.BitmapResource bitmap(Resources res, Bitmap bitmap) {
            this.mResources = res;
            this.mBitmap = bitmap;
            return this;
        }

        public DrawablesUtil.BitmapResource bitmap(Resources res, String filepath) {
            this.mResources = res;
            this.mFilePath = filepath;
            return this;
        }

        public DrawablesUtil.BitmapResource bitmap(Resources res, InputStream inputStream) {
            this.mResources = res;
            this.mInputStream = inputStream;
            return this;
        }

        public DrawablesUtil.BitmapResource tileModeDisabled() {
            this.mTileMode = null;
            return this;
        }

        public DrawablesUtil.BitmapResource tileModeClamp() {
            this.mTileMode = Shader.TileMode.CLAMP;
            return this;
        }

        public DrawablesUtil.BitmapResource tileModeRepeat() {
            this.mTileMode = Shader.TileMode.REPEAT;
            return this;
        }

        public DrawablesUtil.BitmapResource tileModeMirror() {
            this.mTileMode = Shader.TileMode.MIRROR;
            return this;
        }

        public BitmapDrawable create() {
            BitmapDrawable drawable;
            if (this.mFilePath != null) {
                drawable = new BitmapDrawable(this.mResources, this.mFilePath);
            } else if (this.mInputStream != null) {
                drawable = new BitmapDrawable(this.mResources, this.mInputStream);
            } else {
                drawable = new BitmapDrawable(this.mResources, this.mBitmap);
            }

            drawable.setTileModeXY(this.mTileMode, this.mTileMode);
            return drawable;
        }

        public BitmapDrawable createAsBackground(View view) {
            BitmapDrawable drawable = this.create();
            DrawablesUtil.setBackground(view, drawable);
            return drawable;
        }
    }

    public static class InsetResource {
        private Drawable mDrawable;
        private Rect mRect;

        private InsetResource() {
        }

        public DrawablesUtil.InsetResource drawable(Drawable drawable) {
            this.mDrawable = drawable;
            return this;
        }

        public DrawablesUtil.InsetResource drawable(Context ctx, int drawableId) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            return this.drawable(d);
        }

        public DrawablesUtil.InsetResource inset(int l, int t, int r, int b) {
            this.mRect = new Rect(l, t, r, b);
            return this;
        }

        public DrawablesUtil.InsetResource inset(Context ctx, float l, float t, float r, float b) {
            return this.inset(DrawablesUtil.dp2px(ctx, l), DrawablesUtil.dp2px(ctx, t), DrawablesUtil.dp2px(ctx, r), DrawablesUtil.dp2px(ctx, b));
        }

        public InsetDrawable create() {
            return new InsetDrawable(this.mDrawable, this.mRect.left, this.mRect.top, this.mRect.right, this.mRect.bottom);
        }

        public InsetDrawable createAsBackground(View view) {
            InsetDrawable drawable = this.create();
            DrawablesUtil.setBackground(view, drawable);
            return drawable;
        }
    }

    public static class ClipResource {
        private Drawable mDrawable;
        private int mGravity;
        private boolean mHorizontal;

        private ClipResource() {
        }

        public DrawablesUtil.ClipResource drawable(Drawable drawable) {
            this.mDrawable = drawable;
            return this;
        }

        public DrawablesUtil.ClipResource drawable(Context ctx, int drawableId) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            return this.drawable(d);
        }

        public DrawablesUtil.ClipResource gravity(int gravity) {
            this.mGravity = gravity;
            return this;
        }

        public DrawablesUtil.ClipResource horizontal() {
            this.mHorizontal = true;
            return this;
        }

        public DrawablesUtil.ClipResource vertical() {
            this.mHorizontal = false;
            return this;
        }

        public ClipDrawable create() {
            int orientation = this.mHorizontal ? 1 : 2;
            return new ClipDrawable(this.mDrawable, this.mGravity, orientation);
        }

        public ClipDrawable createAsBackground(View view) {
            ClipDrawable drawable = this.create();
            DrawablesUtil.setBackground(view, drawable);
            return drawable;
        }
    }

    public static class LayerResource {
        private List<Drawable> mDrawables;
        private List<Rect> mInsets;

        private LayerResource() {
            this.mDrawables = new ArrayList();
            this.mInsets = new ArrayList();
        }

        public DrawablesUtil.LayerResource item(Drawable drawable, int l, int t, int r, int b) {
            this.mDrawables.add(drawable);
            this.mInsets.add(new Rect(l, t, r, b));
            return this;
        }

        public DrawablesUtil.LayerResource item(Drawable d, Context ctx, float l, float t, float r, float b) {
            return this.item(d, DrawablesUtil.dp2px(ctx, l), DrawablesUtil.dp2px(ctx, t), DrawablesUtil.dp2px(ctx, r), DrawablesUtil.dp2px(ctx, b));
        }

        public LayerDrawable create() {
            int size = this.mDrawables.size();
            Drawable[] drawables = new Drawable[size];

            for(int i = 0; i < size; ++i) {
                drawables[i] = (Drawable)this.mDrawables.get(i);
            }

            LayerDrawable layerList = new LayerDrawable(drawables);

            for(int i = 0; i < size; ++i) {
                Rect rect = (Rect)this.mInsets.get(i);
                layerList.setLayerInset(i, rect.left, rect.top, rect.right, rect.bottom);
            }

            return layerList;
        }

        public LayerDrawable createAsBackground(View view) {
            LayerDrawable drawable = this.create();
            DrawablesUtil.setBackground(view, drawable);
            return drawable;
        }
    }

    public static class LevelListResource {
        private List<Integer> mMins;
        private List<Integer> mMaxs;
        private List<Drawable> mDrawables;

        private LevelListResource() {
            this.mMins = new ArrayList();
            this.mMaxs = new ArrayList();
            this.mDrawables = new ArrayList();
        }

        public DrawablesUtil.LevelListResource item(Drawable drawable) {
            this.mDrawables.add(drawable);
            int level = this.mMins.size();
            this.mMins.add(level);
            this.mMaxs.add(level);
            return this;
        }

        public DrawablesUtil.LevelListResource item(Context ctx, int drawableId) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            return this.item(d);
        }

        public DrawablesUtil.LevelListResource item(Drawable drawable, int minLevel, int maxLevel) {
            this.mDrawables.add(drawable);
            this.mMins.add(minLevel);
            this.mMaxs.add(maxLevel);
            return this;
        }

        public DrawablesUtil.LevelListResource item(Context ctx, int drawableId, int minLevel, int maxLevel) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            return this.item(d, minLevel, maxLevel);
        }

        public LevelListDrawable create() {
            LevelListDrawable levelList = new LevelListDrawable();
            int size = this.mDrawables.size();

            for(int i = 0; i < size; ++i) {
                levelList.addLevel((Integer)this.mMins.get(i), (Integer)this.mMaxs.get(i), (Drawable)this.mDrawables.get(i));
            }

            return levelList;
        }

        public LevelListDrawable createAsBackground(View view) {
            LevelListDrawable drawable = this.create();
            DrawablesUtil.setBackground(view, drawable);
            return drawable;
        }

        public LevelListDrawable createAsImageDrawable(ImageView iv) {
            LevelListDrawable drawable = this.create();
            iv.setImageDrawable(drawable);
            return drawable;
        }
    }

    public static class ColorStateListResource {
        private List<int[]> mStates;
        private List<Integer> mColors;
        private int mNormalColor;

        private ColorStateListResource() {
            this.mStates = new ArrayList();
            this.mColors = new ArrayList();
        }

        public DrawablesUtil.ColorStateListResource pressed(int color, boolean enabled) {
            int state = 16842919;
            this.mStates.add(new int[]{enabled ? state : -state});
            this.mColors.add(color);
            return this;
        }

        public DrawablesUtil.ColorStateListResource pressed(String color, boolean enabled) {
            return this.pressed(Color.parseColor(color), enabled);
        }

        public DrawablesUtil.ColorStateListResource selected(int color, boolean enabled) {
            int state = 16842913;
            this.mStates.add(new int[]{enabled ? state : -state});
            this.mColors.add(color);
            return this;
        }

        public DrawablesUtil.ColorStateListResource selected(String color, boolean enabled) {
            return this.selected(Color.parseColor(color), enabled);
        }

        public DrawablesUtil.ColorStateListResource checked(int color, boolean enabled) {
            int state = 16842912;
            this.mStates.add(new int[]{enabled ? state : -state});
            this.mColors.add(color);
            return this;
        }

        public DrawablesUtil.ColorStateListResource checked(String color, boolean enabled) {
            return this.checked(Color.parseColor(color), enabled);
        }

        public DrawablesUtil.ColorStateListResource item(int color, int[] states) {
            this.mStates.add(states);
            this.mColors.add(color);
            return this;
        }

        public DrawablesUtil.ColorStateListResource item(String color, int[] states) {
            return this.item(Color.parseColor(color), states);
        }

        public DrawablesUtil.ColorStateListResource normal(int color) {
            this.mNormalColor = color;
            return this;
        }

        public DrawablesUtil.ColorStateListResource normal(String color) {
            return this.normal(Color.parseColor(color));
        }

        public ColorStateList create() {
            int size = this.mColors.size() + 1;
            int[][] allStates = new int[size][1];
            int[] allColors = new int[size];

            for(int i = 0; i < size - 1; ++i) {
                allStates[i] = (int[])this.mStates.get(i);
                allColors[i] = (Integer)this.mColors.get(i);
            }

            allStates[size - 1] = StateSet.WILD_CARD;
            allColors[size - 1] = this.mNormalColor;
            return new ColorStateList(allStates, allColors);
        }

        public ColorStateList createAsTextColor(TextView tv) {
            ColorStateList colors = this.create();
            tv.setTextColor(colors);
            return colors;
        }
    }

    public static class StateListResource {
        private List<int[]> mStates;
        private List<Drawable> mDrawables;
        private Drawable mNormalDrawable;

        private StateListResource() {
            this.mStates = new ArrayList();
            this.mDrawables = new ArrayList();
        }

        public DrawablesUtil.StateListResource pressed(Drawable drawable, boolean enabled) {
            int state = 16842919;
            this.mStates.add(new int[]{enabled ? state : -state});
            this.mDrawables.add(drawable);
            return this;
        }

        public DrawablesUtil.StateListResource pressed(Context ctx, int drawableId, boolean enabled) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            return this.pressed(d, enabled);
        }

        public DrawablesUtil.StateListResource selected(Drawable drawable, boolean enabled) {
            int state = 16842913;
            this.mStates.add(new int[]{enabled ? state : -state});
            this.mDrawables.add(drawable);
            return this;
        }

        public DrawablesUtil.StateListResource selected(Context ctx, int drawableId, boolean enabled) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            return this.selected(d, enabled);
        }

        public DrawablesUtil.StateListResource checked(Drawable drawable, boolean enabled) {
            int state = 16842912;
            this.mStates.add(new int[]{enabled ? state : -state});
            this.mDrawables.add(drawable);
            return this;
        }

        public DrawablesUtil.StateListResource checked(Context ctx, int drawableId, boolean enabled) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            return this.checked(d, enabled);
        }

        public DrawablesUtil.StateListResource item(Drawable drawable, int[] states) {
            this.mStates.add(states);
            this.mDrawables.add(drawable);
            return this;
        }

        public DrawablesUtil.StateListResource item(Context ctx, int drawableId, int[] states) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            return this.item(d, states);
        }

        public DrawablesUtil.StateListResource normal(Drawable drawable) {
            this.mNormalDrawable = drawable;
            return this;
        }

        public DrawablesUtil.StateListResource normal(Context ctx, int drawableId) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            return this.normal(d);
        }

        public StateListDrawable create() {
            StateListDrawable sld = new StateListDrawable();
            int size = this.mDrawables.size();

            for(int i = 0; i < size; ++i) {
                sld.addState((int[])this.mStates.get(i), (Drawable)this.mDrawables.get(i));
            }

            sld.addState(StateSet.WILD_CARD, this.mNormalDrawable);
            return sld;
        }

        public StateListDrawable createAsBackground(View view) {
            StateListDrawable drawable = this.create();
            DrawablesUtil.setBackground(view, drawable);
            return drawable;
        }

        public StateListDrawable createAsImageDrawable(ImageView iv) {
            StateListDrawable drawable = this.create();
            iv.setImageDrawable(drawable);
            return drawable;
        }
    }

    public static class GradientResource {
        private int mShape;
        private int mColor;
        private float[] mRadiusArray;
        private float mRadius;
        private int mStrokeWidth;
        private int mStrokeColor;
        private int mWidth;
        private int mHeight;
        private GradientDrawable.Orientation mGradientOrientation;
        private int[] mGradientColors;

        private GradientResource() {
            this.mShape = 0;
            this.mColor = -16777216;
            this.mGradientOrientation = GradientDrawable.Orientation.TOP_BOTTOM;
        }

        public DrawablesUtil.GradientResource rectangle() {
            this.mShape = 0;
            return this;
        }

        public DrawablesUtil.GradientResource oval() {
            this.mShape = 1;
            return this;
        }

        public DrawablesUtil.GradientResource color(int color) {
            this.mColor = color;
            this.mGradientColors = null;
            return this;
        }

        public DrawablesUtil.GradientResource color(String colorString) {
            this.mColor = Color.parseColor(colorString);
            this.mGradientColors = null;
            return this;
        }

        public DrawablesUtil.GradientResource colorRes(Context ctx, int colorResId) {
            this.mColor = ctx.getResources().getColor(colorResId);
            this.mGradientColors = null;
            return this;
        }

        public DrawablesUtil.GradientResource radius(float[] radiusArray) {
            this.mRadiusArray = radiusArray;
            return this;
        }

        public DrawablesUtil.GradientResource radius(Context ctx, float[] radiusArray) {
            float[] radii = new float[radiusArray.length];

            for(int i = 0; i < radii.length; ++i) {
                radii[i] = (float) DrawablesUtil.dp2px(ctx, radiusArray[i]);
            }

            this.mRadiusArray = radii;
            return this;
        }

        public DrawablesUtil.GradientResource radius(int radius) {
            this.mRadius = (float)radius;
            return this;
        }

        public DrawablesUtil.GradientResource radius(Context ctx, float radius) {
            this.mRadius = (float) DrawablesUtil.dp2px(ctx, radius);
            return this;
        }

        public DrawablesUtil.GradientResource stroke(int width, int color) {
            this.mStrokeWidth = width;
            this.mStrokeColor = color;
            return this;
        }

        public DrawablesUtil.GradientResource stroke(Context ctx, float width, int color) {
            this.mStrokeWidth = DrawablesUtil.dp2px(ctx, width);
            this.mStrokeColor = color;
            return this;
        }

        public DrawablesUtil.GradientResource stroke(int width, String color) {
            this.mStrokeWidth = width;
            this.mStrokeColor = Color.parseColor(color);
            return this;
        }

        public DrawablesUtil.GradientResource stroke(Context ctx, float width, String color) {
            this.mStrokeWidth = DrawablesUtil.dp2px(ctx, width);
            this.mStrokeColor = Color.parseColor(color);
            return this;
        }

        public DrawablesUtil.GradientResource size(int w, int h) {
            this.mWidth = w;
            this.mHeight = h;
            return this;
        }

        public DrawablesUtil.GradientResource gradient(GradientDrawable.Orientation orientation, int startColor, int endColor) {
            this.mGradientOrientation = orientation;
            this.mGradientColors = new int[]{startColor, endColor};
            return this;
        }

        public DrawablesUtil.GradientResource gradient(GradientDrawable.Orientation orientation, String startColor, String endColor) {
            return this.gradient(orientation, Color.parseColor(startColor), Color.parseColor(endColor));
        }

        public DrawablesUtil.GradientResource gradient(GradientDrawable.Orientation orientation, int startColor, int centerColor, int endColor) {
            this.mGradientOrientation = orientation;
            this.mGradientColors = new int[]{startColor, centerColor, endColor};
            return this;
        }

        public DrawablesUtil.GradientResource gradient(GradientDrawable.Orientation orientation, String startColor, String centerColor, String endColor) {
            return this.gradient(orientation, Color.parseColor(startColor), Color.parseColor(centerColor), Color.parseColor(endColor));
        }

        public GradientDrawable create() {
            GradientDrawable gd = new GradientDrawable(this.mGradientOrientation, this.mGradientColors);
            gd.setShape(this.mShape);
            if (this.mGradientColors == null) {
                gd.setColor(this.mColor);
            }

            if (this.mRadiusArray != null) {
                gd.setCornerRadii(this.mRadiusArray);
            } else {
                gd.setCornerRadius(this.mRadius);
            }

            if (this.mStrokeWidth > 0) {
                gd.setStroke(this.mStrokeWidth, this.mStrokeColor);
            }

            if (this.mWidth > 0 && this.mHeight > 0) {
                gd.setSize(this.mWidth, this.mHeight);
            }

            gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            return gd;
        }

        public GradientDrawable createAsBackground(View view) {
            GradientDrawable drawable = this.create();
            DrawablesUtil.setBackground(view, drawable);
            return drawable;
        }
    }
}

