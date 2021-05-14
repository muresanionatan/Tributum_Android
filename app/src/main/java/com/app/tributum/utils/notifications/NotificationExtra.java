package com.app.tributum.utils.notifications;

import android.content.Intent;

import androidx.annotation.StringDef;

/**
 * Interface that represents the {@link Intent} extras
 * Created by Ionatan Muresan on 5/9/18.
 */
@StringDef
public @interface NotificationExtra {
    String OPEN = "openExtra";
}