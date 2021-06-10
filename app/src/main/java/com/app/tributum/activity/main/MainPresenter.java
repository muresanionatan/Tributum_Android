package com.app.tributum.activity.main;

import android.content.Context;
import android.content.Intent;

public interface MainPresenter {
    void onCreate(Context context);

    void onSplashFinished(Intent intent);

    void onBackPressed();

    void onLanguageClick();

    void onLanguageDoneClick();

    void onEnglishClick();

    void onRomanianClick();

    void onVatClick();

    void onPaymentsClick();

    void onContractClick();

    void onFaqClick();
}