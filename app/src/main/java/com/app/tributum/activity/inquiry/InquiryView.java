package com.app.tributum.activity.inquiry;

import java.io.File;

public interface InquiryView {

    void showToast(String message);

    void closeActivity();

    void setFocusOnName();

    void setFocusOnEmail();

    void setFocusOnDescription();

    void hideKeyboard();

    void showLoadingScreen();

    void hideLoadingScreen();

    void showRequestSent();

    void showTopViewBottomSheet();

    void showImagePreview(String filePath);

    void hidePreview();

    void openBottomSheet();

    void openPhotoChooserIntent();

    void collapseBottomSheet();

    void takePhoto(int requestId, File file, String picturePath);

    void setImageInHolder(String fileName);

    void resetThumbnailLayout();
}