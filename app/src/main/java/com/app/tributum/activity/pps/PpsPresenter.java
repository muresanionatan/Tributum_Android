package com.app.tributum.activity.pps;

import android.content.Intent;
import android.text.Editable;

public interface PpsPresenter {

    void onBackPressed();

    void handleSendButtonClick(String firstName, String lastName, String momName, String address, String eircode,
                               String email, String phone, String ownerPhone);

    void onFileChooserTopClicked();

    void onBillClicked();

    void onLetterClicked();

    void onIdClicked();

    void onBottomSheetExpanded();

    void onAddFromGalleryClicked(int requestCode);

    void onTakePhotoClicked(String name, int requestId);

    void onRemovePhotoClicked(String fileName);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onIdPreviewClicked();

    void onIdRemoveClicked();

    void onBillPreviewClicked();

    void onBillRemoveClicked();

    void onLetterPreviewClicked();

    void onLetterRemoveClicked();

    void handleCropping(String result);

    void setFilePath(String pictureImagePath);

    void onDestroy();

    void beforeEircodeChanged(int length);

    void afterEircodeChanged(Editable s);
}