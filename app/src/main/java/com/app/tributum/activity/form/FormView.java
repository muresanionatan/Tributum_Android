package com.app.tributum.activity.form;

import android.net.Uri;

import com.app.tributum.activity.vat.model.VatModel;

import java.io.File;

public interface FormView {

    void showToast(int stringId);

    void showLoadingScreen();

    void hideLoadingScreen();

    void closeActivity();

    void showFileChooser(int selectPictureRequest, int takePictureRequest);

    void setFileChooserToVisible();

    void takePhoto(String pictureImagePath, int state);

    void showRequestSentScreen();

    void showRentLayout();

    void hideRentLayout();

    void showMarriageLayout();

    void hideMarriageLayout();

    void collapseBottomSheet();

    void showBottomSheet();

    void hideKeyboard();

    void showTopViewBottomSheet();

    void addItemToBankList(VatModel vatModel);

    void addItemToKidsList(VatModel vatModel);

    void addItemToExpensesList(VatModel vatModel);
    void addItemToMedicalList(VatModel vatModel);

    void setImage(String file, int resourceId);

    void hideBottomSheet();

    void pickBank();
    void pickKids();
    void pickExpenses();
    void pickMedical();
    void pickRent();
    void pickRtb();
    void pickMarriage();
    void pickFisc1();
    void pickFisc2();

    void removeItemFromBank(int photoClicked);
    void removeItemFromKids(int photoClicked);
    void removeItemFromExpenses(int photoClicked);
    void removeItemFromMedical(int photoClicked);

    void startCrop(Uri uri);

    void showExpensesLayout();
    void hideExpensesLayout();
    void showMedicalLayout();
    void hideMedicalLayout();
}