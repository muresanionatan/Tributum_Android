package com.app.tributum.activity.company;

import android.text.Editable;

public interface CompanyPresenter {

    void onBackPressed();

    void onOkClicked();

    void onDirector2Click();
    void onDirector3Click();

    void onMainButtonClick();

    void afterBirthdayChanged(Editable s, int resourceId);

    void beforeBirthdayChanged(int length, int resourceId);

    void onAgreeTermsClick(boolean checkboxClicked);
}