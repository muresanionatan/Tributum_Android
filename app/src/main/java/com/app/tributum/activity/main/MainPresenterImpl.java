package com.app.tributum.activity.main;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainPresenterImpl implements MainPresenter {

    private final MainView view;

    private boolean languageLayoutVisible;

    private String appLanguage;

    private boolean isArrowVisible;

    private int actToStart = -1;

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

            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
                pendingIntent = PendingIntent.getBroadcast(TributumApplication.getInstance(), 0, alarmIntent, PendingIntent.FLAG_MUTABLE);
            else
                pendingIntent = PendingIntent.getBroadcast(TributumApplication.getInstance(), 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) TributumApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
            long interval = ConstantsUtils.NOTIFICATION_INTERVAL;

            Calendar calendar = Calendar.getInstance();
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    interval, pendingIntent);
        }
    }

    @Override
    public void onContractClick() {
        actToStart = ActivityToStart.CONTRACT;
        handleActivityStart();
    }

    @Override
    public void onPaymentsClick() {
        if (view != null)
            view.startPaymentsActivity();
    }

    @Override
    public void onVatClick() {
        actToStart = ActivityToStart.VAT;
        handleActivityStart();
    }

    @Override
    public void onInquiryClick() {
        actToStart = ActivityToStart.INQUIRY;
        handleActivityStart();
    }

    @Override
    public void onPpsClick() {
        actToStart = ActivityToStart.PPS;
        handleActivityStart();
    }

    @Override
    public void onSalaryClick() {
        if (view != null)
            view.startSalaryActivity();
    }

    private void handleActivityStart() {
        if (view == null)
            return;

//        if (view.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//            if (TributumAppHelper.getBooleanSetting(AppKeysValues.STORAGE_FIRST_DENIED) && view.shouldShowStorageRationale()) {
//                view.takeUserToAppSettings();
//                return;
//            }
//        }

        if (checkPermissions()) {
            if (actToStart == ActivityToStart.CONTRACT)
                view.startContractActivity();
            else if (actToStart == ActivityToStart.VAT)
                view.startVatActivity();
            else if (actToStart == ActivityToStart.INQUIRY)
                view.startInquiryActivity();
            else if (actToStart == ActivityToStart.PPS)
                view.startPpsActivity();
        } else {
            if (view.shouldShowCameraRationale())
                view.requestPermissions(new String[]{Manifest.permission.CAMERA}, ConstantsUtils.CAMERA_REQUEST_ID);
            else if (TributumAppHelper.getBooleanSetting(AppKeysValues.CAMERA_FIRST_DENIED)) {
                view.takeUserToAppSettings();
            } else {
                view.requestPermissions(new String[]{Manifest.permission.CAMERA}, ConstantsUtils.CAMERA_REQUEST_ID);
            }
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : ConstantsUtils.PERMISSIONS) {
            result = view.checkPermission(permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            view.requestPermissions(listPermissionsNeeded.toArray(new String[0]), ConstantsUtils.MULTIPLE_PERMISSIONS_PPS_FRONT);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        if (requestCode == ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_VAT) {
                            TributumAppHelper.saveSetting(AppKeysValues.STORAGE_FIRST_DENIED, AppKeysValues.TRUE);
                        } else {
                            TributumAppHelper.saveSetting(AppKeysValues.CAMERA_FIRST_DENIED, AppKeysValues.TRUE);
                        }
                    }
                    return;
                }
            }
            if (actToStart == ActivityToStart.CONTRACT)
                view.startContractActivity();
            else if (actToStart == ActivityToStart.VAT)
                view.startVatActivity();
            else
                view.startInquiryActivity();
        }

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
            actToStart = ActivityToStart.VAT;
            handleActivityStart();
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