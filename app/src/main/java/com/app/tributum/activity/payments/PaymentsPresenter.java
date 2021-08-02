package com.app.tributum.activity.payments;

public interface PaymentsPresenter {

    void onDestroy(String payer, String email, String site);

    void onBackPressed();

    void handleSendButtonClick(String payer, String email, String site, String month);

    void onCreate();

    void onPause(String payer, String email, String site);

    void onNetClick();

    void onGrossClick();

    void onTextChanged(String name, String email, String site, String month);
}