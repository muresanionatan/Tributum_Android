package com.app.tributum.activity.payments;

import com.app.tributum.activity.payments.model.PaymentModel;

import java.util.List;

public interface PaymentsView {

    void showToast(String string);

    void showLoadingScreen();

    void hideKeyboard();

    void hideLoadingScreen();

    boolean areThereEmptyInputs();

    void setNetViews();

    void setGrossViews();

    void populateInputsWithValues(String payer, String email, String site, String currentMonth);

    void closeActivity();

    void showRequestSentScreen();

    void disableSend();

    void enableSend();

    void setFocusOnName();

    void setFocusOnEmail();

    void setFocusOnSite();

    void setFocusOnMonth();

    void setFocusOnRecyclerView();

    List<PaymentModel> getPaymentList();
}