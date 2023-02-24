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

    public static String[] getCurrentWeek() {
        DateFormat format = new SimpleDateFormat("dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        String[] days = new String[7];
        for (int i = 0; i < 7; i++) {
            days[i] = format.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return days;
    }

    public static int getCurrentDayInt() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static String getCurrentMonthAndYear() {
        Calendar calendar = Calendar.getInstance();
        return "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
    }
}