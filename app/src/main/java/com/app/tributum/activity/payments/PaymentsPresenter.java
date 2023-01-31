package com.app.tributum.activity.payments;

public interface PaymentsPresenter {

    void onDestroy(String payer, String email);

    void onBackPressed();

    void handleSendButtonClick(String payer, String email, String month);

    void onCreate();

    void onPause(String payer, String email);

    void onNetClick();

    void onGrossClick();
}