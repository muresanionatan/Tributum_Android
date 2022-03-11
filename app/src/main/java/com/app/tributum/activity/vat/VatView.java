package com.app.tributum.activity.vat;

import android.net.Uri;

import com.app.tributum.activity.vat.model.VatModel;

import java.util.List;

public interface VatView {

    void showToast(String string);

    void showLoadingScreen();

    void hideKeyboard();

    void startPdfCreation(List<VatModel> invoices, List<VatModel> privates);

    void hideLoadingScreen();

    void addItemToInvoicesList(VatModel model);

    void removeItemFromInvoicesList(int photoClicked);

    void removeItemFromPrivatesList(int photoClicked);

    void takePhoto(String pictureImagePath, int state);

    void hidePreview();

    void showImagePreview(String filePath);

    void getFilesFromGalleryForInvoices(Uri imageUri);

    void closeActivity();

    void openBottomSheet();

    void collapseBottomSheet();

    void openPhotoChooserIntent(int requestId);

    void showRequestSentScreen();

    void showTopViewBottomSheet();

    void setFocusOnName();

    void setFocusOnEmail();

    void setFocusOnStartingMonth();

    void setFocusOnEndingMonth();

    void setPrivatesStates(boolean state);

    void setPrivatesFont(int font);

    void setRecyclerViewVisibility(int visibility);

    void getFilesFromGalleryForPrivates(Uri imageUri);

    void addItemToPrivatesList(VatModel vatModel);
}