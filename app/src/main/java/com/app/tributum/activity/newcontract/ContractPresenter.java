package com.app.tributum.activity.newcontract;

import android.content.Intent;
import android.text.Editable;

import com.app.tributum.model.PaymentModel;

import java.io.File;
import java.util.List;

public interface ContractPresenter {

    void onBackPressed();

    void handleSendButtonClick(String name, String address, String pps, String email, String contractDate, String birthday, String occupation, String otherText,
                               String phone, String bankAccount, String noOfKids);

    void onCreate();

    void onFormStarted(String name);

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

    void onAddFromGalleryClicked(int requestCode);

    void onTakePhotoClicked(String name, int requestId);

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

    void onDestroy();

    void afterContractDateChanged(Editable s);
}