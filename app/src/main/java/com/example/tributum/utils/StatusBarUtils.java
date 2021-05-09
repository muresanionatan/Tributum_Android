package com.example.tributum.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

public class StatusBarUtils {

    /**
     * private constructor to prevent class instantiation
     */
    private StatusBarUtils() { }

    @SuppressLint("ObsoleteSdkInt")
    public static void makeStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            View decor = window.getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(activity, android.R.color.transparent));
        }
    }
}