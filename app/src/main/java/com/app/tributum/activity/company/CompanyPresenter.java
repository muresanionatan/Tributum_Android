package com.app.tributum.activity.company;

public interface CompanyPresenter {

    void onSendClick(String name, String email, String description);

    void onBackPressed();

    void onOkClicked();

    void onDirector2Click();
    void onDirector3Click();
}