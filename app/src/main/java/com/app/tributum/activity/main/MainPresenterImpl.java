package com.app.tributum.activity.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.app.tributum.R;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.notifications.NotificationExtra;
import com.app.tributum.utils.notifications.NotificationIntentIds;
import com.app.tributum.utils.receiver.AlarmReceiver;

import java.util.Calendar;

public class MainPresenterImpl implements MainPresenter {

    private MainView view;

    private boolean languageLayoutVisible;

    private String appLanguage;

    public MainPresenterImpl(MainView view) {
        this.view = view;
    }

    @Override
    public void onCreate(Context context) {
        if (view == null)
            return;

        appLanguage = TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE);
        startNotificationAlarm(context);

        if (TributumAppHelper.getBooleanSetting(AppKeysValues.FIRST_TIME_USER)) {
            TributumAppHelper.saveSetting(AppKeysValues.FIRST_TIME_USER, AppKeysValues.FALSE);
            view.setWelcomeMessage(context.getString(R.string.welcome_tributum_label));
        } else {
            view.setWelcomeMessage(context.getString(R.string.welcome_back_label));
        }
    }

    public void startNotificationAlarm(Context context) {
        if (!TributumAppHelper.getBooleanSetting(AppKeysValues.NOTIFICATION_ALARM_SET)) {
            TributumAppHelper.saveSetting(AppKeysValues.NOTIFICATION_ALARM_SET, AppKeysValues.TRUE);

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long interval = ConstantsUtils.NOTIFICATION_INTERVAL;

            Calendar calendar = Calendar.getInstance();
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    interval, pendingIntent);
        }
    }

    @Override
    public void onContractClick() {
        if (view == null)
            return;

        view.startContractActivity();

    }

    @Override
    public void onPaymentsClick() {
        if (view == null)
            return;

        view.startPaymentsActivity();
    }

    @Override
    public void onVatClick() {
        if (view == null)
            return;

        view.startVatActivity();
    }

    @Override
    public void onLanguageClick() {
        if (view == null)
            return;

        languageLayoutVisible = true;
        view.showLanguageLayout();
        if (TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE).equals("en"))
            view.checkEnglishBox();
        else
            view.checkRomanianBox();
    }

    @Override
    public void onEnglishClick() {
        if (view == null)
            return;

        appLanguage = "en";
        view.checkEnglishBox();
        view.unCheckRomanianBox();
    }

    @Override
    public void onRomanianClick() {
        if (view == null)
            return;

        appLanguage = "ro";
        view.checkRomanianBox();
        view.unCheckEnglishBox();
    }

    @Override
    public void onLanguageDoneClick() {
        if (view == null)
            return;

        languageLayoutVisible = false;
        if (appLanguage.equals(TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE))) {
            view.hideLanguageLayout();
        } else {
            TributumAppHelper.saveSetting(AppKeysValues.APP_LANGUAGE, appLanguage);
            view.restartApp();
        }
    }

    @Override
    public void onSplashFinished(Intent intent) {
        if (intent != null && intent.getStringExtra(NotificationExtra.OPEN) != null
                && intent.getStringExtra(NotificationExtra.OPEN).equals(NotificationIntentIds.VAT_INTENT)) {
            //TODO: start VAT screen
        }
    }

    @Override
    public void onBackPressed() {
        if (languageLayoutVisible) {
            onLanguageDoneClick();
        }
    }
}