package com.app.tributum.utils;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtils {

    private CalendarUtils() {
    }

    public static String getCurrentMonth() {
        return (String) android.text.format.DateFormat.format("MMMM", new Date());
    }

    public static long getCurrentDateInMilies() {
        return Calendar.getInstance().getTimeInMillis();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getCurrentDay() {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        return dateFormat.format(date);
    }
}