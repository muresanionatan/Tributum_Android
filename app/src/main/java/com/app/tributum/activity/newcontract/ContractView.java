package com.app.tributum.activity.newcontract;

import android.graphics.Bitmap;

import java.io.File;

public interface ContractView {

    void showToast(String string);

    void showLoadingScreen();

    void hideKeyboard();

    void hideLoadingScreen();

    void closeActivity();

    void setBirthdayText(String string);

    void moveBirthdayCursorToEnd();

    void clearSignature();

    void hideMaritalLayout();

    void changeSingleState(boolean check);

    void changeMarriedState(boolean check);

    void changeDivorcedState(boolean check);

    void changeCohabitingState(boolean check);

    void showMaritalLayout();

    void showTaxesView();

    void hideTaxesView();

    void changeSelfEmployedState(boolean state);

    void changeEmployeeState(boolean state);

    void hideBottomSheet();

    void showFileChooser(int selectPictureRequest, int takePictureRequest);

    void openFilePreview(String fileName);

    void setFileChooserToVisible();

    void closePreview();

    void showPersonalInfoLayout();

    void hidePersonalInfoLayout();

    void showEmploymentInfoLayout();

    void hideEmploymentInfoLayout();

    void showSignatureLayout();

    void hideSignatureLayout();

    void setTitle(String title);

    void setSubtitle(String subtitle);

    void showClearButton();

    void hideClearButton();

    void setConfirmationButtonText(String string);

    void setSendDisabled();

    void setSendEnabled();

    void setFirstCheckboxState(boolean state);

    void setSecondCheckboxState(boolean state);

    void setThirdCheckboxState(boolean state);

    void setFourthCheckboxState(boolean state);

    void setFifthCheckboxState(boolean state);

    void setSixthCheckboxState(boolean state);

    void setSeventhCheckboxState(boolean state);

    void setEightCheckboxState(boolean state);

    void setNinthCheckboxState(boolean state);

    void setFocusOnOther();

    void hideOther();

    void openFilePicker(int requestId);

    boolean hasStoragePermission();

    void requestOnePermission(int requestId);

    void requestListOfPermissions(String[] permissions, int requestCode);

    void takePicture(int requestId, File file, String pictureImagePath);

    int hasPermission(String permission);

    void resetPpsFrontLayout();

    void resetPpsBackLayout();

    void resetIdLayout();

    void resetMarriageCertificateLayout();

    void setPpsFrontImage(String ppsFileFront);

    void setPpsBackImage(String ppsFileBack);

    void setIdImage(String idFile);

    void setMarriageCertificateImage(String marriageCertificateFile);

    void setDrawingCacheEnabled();

    Bitmap getSignatureFile();

    void setContractDateText(String s);

    void moveContractCursorToEnd();

    void showRequestSentScreen();
}