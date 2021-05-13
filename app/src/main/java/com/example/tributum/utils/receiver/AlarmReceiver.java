package com.example.tributum.utils.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import androidx.core.app.NotificationCompat;

import com.example.tributum.R;
import com.example.tributum.activity.MainActivity;
import com.example.tributum.utils.AppState;
import com.example.tributum.utils.UtilsGeneral;
import com.example.tributum.utils.notifications.NotificationAction;
import com.example.tributum.utils.notifications.NotificationChannelIds;
import com.example.tributum.utils.notifications.NotificationExtra;
import com.example.tributum.utils.notifications.NotificationIds;
import com.example.tributum.utils.notifications.NotificationIntentIds;
import com.example.tributum.utils.notifications.SystemNotification;
import com.example.tributum.utils.notifications.SystemNotificationContent;
import com.example.tributum.utils.notifications.SystemNotificationFactory;
import com.example.tributum.utils.notifications.SystemNotificationProperties;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppState.isForeground()) {
            return;
        }
        Resources resources = context.getResources();
        Intent openVatsIntent = new Intent(context, MainActivity.class);
        openVatsIntent.setAction(NotificationAction.OPEN_ACTION);
        openVatsIntent.putExtra(NotificationExtra.OPEN, NotificationIntentIds.VAT_INTENT);

        PendingIntent openVatScreen = PendingIntent.getActivity(
                context,
                0,
                openVatsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        SystemNotificationContent systemNotificationContent = new SystemNotificationContent(
                resources.getString(R.string.app_name),
                resources.getString(R.string.vat_notification_string),
                R.drawable.camera_svg);
        SystemNotificationProperties systemNotificationProperties = new SystemNotificationProperties(
                false,
                true,
                NotificationChannelIds.INPUT_VAT_CHANNEL,
                NotificationCompat.PRIORITY_HIGH);
        SystemNotification systemNotification = SystemNotificationFactory.createSystemNotification(systemNotificationContent, systemNotificationProperties);
        if (UtilsGeneral.isBuildVersionAtLeastOreo())
            systemNotification.addNotificationChannel(NotificationChannelIds.INPUT_VAT_CHANNEL,
                    "chanel name",
                    "chanel description",
                    NotificationManager.IMPORTANCE_HIGH);
        systemNotification.setContentIntent(openVatScreen);
        systemNotification.addAction(0, resources.getString(R.string.vats_action), openVatScreen);
        systemNotification.showNotification(NotificationIds.INPUT_VAT_ID);
    }
}