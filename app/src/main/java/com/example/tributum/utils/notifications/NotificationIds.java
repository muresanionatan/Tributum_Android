package com.example.tributum.utils.notifications;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Interface that contains the ids for the notifications that will be displayed in the app
 */
@IntDef({NotificationIds.INPUT_VAT_ID})
@Retention(RetentionPolicy.SOURCE)
public @interface NotificationIds {
    int INPUT_VAT_ID = 10000;
}