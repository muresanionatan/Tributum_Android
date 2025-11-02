package com.app.tributum.activity.form;

import android.content.Intent;
import android.net.Uri;
import android.view.MotionEvent;

import androidx.activity.result.ActivityResult;

import com.app.tributum.activity.vat.model.VatModel;

import java.util.List;

public interface FormPresenter {
    void onBackPressed();
    void onCreate();
    void setFilePath(String pictureImagePath);
    void onBankYesClick();
    void onBankNoClick();
    void onMarriageYesClick();
    void onMarriageNoClick();
    List<VatModel> getBsList();
    List<VatModel> getChildList();
    List<VatModel> getExpensesList();
    List<VatModel> getMedicalList();
    void onBottomSheetExpanded();
    boolean onRecyclerViewTouch(MotionEvent event);
    void onTopViewClick();
    void onAddFromGalleryClick();
    void onTakePhotoClick();
    void onActivityResult(int requestCode, int resultCode, Intent data);
    void onBankSelected(List<Uri> uris);
    void onKidsSelected(List<Uri> uris);
    void onExpensesSelected(List<Uri> uris);
    void onMedicalSelected(List<Uri> uris);
    void onRentSelected(Uri uri);
    void onRtbSelected(Uri uri);
    void onMarriageSelected(Uri uri);
    void onFisc1Selected(Uri uri);
    void onFisc2Selected(Uri uri);
    void handleCropping(String uriFilePath);
    void onExpensesYesClick();
    void onExpensesNoClick();
    void onMedicalYesClick();
    void onMedicalNoClick();
    String[] getFormYears();
    void onSendClick(String fullName, String email, String year, String rent);

    void onAddPdfClick();

    void handlePdfSelected(ActivityResult result);
}