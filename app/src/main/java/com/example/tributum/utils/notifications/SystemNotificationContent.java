package com.example.tributum.utils.notifications;

import androidx.annotation.DrawableRes;

/**
 * Class that contains notification information like:
 * <ul>
 * <li>{@link #title}</li>
 * <li>{@link #message}</li>
 * <li>{@link #drawableId}</li>
 * </ul>
 */
public class SystemNotificationContent {

    private String title;

    private String message;

    @DrawableRes
    private int drawableId;

    public SystemNotificationContent(String title, String message, @DrawableRes int drawableId) {
        this.title = title;
        this.message = message;
        this.drawableId = drawableId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public int getDrawableId() {
        return drawableId;
    }
}