package com.app.tributum.activity.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

import com.app.tributum.R;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.notifications.NotificationExtra;
import com.app.tributum.utils.notifications.NotificationIds;
import com.app.tributum.utils.notifications.NotificationIntentIds;
import com.app.tributum.utils.receiver.AlarmReceiver;

import java.util.Calendar;

public class MainPresenterImpl implements MainPresenter {

    private final MainView view;

    private boolean languageLayoutVisible;

    private String appLanguage;

    private boolean isArrowVisible;

    public MainPresenterImpl(MainView view) {
        this.view = view;
    }

    @Override
    public void onCreate() {
        if (view == null)
            return;

        if (TributumAppHelper.getBooleanSetting(AppKeysValues.USER_DENIED_TERMS) || !TributumAppHelper.getBooleanSetting(AppKeysValues.USER_ACCEPTED_TERMS))
            view.showTermsAndConditionsScreen();

        appLanguage = TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE);
        startNotificationAlarm();

        if (TributumAppHelper.getBooleanSetting(AppKeysValues.FIRST_TIME_USER)) {
            TributumAppHelper.saveSetting(AppKeysValues.FIRST_TIME_USER, AppKeysValues.FALSE);
            view.showTermsAndConditionsScreen();
            view.setWelcomeMessage(R.string.welcome_tributum_label);
        } else {
            view.setWelcomeMessage(R.string.welcome_back_label);
        }
        setLanguageLabel();
    }

    private void setLanguageLabel() {
        if (TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE).equals("en"))
            view.setLanguageLabel(R.string.english_label);
        else
            view.setLanguageLabel(R.string.romanian_label);
    }

    public void startNotificationAlarm() {
        if (!TributumAppHelper.getBooleanSetting(AppKeysValues.NOTIFICATION_ALARM_SET)) {
            TributumAppHelper.saveSetting(AppKeysValues.NOTIFICATION_ALARM_SET, AppKeysValues.TRUE);

            Intent alarmIntent = new Intent(TributumApplication.getInstance(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(TributumApplication.getInstance(), 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) TributumApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
            long interval = ConstantsUtils.NOTIFICATION_INTERVAL;

            Calendar calendar = Calendar.getInstance();
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    interval, pendingIntent);
        }
    }

    @Override
    public void onContractClick() {
        if (view != null)
            view.startContractActivity();

    }

    @Override
    public void onPaymentsClick() {
        if (view != null)
            view.startPaymentsActivity();
    }

    @Override
    public void onVatClick() {
        if (view != null)
            view.startVatActivity();
    }

    @Override
    public void onFaqClick() {
        if (view != null)
            view.startFaqActivity();
    }

    @Override
    public void onLanguageClick() {
        if (view == null)
            return;

        languageLayoutVisible = true;
        view.showLanguageLayout();
        if (TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE).equals("en")) {
            view.checkEnglishBox();
            view.unCheckRomanianBox();
        } else {
            view.checkRomanianBox();
            view.unCheckEnglishBox();
        }
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
                && intent.getStringExtra(NotificationExtra.OPEN).equals(NotificationIntentIds.VAT_INTENT)
                && view != null) {
            NotificationManagerCompat.from(TributumApplication.getInstance()).cancel(null, NotificationIds.INPUT_VAT_ID);
            view.startVatActivity();
        }
    }

    @Override
    public void onAcceptTermsClicked() {
        TributumAppHelper.saveSetting(AppKeysValues.USER_ACCEPTED_TERMS, AppKeysValues.TRUE);
        TributumAppHelper.saveSetting(AppKeysValues.USER_DENIED_TERMS, AppKeysValues.FALSE);
        if (view != null)
            view.hideTermsAndConditionsScreen();
    }

    @Override
    public void oNDenyTermsClicked() {
        if (view != null)
            view.showPopup();
    }

    @Override
    public void scrollViewScrolledToBottom() {
        if (view != null && isArrowVisible) {
            isArrowVisible = false;
            view.hideScrollToBottomButton();
        }
    }

    @Override
    public void scrollViewNotAtBottom() {
        if (view != null && !isArrowVisible) {
            isArrowVisible = true;
            view.showScrollToBottomButton();
        }
    }

    @Override
    public void onUserDenyClicked() {
        TributumAppHelper.saveSetting(AppKeysValues.USER_DENIED_TERMS, true);
        if (view != null)
            view.closeApp();
    }

    @Override
    public void onArrowClicked() {
        if (view != null) {
            view.scrollViewToBottom();
        }
    }

    @Override
    public void onBackPressed() {
        if (languageLayoutVisible) {
            onLanguageDoneClick();
        } else if (view != null) {
            view.closeApp();
        }
    }
}