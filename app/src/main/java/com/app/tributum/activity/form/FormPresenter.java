package com.app.tributum.activity.form;

public interface FormPresenter {

    void onBackPressed();

    void handleSendButtonClick(String firstName, String lastName, String address1, String address2, String address3, String eircode,
                               String birthday, String occupation, String phone, String email, String bankAccount,
                               String pps, String noOfKids);

    void onCreate();


}