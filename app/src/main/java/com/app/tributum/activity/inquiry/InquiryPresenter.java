package com.app.tributum.activity.inquiry;

import android.content.Intent;

public interface InquiryPresenter {

    void onSendClick(String name, String email, String description);

    void onRemovePhotoClick();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onDestroy();

    void onBackPressed();

    void onAddFromGalleryClick();

    void onTakePhotoClick(String name);

    void onTopViewClick();

    void onBottomSheetExpanded();

    void onPlusClick();

    void onPreviewPhotoClick();

    void onDeleteClick();

    void setFilePath(String picturePath);
}