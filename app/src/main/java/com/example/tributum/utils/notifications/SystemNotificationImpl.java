package com.example.tributum.utils.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.tributum.application.TributumApplication;
import com.example.tributum.utils.UtilsGeneral;

/**
 * Implementation of {@link SystemNotification}
 */
public class SystemNotificationImpl implements SystemNotification {

    private NotificationCompat.Builder builder;

    SystemNotificationImpl(NotificationCompat.Builder builder) {
        this.builder = builder;
    }

    @Override
    public void setContentIntent(PendingIntent pendingIntent) {
        builder.setContentIntent(pendingIntent);
    }

    @Override
    public void showNotification(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) TributumApplication.getInstance().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.notify(notificationId, getNotification());
    }

    @Override
    public void hideNotification(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) TributumApplication.getInstance().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.cancel(notificationId);
        builder = null;
    }

    @Override
    public void addNotificationChannel(String channelId, String name, String description, int importance) {
        if (UtilsGeneral.isBuildVersionAtLeastOreo()) {
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setSound(null, null);
            NotificationManager notificationManager = TributumApplication.getInstance().getApplicationContext().getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void addAction(int drawableId, String title, PendingIntent pendingIntent) {
        builder.addAction(drawableId, title, pendingIntent);
    }

    @Override
    public Notification getNotification() {
        return builder.build();
    }
}