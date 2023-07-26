package com.app.tributum.activity.contract;

import android.content.Intent;
import android.text.Editable;

public interface ContractPresenter {

    void onBackPressed();

    void handleSendButtonClick(String firstName, String lastName, String address1, String address2, String address3, String eircode,
                               String birthday, String occupation, String phone, String email, String bankAccount,
                               String pps, String startingDate, String noOfKids);

    void onCreate();

    void afterBirthdayChanged(Editable string);

    void beforeBirthdayChanged(int length);

    void onClearSignatureClick();

    void onSingleClicked();

    void onMarriedClicked();

    void onDivorcedClick();

    void onCohabitingClicked();

    void onSelfEmployeeClick();

    void onEmployeeClick();

    void onFileChooserTopClicked();

    void onPpsFrontClicked();

    void onPpsBackClicked();

    void onIdClicked();

    void onMarriageCertificateClicked();

    void onBottomSheetExpanded();

    void onAddFromGalleryClicked(int requestCode);

    void onTakePhotoClicked(String name, int requestId);

    void onRemovePhotoClicked(String fileName);

    void onFirstCheckboxClicked();

    void onSecondCheckboxClicked();

    void onThirdCheckboxClicked();

    void onFourthCheckboxClicked();

    void onSixthCheckboxClicked();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onIdPreviewClicked();

    void onIdRemoveClicked();

    void onPpsFrontPreviewClicked();

    void onPpsFrontRemoveClicked();

    void onPpsBackPreviewClicked();

    void onPpsBackRemoveClicked();

    void onMarriagePreviewClicked();

    void onMarriageRemoveClicked();

    void handleCropping(String result);

    void setFilePath(String pictureImagePath);

    void onBirthdayDateSet(int year, int monthOfYear, int dayOfMonth);

    void onDestroy();

    void beforeEircodeChanged(int length);

    void afterEircodeChanged(Editable s);

    void onStartingDateSet(int year, int monthOfYear, int dayOfMonth);

    void beforeStartingDateChanged(int length);

    void afterStartingDateChanged(Editable string);
}