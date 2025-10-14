package com.app.tributum.activity.form;

import android.net.Uri;

import java.io.File;

public interface FormView {

    void showToast(int stringId);

    void showLoadingScreen();

    void hideLoadingScreen();

    void closeActivity();

    void hideMaritalLayout();

    void showMaritalLayout();

    void hideBottomSheet();

    void showFileChooser(int selectPictureRequest, int takePictureRequest);

    void openFilePreview(String fileName);

    void setFileChooserToVisible();

    void closePreview();

    void showClearButton();

    void hideClearButton();

    void takePicture(int requestId, File file, String pictureImagePath);

    void resetPpsFrontLayout();

    void resetPpsBackLayout();

    void resetIdLayout();

    void resetMarriageCertificateLayout();

    void setPpsFrontImage(String ppsFileFront);

    void setPpsBackImage(String ppsFileBack);

    void setIdImage(String idFile);

    void setMarriageCertificateImage(String marriageCertificateFile);

    void showRequestSentScreen();

    void removeFocus();

    void startCrop(Uri imageUri);
}