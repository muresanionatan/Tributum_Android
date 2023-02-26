package com.app.tributum.utils;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.app.tributum.R;
import com.app.tributum.application.TributumApplication;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public static String formatDate(String date) {
        String formattedDate;
        String year = date.substring(0, 4);
        date = date.substring(5);
        int month = Integer.parseInt(date.substring(0, date.indexOf('/'))) + 1;
        String day = date.substring(date.indexOf('/') + 1);
        formattedDate = day + "/" + month + "/" + year;

        return formattedDate;
    }

    public static String getDatesToBeDisplayed(ArrayList<CalendarDay> datesSelected) {
        ArrayList<String> newDates = new ArrayList<>();
        for (int i = 0; i < datesSelected.size(); i++) {
            String toBeDisplayed;
            toBeDisplayed =
                    datesSelected.get(i).toString().replace("CalendarDay", "").
                            replace("{", "").
                            replace("}", "").
                            replace("-", "/");
            newDates.add(CalendarUtils.formatDate(toBeDisplayed));
        }
        if (newDates.size() == 0) {
            return TributumApplication.getInstance().getString(R.string.date_you_choose);
        } else {
            String concatenatedDates = "";
            for (int i = 0; i < newDates.size(); i++) {
                if (i == 1) {
                    concatenatedDates = concatenatedDates + ", " + newDates.get(i);
                    break;
                }
                concatenatedDates = newDates.get(i);
            }
            return concatenatedDates;
        }
    }
}