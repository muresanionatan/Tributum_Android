package com.app.tributum.activity.contract;

import android.text.Editable;

public interface ContractPresenter {

    void onBackPressed();

    void handleSendButtonClick(String firstName, String lastName, String address1, String address2, String address3, String eircode,
                               String birthday, String occupation, String phone, String email, String bankAccount,
                               String pps, String startingDate, String noOfKids);

    void onStartingDateSet(int year, int monthOfYear, int dayOfMonth);

    void beforeStartingDateChanged(int length);

    void afterStartingDateChanged(Editable string);

    void onClearSignatureClick();

    void setVatCheckbox();

    void setRctCheckbox();
}