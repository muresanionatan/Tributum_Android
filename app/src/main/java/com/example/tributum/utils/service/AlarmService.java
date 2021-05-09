package com.example.tributum.utils.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.tributum.utils.receiver.AlarmReceiver;

import java.util.Calendar;

public class AlarmService {

    private Context context;

    public AlarmService(Context context) {
        this.context = context;
    }

    public void startAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 9);
        calendar.add(Calendar.MINUTE, 0);
        calendar.add(Calendar.SECOND, 0);
        calendar.add(Calendar.MONTH, 0);
//        calendar.add(Calendar.MONTH, 2);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);

        long firstTime = calendar.getTimeInMillis();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstTime, AlarmManager.INTERVAL_DAY, pendingIntent);

//        //Set the alarm to 10 seconds from now
//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.SECOND, 5);
//        long firstTime = c.getTimeInMillis();
//        // Schedule the alarm!
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);
//        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        am.set(AlarmManager.RTC_WAKEUP, firstTime, pendingIntent);
    }
}