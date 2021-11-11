package com.app.tributum.utils;

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
}