package com.app.tributum.activity.payments;

import com.app.tributum.activity.payments.model.PaymentModel;

import java.util.List;

public interface PaymentsPresenter {

    void onDestroy(String payer, String email, String site);

    void onBackPressed();

    void handleSendButtonClick(String payer, String email, String site, String month);

    List<PaymentModel> getPaymentsList();

    void onCreate();

    void onPause(String payer, String email, String site);

    void onNetClick();

    void onGrossClick();

    void onAddNewPaymentClick();

    void onTextChanged(String name, String email, String site, String month);
}