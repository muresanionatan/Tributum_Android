package com.example.tributum.utils.notifications;

import android.app.Notification;
import android.app.PendingIntent;

/**
 * Interface that communicates between {@link SystemNotificationImpl} and interested parties
 */
public interface SystemNotification {

    /**
     * Sets the pending intent which will be send when the user presses on the notification body
     * @param pendingIntent intent send
     */
    void setContentIntent(PendingIntent pendingIntent);

    /**
     * Shows the notification corresponding to the given id
     * @param notificationId given notification id
     */
    void showNotification(int notificationId);

    /**
     * Hides the notification corresponding to the given id
     * @param notificationId given notification id
     */
    void hideNotification(int notificationId);

    /**
     * <b>ONLY for Android O</b>
     * <br>
     * Adds a notification channel which will contain the notification correlated to this channel
     * </br>
     * @param channelId the channel id
     * @param name the name of the channel displayed in App system settings
     * @param description the description of the channel
     * @param importance the importance of the channel
     */
    void addNotificationChannel(String channelId, String name, String description, int importance);

    /**
     * Adds the action displayed on the notification
     * @param drawableId the drawable displayed near the action title
     * @param title the action title
     * @param pendingIntent the intent that will be sent when action selected
     */
    void addAction(int drawableId, String title, PendingIntent pendingIntent);

    Notification getNotification();
}