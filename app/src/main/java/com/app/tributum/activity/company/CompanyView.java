package com.app.tributum.activity.company;

import com.app.tributum.activity.company.model.Company;
import com.app.tributum.activity.company.model.Director;
import com.app.tributum.activity.company.model.Secretary;

public interface CompanyView {

    void showToast(int stringResource);

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

    Company getCompanyDetails();

    Director getDirector1Details();
    Director getDirector2Details();
    Director getDirector3Details();
    Secretary getSecretaryDetails();

    void setBirthdayText(String string1, int id);

    void moveBirthdayCursorToEnd(int id);

    void checkTheAgreeBox(boolean acceptTerms);
}