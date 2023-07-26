package com.app.tributum.activity.contract;

import android.net.Uri;

import java.io.File;

public interface ContractView {

    void showToast(int stringId);

    void showLoadingScreen();

    void hideLoadingScreen();

    void closeActivity();

    void setBirthdayText(String string);

    void setStartingDateText(String string);

    void moveBirthdayCursorToEnd();

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

    void showEmploymentInfoLayoutFromRight();

    void hideEmploymentInfoLayoutToRight();

    void setTitle(int stringId);

    void setSubtitle(int stringId);

    void setConfirmationButtonText(int stringId);

    void setFirstCheckboxState(boolean state);

    void setSecondCheckboxState(boolean state);

    void setThirdCheckboxState(boolean state);

    void setFourthCheckboxState(boolean state);

    void setSixthCheckboxState(boolean state);

    void openFilePicker(int requestId);

    void takePicture(int requestId, File file, String pictureImagePath);

    void resetPpsFrontLayout();

    void resetPpsBackLayout();

    void resetIdLayout();

    void resetMarriageCertificateLayout();

    void setPpsFrontImage(String ppsFileFront);

    void setPpsBackImage(String ppsFileBack);

    void setIdImage(String idFile);

    void setMarriageCertificateImage(String marriageCertificateFile);

    void showRequestSentScreen();

    void selectSingle();

    void selectMarriage();

    void selectDivorced();

    void selectCohabiting();

    void selectText(int textId);

    void deselectText(int textId);

    void showEmploymentInfoLayoutFromLeft();

    void hideEmploymentInfoLayoutToLeft();

    void removeFocus();

    void focusOnFirstName();

    void focusOnLastName();

    void focusOnAddress1();

    void focusOnAddress2();

    void focusOnBirthday();

    void focusOnOccupation();

    void focusOnPhone();

    void focusOnEmail();

    void focusOnEircode();

    void focusOnPps();

    void scrollToPpsFront();

    void scrollToId();

    void scrollToTaxes();

    void hideAsteriskView();

    void showAsteriskView();

    void setEircodeText(String string);

    void moveEircodeCursorToEnd(int length);

    void moveStartingDayCursorToEnd();

    void startCrop(Uri imageUri);
}