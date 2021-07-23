package com.app.tributum.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.app.tributum.activity.main.MainActivity;
import com.app.tributum.application.TributumApplication;

import java.util.Locale;

/**
 * Class containing general methods
 * Created by Ionatan Muresan on 8/9/16.
 */
public class UtilsGeneral {

    /**
     * private constructor just to make sure this class cannot be instantiated
     */
    private UtilsGeneral() {
    }

    /**
     * sets focus on the given {@link EditText} and shows keyboard
     *
     * @param editText the {@link EditText} that requests focus
     */
    public static void setFocusOnInput(EditText editText) {
        if (editText != null && !editText.hasFocus()) {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setCursorVisible(true);
            showSoftKeyboard(editText);
        }
    }

    /**
     * removes focus from the given {@link EditText} and hides keyboard
     *
     * @param editText the {@link EditText} that the focus needs to be removed from
     */
    public static void removeFocusFromInput(Activity activity, EditText editText) {
        UtilsGeneral.hideSoftKeyboard(activity);
        if (editText != null) {
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.setCursorVisible(false);
        }
    }

    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(Activity activity) {
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), 0);
            }
        }
    }

    /**
     * Shows the soft keyboard
     */
    private static void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) TributumApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            view.requestFocus();
            inputMethodManager.showSoftInput(view, 0);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static void setMaxLengthAndAllCapsToEditText(EditText editText, int maxLength, boolean allCaps) {
        InputFilter[] filterArray = new InputFilter[2];
        filterArray[0] = new InputFilter.LengthFilter(maxLength);
        if (allCaps)
            filterArray[1] = new InputFilter.AllCaps();
        editText.setFilters(filterArray);
    }

    public static void setMaxLengthEditText(EditText editText, int maxLength) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(maxLength);
        editText.setFilters(filterArray);
    }

    public static void setAppLanguage(Activity activity, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        activity.getResources().updateConfiguration(configuration, activity.getResources().getDisplayMetrics());
    }

    public static void restartAppForLanguage(Activity activity) {
        Intent refresh = new Intent(activity, MainActivity.class);
        activity.finish();
        activity.startActivity(refresh);
    }

    /**
     * Changes the locale for the given context
     */
    public static void changeLocaleForContext(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);

        context.createConfigurationContext(configuration);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    /**
     * checks if the build version is bigger then API 26 Oreo
     *
     * @return true if build version is at least API 26
     */
    public static boolean isBuildVersionAtLeastOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}