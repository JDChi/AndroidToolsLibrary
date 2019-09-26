package com.example.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;

import android.util.Log;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class NotchUtil {

    private NotchUtil() {
    }


    private static final String TAG = "NotchUtil";

    private static final int NOTCH_IN_SCREEN_VOIO = 0x00000020;
    private static final String MIUI_NOTCH = "ro.miui.notch";
    private static Boolean sHasNotch = null;
    private static Rect sRotation0SafeInset = null;
    private static Rect sRotation90SafeInset = null;
    private static Rect sRotation180SafeInset = null;
    private static Rect sRotation270SafeInset = null;
    private static int[] sNotchSizeInHawei = null;
    private static Boolean sHuaweiIsNotchSetToShow = null;

    public static boolean hasNotchListener(final Activity activity, final OnNotchListener listener) {
        if (activity == null || activity.getWindow() == null || activity.getWindow().getDecorView() == null) {
            return false;
        }
        if (!isNotchOfficialSupport()) {
            if (listener != null) {
                listener.onNotch(NotchUtil.hasNotch(activity));
            }
            return true;
        }
        // Android P刘海屏判断的坑,需要在decorView attach到window之后才能获取到准确信息
        activity.getWindow().getDecorView().addOnAttachStateChangeListener(
                new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        // 刘海屏判断的坑,需要在decorView attach到window之后才能获取到准确信息
                        if (listener != null) {
                            listener.onNotch(NotchUtil.hasNotch(activity));
                        }
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {

                    }
                });
        return true;
    }

    public static boolean hasNotchInVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class ftFeature = cl.loadClass("android.util.FtFeature");
            Method[] methods = ftFeature.getDeclaredMethods();
            if (methods != null) {
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    if (method.getName().equalsIgnoreCase("isFeatureSupport")) {
                        ret = (boolean) method.invoke(ftFeature, NOTCH_IN_SCREEN_VOIO);
                        break;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            Log.i(TAG, "hasNotchInVivo ClassNotFoundException");
        } catch (Exception e) {
            Log.e(TAG, "hasNotchInVivo Exception");
        }
        return ret;
    }


    public static boolean hasNotchInHuawei(Context context) {
        boolean hasNotch = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            hasNotch = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.i(TAG, "hasNotchInHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hasNotchInHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "hasNotchInHuawei Exception");
        }
        return hasNotch;
    }

    public static boolean hasNotchInOppo(Context context) {
        return context.getPackageManager()
                .hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    @SuppressLint("PrivateApi")
    public static boolean hasNotchInXiaomi(Context context) {
        try {
            Class spClass = Class.forName("android.os.SystemProperties");
            Method getMethod = spClass.getDeclaredMethod("getInt", String.class, int.class);
            getMethod.setAccessible(true);
            int hasNotch = (int) getMethod.invoke(null, MIUI_NOTCH, 0);
            return hasNotch == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasNotch(View view) {
        if (sHasNotch == null) {
            if (isNotchOfficialSupport()) {
                if (!attachHasOfficialNotch(view)) {
                    return false;
                }
            } else {
                sHasNotch = has3rdNotch(view.getContext());
            }
        }
        return sHasNotch;
    }


    public static boolean hasNotch(Activity activity) {
        if (sHasNotch == null) {
            if (isNotchOfficialSupport()) {
                Window window = activity.getWindow();
                if (window == null) {
                    return false;
                }
                View decorView = window.getDecorView();
                if (decorView == null) {
                    return false;
                }
                if (!attachHasOfficialNotch(decorView)) {
                    return false;
                }
            } else {
                sHasNotch = has3rdNotch(activity);
            }
        }
        return sHasNotch;
    }

    /**
     * @param view
     * @return false indicates the failure to get the result
     */
    @TargetApi(28)
    private static boolean attachHasOfficialNotch(View view) {
        WindowInsets windowInsets = view.getRootWindowInsets();
        if (windowInsets != null) {
            DisplayCutout displayCutout = windowInsets.getDisplayCutout();
            sHasNotch = displayCutout != null;
            return true;
        } else {
            // view not attached, do nothing
            return false;
        }
    }

    public static boolean has3rdNotch(Context context) {
        if (DeviceHelper.isHuawei()) {
            return hasNotchInHuawei(context);
        } else if (DeviceHelper.isVivo()) {
            return hasNotchInVivo(context);
        } else if (DeviceHelper.isOppo()) {
            return hasNotchInOppo(context);
        } else if (DeviceHelper.isXiaomi()) {
            return hasNotchInXiaomi(context);
        }
        return false;
    }

    public static int getSafeInsetTop(Activity activity) {
        if (!hasNotch(activity)) {
            return 0;
        }
        return getSafeInsetRect(activity).top;
    }

    public static int getSafeInsetBottom(Activity activity) {
        if (!hasNotch(activity)) {
            return 0;
        }
        return getSafeInsetRect(activity).bottom;
    }

    public static int getSafeInsetLeft(Activity activity) {
        if (!hasNotch(activity)) {
            return 0;
        }
        return getSafeInsetRect(activity).left;
    }

    public static int getSafeInsetRight(Activity activity) {
        if (!hasNotch(activity)) {
            return 0;
        }
        return getSafeInsetRect(activity).right;
    }


    public static int getSafeInsetTop(View view) {
        if (!hasNotch(view)) {
            return 0;
        }
        return getSafeInsetRect(view).top;
    }

    public static int getSafeInsetBottom(View view) {
        if (!hasNotch(view)) {
            return 0;
        }
        return getSafeInsetRect(view).bottom;
    }

    public static int getSafeInsetLeft(View view) {
        if (!hasNotch(view)) {
            return 0;
        }
        return getSafeInsetRect(view).left;
    }

    public static int getSafeInsetRight(View view) {
        if (!hasNotch(view)) {
            return 0;
        }
        return getSafeInsetRect(view).right;
    }


    private static void clearAllRectInfo() {
        sRotation0SafeInset = null;
        sRotation90SafeInset = null;
        sRotation180SafeInset = null;
        sRotation270SafeInset = null;
    }

    private static void clearPortraitRectInfo() {
        sRotation0SafeInset = null;
        sRotation180SafeInset = null;
    }

    private static void clearLandscapeRectInfo() {
        sRotation90SafeInset = null;
        sRotation270SafeInset = null;
    }

    private static Rect getSafeInsetRect(Activity activity) {
        if (isNotchOfficialSupport()) {
            Rect rect = new Rect();
            View decorView = activity.getWindow().getDecorView();
            getOfficialSafeInsetRect(decorView, rect);
            return rect;
        }
        return get3rdSafeInsetRect(activity);
    }

    private static Rect getSafeInsetRect(View view) {
        if (isNotchOfficialSupport()) {
            Rect rect = new Rect();
            getOfficialSafeInsetRect(view, rect);
            return rect;
        }
        return get3rdSafeInsetRect(view.getContext());
    }

    @TargetApi(28)
    private static void getOfficialSafeInsetRect(View view, Rect out) {
        if (view == null) {
            return;
        }
        WindowInsets rootWindowInsets = view.getRootWindowInsets();
        if (rootWindowInsets == null) {
            return;
        }
        DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
        if (displayCutout != null) {
            out.set(displayCutout.getSafeInsetLeft(), displayCutout.getSafeInsetTop(),
                    displayCutout.getSafeInsetRight(), displayCutout.getSafeInsetBottom());
        }
    }

    private static Rect get3rdSafeInsetRect(Context context) {
        // 全面屏设置项更改
        if (DeviceHelper.isHuawei()) {
//            boolean isHuaweiNotchSetToShow = DisplayHelper.huaweiIsNotchSetToShowInSetting(context);
            boolean isHuaweiNotchSetToShow = true;
            if (sHuaweiIsNotchSetToShow != null && sHuaweiIsNotchSetToShow != isHuaweiNotchSetToShow) {
                clearLandscapeRectInfo();
            }
            sHuaweiIsNotchSetToShow = isHuaweiNotchSetToShow;
        }
        int screenRotation = getScreenRotation(context);
        if (screenRotation == Surface.ROTATION_90) {
            if (sRotation90SafeInset == null) {
                sRotation90SafeInset = getRectInfoRotation90(context);
            }
            return sRotation90SafeInset;
        } else if (screenRotation == Surface.ROTATION_180) {
            if (sRotation180SafeInset == null) {
                sRotation180SafeInset = getRectInfoRotation180(context);
            }
            return sRotation180SafeInset;
        } else if (screenRotation == Surface.ROTATION_270) {
            if (sRotation270SafeInset == null) {
                sRotation270SafeInset = getRectInfoRotation270(context);
            }
            return sRotation270SafeInset;
        } else {
            if (sRotation0SafeInset == null) {
                sRotation0SafeInset = getRectInfoRotation0(context);
            }
            return sRotation0SafeInset;
        }
    }

    private static Rect getRectInfoRotation0(Context context) {
        Rect rect = new Rect();
        if (DeviceHelper.isVivo()) {
            // TODO vivo 显示与亮度-第三方应用显示比例
            rect.top = getNotchHeightInVivo(context);
            rect.bottom = 0;
        } else if (DeviceHelper.isOppo()) {
            // TODO OPPO 设置-显示-应用全屏显示-凹形区域显示控制
            rect.top = getStatusBarHeight(context);
            rect.bottom = 0;
        } else if (DeviceHelper.isHuawei()) {
            int[] notchSize = getNotchSizeInHuawei(context);
            rect.top = notchSize[1];
            rect.bottom = 0;
        } else if (DeviceHelper.isXiaomi()) {
            rect.top = getNotchHeightInXiaomi(context);
            rect.bottom = 0;
        }
        return rect;
    }

    private static Rect getRectInfoRotation90(Context context) {
        Rect rect = new Rect();
        if (DeviceHelper.isVivo()) {
            rect.left = getNotchHeightInVivo(context);
            rect.right = 0;
        } else if (DeviceHelper.isOppo()) {
            rect.left = getStatusBarHeight(context);
            rect.right = 0;
        } else if (DeviceHelper.isHuawei()) {
            if (sHuaweiIsNotchSetToShow) {
                rect.left = getNotchSizeInHuawei(context)[1];
            } else {
                rect.left = 0;
            }
            rect.right = 0;
        } else if (DeviceHelper.isXiaomi()) {
            rect.left = getNotchHeightInXiaomi(context);
            rect.right = 0;
        }
        return rect;
    }

    private static Rect getRectInfoRotation180(Context context) {
        Rect rect = new Rect();
        if (DeviceHelper.isVivo()) {
            rect.top = 0;
            rect.bottom = getNotchHeightInVivo(context);
        } else if (DeviceHelper.isOppo()) {
            rect.top = 0;
            rect.bottom = getStatusBarHeight(context);
        } else if (DeviceHelper.isHuawei()) {
            int[] notchSize = getNotchSizeInHuawei(context);
            rect.top = 0;
            rect.bottom = notchSize[1];
        } else if (DeviceHelper.isXiaomi()) {
            rect.top = 0;
            rect.bottom = getNotchHeightInXiaomi(context);
        }
        return rect;
    }

    private static Rect getRectInfoRotation270(Context context) {
        Rect rect = new Rect();
        if (DeviceHelper.isVivo()) {
            rect.right = getNotchHeightInVivo(context);
            rect.left = 0;
        } else if (DeviceHelper.isOppo()) {
            rect.right = getStatusBarHeight(context);
            rect.left = 0;
        } else if (DeviceHelper.isHuawei()) {
            if (sHuaweiIsNotchSetToShow) {
                rect.right = getNotchSizeInHuawei(context)[1];
            } else {
                rect.right = 0;
            }
            rect.left = 0;
        } else if (DeviceHelper.isXiaomi()) {
            rect.right = getNotchHeightInXiaomi(context);
            rect.left = 0;
        }
        return rect;
    }


    public static int[] getNotchSizeInHuawei(Context context) {
        if (sNotchSizeInHawei == null) {
            sNotchSizeInHawei = new int[]{0, 0};
            try {
                ClassLoader cl = context.getClassLoader();
                Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
                Method get = HwNotchSizeUtil.getMethod("getNotchSize");
                sNotchSizeInHawei = (int[]) get.invoke(HwNotchSizeUtil);
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "getNotchSizeInHuawei ClassNotFoundException");
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "getNotchSizeInHuawei NoSuchMethodException");
            } catch (Exception e) {
                Log.e(TAG, "getNotchSizeInHuawei Exception");
            }

        }
        return sNotchSizeInHawei;
    }

    public static int getNotchWidthInXiaomi(Context context) {
        int resourceId = context.getResources().getIdentifier("notch_width", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return -1;
    }

    public static int getNotchHeightInXiaomi(Context context) {
        int resourceId = context.getResources().getIdentifier("notch_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return getStatusBarHeight(context);
    }

    public static int getNotchWidthInVivo(Context context) {
        return ScreenUtil.dip2px(context, 100);
    }

    public static int getNotchHeightInVivo(Context context) {
        return ScreenUtil.dip2px(context, 27);
    }

    /**
     * this method is private, because we do not need to handle tablet
     *
     * @param context
     * @return
     */
    private static int getScreenRotation(Context context) {
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (w == null) {
            return Surface.ROTATION_0;
        }
        Display display = w.getDefaultDisplay();
        if (display == null) {
            return Surface.ROTATION_0;
        }

        return display.getRotation();
    }

    public static boolean isNotchOfficialSupport() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    /**
     * fitSystemWindows 对小米挖孔屏横屏挖孔区域无效
     *
     * @param view
     * @return
     */
    public static boolean needFixLandscapeNotchAreaFitSystemWindow(View view) {
        return DeviceHelper.isXiaomi() && hasNotch(view);
    }

    /**
     * 设置应用窗口在刘海屏手机使用刘海区
     */
    public static boolean setWindowLayoutInNotch(Window window) {
        if (isNotchOfficialSupport()) {
            return setWindowLayoutInNotchInOffcial(window);
        } else {
            if (DeviceHelper.isHuawei()) {
                return setWindowLayoutInNotchInHuawei(window);
            } else if (DeviceHelper.isVivo()) {
                return setWindowLayoutInNotchInVivo(window);
            } else if (DeviceHelper.isOppo()) {
                return setWindowLayoutInNotchInOppo(window);
            } else if (DeviceHelper.isXiaomi()) {
                return setWindowLayoutInNotchInXiaomi(window);
            }
        }
        return false;
    }

    /**
     * 设置应用窗口在刘海屏手机不使用刘海区
     */
    public static void setWindowLayoutOutNotch(Window window) {
        if (isNotchOfficialSupport()) {
            setWindowLayoutOutNotchInOffcial(window);
        } else {
            if (DeviceHelper.isHuawei()) {
                setWindowLayoutOutNotchInHuawei(window);
            } else if (DeviceHelper.isVivo()) {
                setWindowLayoutOutNotchInVivo(window);
            } else if (DeviceHelper.isOppo()) {
                setWindowLayoutOutNotchInOppo(window);
            } else if (DeviceHelper.isXiaomi()) {
                setWindowLayoutOutNotchInXiaomi(window);
            }
        }
    }

    public static boolean setWindowLayoutInNotchInHuawei(Window window) {
        if (window == null) {
            return false;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("addHwFlags", int.class);
            method.invoke(layoutParamsExObj, 0x00010000);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static boolean setWindowLayoutInNotchInOffcial(Window window) {
        if (window == null) {
            return false;
        }
        // 添加全屏flag
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        window.setAttributes(layoutParams);
        return true;
    }

    public static boolean setWindowLayoutInNotchInXiaomi(Window window) {
        int flag = 0x00000100 | 0x00000200 | 0x00000400;
        try {
            Method method = Window.class.getMethod("addExtraFlags",
                    int.class);
            method.invoke(window, flag);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean setWindowLayoutInNotchInVivo(Window window) {
        return false;
    }

    public static boolean setWindowLayoutInNotchInOppo(Window window) {
        return false;
    }

    public static void setWindowLayoutOutNotchInHuawei(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("clearHwFlags", int.class);
            method.invoke(layoutParamsExObj, 0x00010000);
        } catch (Exception ignore) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void setWindowLayoutOutNotchInOffcial(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        window.setAttributes(layoutParams);
    }

    public static void setWindowLayoutOutNotchInXiaomi(Window window) {
        int flag = 0x00000100 | 0x00000200 | 0x00000400;
        try {
            Method method = Window.class.getMethod("clearExtraFlags",
                    int.class);
            method.invoke(window, flag);
        } catch (Exception e) {
        }
    }

    public static void setWindowLayoutOutNotchInVivo(Window window) {
    }

    public static void setWindowLayoutOutNotchInOppo(Window window) {
    }

    // --- 状态栏 ---
    public static final int DEFAULT_STATUS_BAR_ALPHA = 0;
    private static final int FAKE_STATUS_BAR_VIEW_ID = R.id.statusbarutil_fake_status_bar_view;

    public static void setColor(Activity activity, @ColorInt int color) {
        setColor(activity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setColor(Activity activity, @ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(calculateStatusColor(color, statusBarAlpha));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
            if (fakeStatusBarView != null) {
                if (fakeStatusBarView.getVisibility() == View.GONE) {
                    fakeStatusBarView.setVisibility(View.VISIBLE);
                }
                fakeStatusBarView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
            } else {
                decorView.addView(createStatusBarView(activity, color, statusBarAlpha));
            }
            setRootView(activity);
        }
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    private static int calculateStatusColor(@ColorInt int color, int alpha) {
        if (alpha == 0) {
            return color;
        }
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * 生成一个和状态栏大小相同的半透明矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @param alpha    透明值
     * @return 状态栏矩形条
     */
    private static View createStatusBarView(Activity activity, @ColorInt int color, int alpha) {
        // 绘制一个和状态栏一样高的矩形
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha));
        statusBarView.setId(FAKE_STATUS_BAR_VIEW_ID);
        return statusBarView;
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 设置根布局参数
     */
    private static void setRootView(Activity activity) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    public interface OnNotchListener {
        void onNotch(boolean isNotch);
    }
}
