package com.app.tributum.activity.vat;

import android.content.Intent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.app.tributum.activity.vat.model.VatModel;

import java.util.List;

public interface VatPresenter {

    void onRemovePhotoClick();

    void onSendClick(String name, String email, String startingMonth, String endingMonth);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    boolean onRecyclerViewTouch(MotionEvent event);

    void onDestroy();

    void onTaskCompleted(String name, String email, String startingMonth, String endingMonth, String fileName);

    List<VatModel> getInvoicesList();

    List<VatModel> getPrivatesList();

    void onCreate();

    void onBackPressed();

    void onAddFromGalleryClick();

    void onTakePhotoClick();

    void onTopViewClick();

    void setFilePath(String pictureImagePath);

    void onBottomSheetExpanded();

    void onPrivatesClick();
}