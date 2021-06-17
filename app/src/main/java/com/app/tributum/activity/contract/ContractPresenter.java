package com.app.tributum.activity.contract;

import android.content.Intent;
import android.text.Editable;

import java.io.File;

public interface ContractPresenter {

    void onBackPressed();

    void handleSendButtonClick(String firstName, String lastName, String address1, String address2, String address3, String eircode,
                               String birthday, String occupation, String phone, String email, String bankAccount,
                               String pps, String contractDate, String noOfKids, String otherText);

    void onCreate();

    void afterBirthdayChanged(Editable string);

    void beforeBirthdayChanged(int length);

    void beforeOtherChanged();

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

    void onAddFromGalleryClicked(int requestCode, int storagePermissionId);

    void onTakePhotoClicked(String name, int requestId, int permissionId);

    void onRemovePhotoClicked(String fileName);

    void onFirstCheckboxClicked();

    void onSecondCheckboxClicked();

    void onThirdCheckboxClicked();

    void onFourthCheckboxClicked();

    void onFifthCheckboxClicked();

    void onSixthCheckboxClicked();

    void onSeventhCheckboxClicked();

    void onEightCheckboxClicked();

    void onNinthCheckboxClicked();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onIdPreviewClicked();

    void onIdRemoveClicked();

    void onPpsFrontPreviewClicked();

    void onPpsFrontRemoveClicked();

    void onPpsBackPreviewClicked();

    void onPpsBackRemoveClicked();

    void onMarriagePreviewClicked();

    void onMarriageRemoveClicked();

    void setFilePath(String pictureImagePath);

    void setFile(File file);

    void beforeContractDateChanged(int length);

    void afterContractDateChanged(Editable s);

    void checkPersonalValidation(String firstName, String lastName, String address1, String address2, String birthday, String occupation, String phone,
                                 String email, String pps, String contractDate);

    void onRequestPermissionResult(String name, int requestCode, int[] grantResults);

    void onBirthdayDateSet(int year, int monthOfYear, int dayOfMonth);

    void onContractDateSet(int year, int monthOfYear, int dayOfMonth);
}