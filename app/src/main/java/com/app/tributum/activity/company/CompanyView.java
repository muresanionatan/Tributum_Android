package com.app.tributum.activity.company;

import android.net.Uri;

import com.app.tributum.activity.company.model.Company;
import com.app.tributum.activity.company.model.Director;
import com.app.tributum.activity.company.model.Secretary;

import java.io.File;

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

    void hideBottomSheet();

    void showFileChooser(int selectPictureRequest, int takePictureRequest);

    void openFilePreview(String fileName);

    void setFileChooserToVisible();

    void takePicture(int requestId, File file, String pictureImagePath);

    void closePreview();

    void pickDirector1PpsFront();

    void pickDirector1PpsBack();

    void pickDirector1Id();

    void pickDirector1Pass();

    void pickDirector2PpsFront();

    void pickDirector2PpsBack();

    void pickDirector2Id();

    void pickDirector2Pass();

    void pickDirector3PpsFront();

    void pickDirector3PpsBack();

    void pickDirector3Id();

    void pickDirector3Pass();

    void resetDirector1PpsFrontLayout();

    void resetDirector1PpsBackLayout();

    void resetDirector1IdLayout();

    void resetDirector1PassLayout();

    void resetDirector2PpsFrontLayout();

    void resetDirector2PpsBackLayout();

    void resetDirector2IdLayout();

    void resetDirector2PassLayout();

    void resetDirector3PpsFrontLayout();

    void resetDirector3PpsBackLayout();

    void resetDirector3IdLayout();

    void resetDirector3PassLayout();

    void startCrop(Uri uri);

    void setDirector1PpsFrontImage(String result);

    void setDirector1PpsBackImage(String result);

    void setDirector1IdImage(String result);

    void setDirector1PassImage(String result);

    void setDirector2PpsFrontImage(String result);

    void setDirector2PpsBackImage(String result);

    void setDirector2IdImage(String result);

    void setDirector2PassImage(String result);

    void setDirector3PpsFrontImage(String result);

    void setDirector3PpsBackImage(String result);

    void setDirector3IdImage(String result);

    void setDirector3PassImage(String result);
}