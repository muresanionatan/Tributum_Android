package com.app.tributum.utils.notifications;

/**
 * Class that contains notification properties like:
 * <ul>
 * <li>{@link #ongoing}</li>
 * <li>{@link #autoCancel}</li>
 * <li>{@link #channelId}</li>
 * <li>{@link #priority}</li>
 * </ul>
 */
public class SystemNotificationProperties {

    /**
     * {@code true} if the notification should not be swipable, {@code false} otherwise
     */
    private boolean ongoing;

    /**
     * {@code true} if the notification will be dismissed when the user touches it, {@code false} otherwise
     */
    private boolean autoCancel;

    /**
     * <b>ONLY for Android O</b>, the channel in which the notification will be added
     */
    @NotificationChannelIds
    private String channelId;

    /**
     * <b>ONLY under Android O</b> depending on this, the notification will pop-up or not.
     * <br>
     * If the priority is bigger then {@link androidx.core.app.NotificationCompat#PRIORITY_DEFAULT} then the notification will pop,
     * otherwise it will only be visible in the status bar.
     * </br>
     */
    private int priority;

    public SystemNotificationProperties(boolean ongoing, boolean autoCancel, String channelId, int priority) {
        this.ongoing = ongoing;
        this.autoCancel = autoCancel;
        this.channelId = channelId;
        this.priority = priority;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public boolean isAutoCancel() {
        return autoCancel;
    }

    public String getChannelId() {
        return channelId;
    }

    public int getPriority() {
        return priority;
    }
}