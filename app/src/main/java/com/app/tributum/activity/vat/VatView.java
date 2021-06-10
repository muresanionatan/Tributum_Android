package com.app.tributum.activity.vat;

import android.net.Uri;

import com.app.tributum.fragment.invoices.model.InvoiceModel;

public interface VatView {

    void showToast(String string);

    void showLoadingScreen();

    void hideKeyboard();

    void startPdfCreation();

    void hideLoadingScreen();

    void addItemToList(InvoiceModel model);

    void removeItemFromList(int photoClicked);

    void requestPermissions(String[] toArray, int multiplePermissionsPpsFront);

    void takePhoto(String pictureImagePath);

    void hidePreview();

    void showImagePreview(String filePath);

    void getFilesFromGallery(Uri imageUri);

    int checkPermission(String permission);

    void closeActivity();

    void openBottomSheet();

    void collapseBottomSheet();

    void openPhotoChooserIntent();

    void showRequestSentScreen();
}