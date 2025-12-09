package com.app.tributum.activity.company;

import android.content.Intent;
import android.net.Uri;
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

    void onFileChooserTopClicked();

    void onBottomSheetExpanded();

    void onAddFromGalleryClicked(int selectPictureRequest);

    void onTakePhotoClicked(String name, int requestId);

    void setFilePath(String pictureImagePath);

    void onRemovePhotoClicked(String fileName);

    void onDirector1PpsFrontPicker(Uri uri);

    void onDirector1PpsBackPicker(Uri uri);

    void onDirector1IdPicker(Uri uri);

    void onDirector1PassPicker(Uri uri);

    void onDirector2PpsFrontPicker(Uri uri);

    void onDirector2PpsBackPicker(Uri uri);

    void onDirector2IdPicker(Uri uri);

    void onDirector2PassPicker(Uri uri);

    void onDirector3PpsFrontPicker(Uri uri);

    void onDirector3PpsBackPicker(Uri uri);

    void onDirector3IdPicker(Uri uri);

    void onDirector3PassPicker(Uri uri);

    void handleCropping(String uriFilePath);

    void onDirector1PpsFrontClick();

    void onDirector1PpsBackClick();

    void onDirector1IdClick();

    void onDirector1PassClick();

    void onDirector2PpsFrontClick();

    void onDirector2PpsBackClick();

    void onDirector2IdClick();

    void onDirector2PassClick();

    void onDirector3PpsFrontClick();

    void onDirector3PpsBackClick();

    void onDirector3IdClick();

    void onDirector3PassClick();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onDirector1PpsFrontPreview();

    void onDirector1PpsBackPreview();

    void onDirector1IdPreview();

    void onDirector1PassPreview();

    void onDirector1PpsFrontDelete();

    void onDirector1PpsBackDelete();

    void onDirector1IdDelete();

    void onDirector1PassDelete();

    void onDirector2PpsFrontPreview();

    void onDirector2PpsBackPreview();

    void onDirector2IdPreview();

    void onDirector2PassPreview();

    void onDirector2PpsFrontDelete();

    void onDirector2PpsBackDelete();

    void onDirector2IdDelete();

    void onDirector2PassDelete();

    void onDirector3PpsFrontPreview();

    void onDirector3PpsBackPreview();

    void onDirector3IdPreview();

    void onDirector3PassPreview();

    void onDirector3PpsFrontDelete();

    void onDirector3PpsBackDelete();

    void onDirector3IdDelete();

    void onDirector3PassDelete();

    void setDirector1Details(String firstName, String surName, String birthday, String pps,
                             String nationality, String occupation, String home, String directorships, String other);
    void setDirector2Details(String firstName, String surName, String birthday, String pps,
                             String nationality, String occupation, String home, String directorships, String other);
    void setDirector3Details(String firstName, String surName, String birthday, String pps,
                             String nationality, String occupation, String home, String directorships, String other);
}