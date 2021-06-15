package com.app.tributum.activity.main;

import android.content.Intent;

public interface MainPresenter {
    void onCreate();

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

    void onAcceptTermsClicked();

    void oNDenyTermsClicked();

    void scrollViewScrolledToBottom();

    void scrollViewNotAtBottom();

    void onArrowClicked();

    void onUserDenyClicked();
}