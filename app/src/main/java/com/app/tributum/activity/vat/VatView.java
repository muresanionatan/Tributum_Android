package com.app.tributum.activity.vat;

import android.net.Uri;

import com.app.tributum.activity.vat.model.VatModel;

public interface VatView {

    void showToast(String string);

    void showLoadingScreen();

    void hideKeyboard();

    void startPdfCreation();

    void hideLoadingScreen();

    void addItemToList(VatModel model);

    void removeItemFromList(int photoClicked);

    void takePhoto(String pictureImagePath);

    void hidePreview();

    void showImagePreview(String filePath);

    void getFilesFromGallery(Uri imageUri);

    void closeActivity();

    void openBottomSheet();

    void collapseBottomSheet();

    void openPhotoChooserIntent();

    void showRequestSentScreen();

    void showTopViewBottomSheet();

    void setFocusOnName();

    void setFocusOnEmail();

    void setFocusOnStartingMonth();

    void setFocusOnEndingMonth();
}