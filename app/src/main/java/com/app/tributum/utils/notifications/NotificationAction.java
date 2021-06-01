package com.app.tributum.utils.notifications;

import androidx.annotation.StringDef;

/**
 * Interface that represents the actions that are being displayed in the push notification
 */
@StringDef
public @interface NotificationAction {
    String OPEN_ACTION = "openAction";
}