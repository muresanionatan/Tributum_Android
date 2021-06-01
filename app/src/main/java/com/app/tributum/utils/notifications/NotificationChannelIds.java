package com.app.tributum.utils.notifications;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Interface that contains the ids for the notification channels used in the app
 */
@StringDef({NotificationChannelIds.INPUT_VAT_CHANNEL})
@Retention(RetentionPolicy.SOURCE)
public @interface NotificationChannelIds {
    String INPUT_VAT_CHANNEL = "inputVatChannel";
}