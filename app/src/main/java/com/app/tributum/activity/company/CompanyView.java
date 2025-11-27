package com.app.tributum.activity.company;

public interface CompanyView {

    void showToast(String message);

    void closeActivity();

    void hideKeyboard();

    void showLoadingScreen();

    void hideLoadingScreen();

    void showRequestSent();

    void hideSecondDirector();

    void showSecondDirector();

    void hideThirdDirector();

    void showThirdDirector();

    void hideCompanyView();

    void showCompanyView();

    void hideDirectorViewToLeft();
    void hideDirectorViewToRight();

    void showDirectorViewFromRight();
    void showDirectorViewFromLeft();

    void hideSecretaryView();

    void showSecretaryView();

    void setConfirmationButtonText(int continueLabel);
}