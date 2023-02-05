package com.app.tributum.activity.pps;

import android.net.Uri;

import java.io.File;

public interface PpsView {

    void showToast(int stringId);

    void showLoadingScreen();

    void hideLoadingScreen();

    void closeActivity();

    void hideBottomSheet();

    void showFileChooser(int selectPictureRequest, int takePictureRequest);

    void openFilePreview(String fileName);

    void setFileChooserToVisible();

    void closePreview();

    void openFilePicker(int requestId);

    void takePicture(int requestId, File file, String pictureImagePath);

    void resetBillLayout();

    void resetLetterLayout();

    void resetIdLayout();

    void setBillImage(String bill);

    void setLetterImage(String letter);

    void setIdImage(String idFile);

    void showRequestSentScreen();

    void removeFocus();

    void focusOnFirstName();

    void focusOnLastName();

    void focusOnMomName();

    void focusOnAddress();

    void focusOnEircode();

    void focusOnEmail();

    void focusOnPhone();

    void setEircodeText(String string);

    void moveEircodeCursorToEnd(int length);

    void startCrop(Uri imageUri);

    void scrollToId();

    void scrollToBill();

    void scrollToLetter();
}