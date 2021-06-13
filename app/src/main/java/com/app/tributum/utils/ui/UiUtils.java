package com.app.tributum.utils.ui;

import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.app.tributum.application.TributumApplication;

public class UiUtils {

    private UiUtils() {
    }

    public static void setFontFamily(int fontFamily, TextView textView) {
        Typeface typeface = ResourcesCompat.getFont(TributumApplication.getInstance(), fontFamily);
        textView.setTypeface(typeface);
    }

    public static float getScreenWidth() {
        DisplayMetrics displayMetrics = TributumApplication.getInstance().getResources().getDisplayMetrics();
        return displayMetrics.widthPixels / displayMetrics.density * 3;
    }
}
