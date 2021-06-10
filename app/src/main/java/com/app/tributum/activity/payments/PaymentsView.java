package com.app.tributum.activity.payments;

public interface PaymentsView {

    void showToast(String string);

    void showLoadingScreen();

    void hideKeyboard();

    void hideLoadingScreen();

    boolean areThereEmptyInputs();

    void addModel(int itemNotified, int itemInserted);

    void removeModel();

    void setNetViews();

    void setGrossViews();

    void populateInputsWithValues(String payer, String email, String site, String currentMonth);

    void closeActivity();

    void showRequestSentScreen();
}