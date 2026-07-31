package com.app.tributum.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.app.tributum.application.FintrexApplication;

public class NetworkUtils {

    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) FintrexApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}