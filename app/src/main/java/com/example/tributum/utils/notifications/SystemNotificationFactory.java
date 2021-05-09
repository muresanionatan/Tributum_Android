package com.example.tributum.utils.notifications;

import android.app.Notification;

import androidx.core.app.NotificationCompat;

import com.example.tributum.application.TributumApplication;
import com.example.tributum.utils.UtilsGeneral;

/**
 * Class used to create {@link SystemNotification}
 */
public class SystemNotificationFactory {

    private SystemNotificationFactory() {
        //private constructor to prevent instantiation
    }

    public static SystemNotification createSystemNotification(SystemNotificationContent content, SystemNotificationProperties properties) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(TributumApplication.getInstance().getApplicationContext(), properties.getChannelId());

        String title = content.getTitle();
        String message = content.getMessage();
        String channelId = properties.getChannelId();
        int priority = properties.getPriority();

        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(content.getDrawableId());
        builder.setAutoCancel(properties.isAutoCancel());
        builder.setOngoing(properties.isOngoing());
        if (!UtilsGeneral.isBuildVersionAtLeastOreo()) {
            builder.setPriority(priority);
            if (priority > NotificationCompat.PRIORITY_DEFAULT)
                builder.setDefaults(Notification.DEFAULT_ALL);
        } else {
            builder.setChannelId(channelId);
        }
        return new SystemNotificationImpl(builder);
    }
}