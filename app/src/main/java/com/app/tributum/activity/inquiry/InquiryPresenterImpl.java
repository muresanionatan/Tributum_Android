package com.app.tributum.activity.inquiry;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.AsyncListener;
import com.app.tributum.listener.RequestSentListener;
import com.app.tributum.model.EmailBody;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.utils.CalendarUtils;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.ImageUtils;
import com.app.tributum.utils.UploadAsyncTask;
import com.app.tributum.utils.ValidationUtils;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InquiryPresenterImpl implements InquiryPresenter, RequestSentListener, AsyncListener {

    final private InquiryView view;

    private File fileName;

    private boolean isPreview;

    private boolean isBottomSheetVisible;

    private String pictureImagePath;

    private String inquiryPhotoName;

    public InquiryPresenterImpl(InquiryView view) {
        this.view = view;
    }

    private void sendInquiry(String name, String email, String description) {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.TRIBUTUM_EMAIL, generateInternalEmailMessage(name, email, description)));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (!response.isSuccessful()) {
                    view.showToast(TributumApplication.getInstance().getResources().getString(R.string.something_went_wrong));
                    view.hideLoadingScreen();
                } else {
                    if (pictureImagePath != null && !pictureImagePath.equals("")) {
                        UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(name,
                                pictureImagePath,
                                inquiryPhotoName,
                                InquiryPresenterImpl.this,
                                UploadAsyncTask.UploadType.INQUIRY);
                        uploadAsyncTask.execute();
                    } else {
                        view.hideLoadingScreen();
                        view.showRequestSent();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                view.hideLoadingScreen();
                view.showToast(TributumApplication.getInstance().getResources().getString(R.string.something_went_wrong));
            }
        });
    }

    private String generateInternalEmailMessage(String name, String email, String description) {
        String formattedString = name.toUpperCase();
        formattedString = formattedString.replaceAll(" ", "%20");

        String resultString = name + " requested an inquiry:\n\n'" + description + "'";

        if (pictureImagePath != null) {
            inquiryPhotoName = CalendarUtils.getCurrentMonth() + "_"
                    + CalendarUtils.getCurrentDateInMilies();

            resultString += "\n\n" + "Click on below link to access the file\n\n"
                    + "https://www.dropbox.com/home/Apps/Tributum/INQUIRIES/"
                    + formattedString + "?preview="
                    + inquiryPhotoName
                    + ".png";
        }

        resultString += "\n\nPlease respond to: " + email;

        return resultString;
    }

    private void collapseBottomSheet() {
        isBottomSheetVisible = false;
        view.collapseBottomSheet();
    }

    @Override
    public void onSendClick(String name, String email, String description) {
        if (view == null)
            return;

        if (name.equals("")) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_name));
            view.setFocusOnName();
        } else if (!ValidationUtils.isEmailValid(email)) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_correct_email));
            view.setFocusOnEmail();
        } else if (description.equals("")) {
            view.showToast(TributumApplication.getInstance().getResources().getString(R.string.please_enter_description));
            view.setFocusOnDescription();
        } else {
            view.hideKeyboard();
            view.showLoadingScreen();
            sendInquiry(name, email, description);
        }
    }

    @Override
    public void onBackPressed() {
        if (view == null)
            return;

        if (isPreview) {
            isPreview = false;
            view.hidePreview();
        } else if (isBottomSheetVisible) {
            collapseBottomSheet();
        } else {
            view.closeActivity();
        }
    }

    @Override
    public void onOkClicked() {
        if (view != null)
            view.closeActivity();
    }

    @Override
    public void onTaskCompleted() {
        if (view != null) {
            view.hideLoadingScreen();
            view.showRequestSent();
        }
    }

    @Override
    public void onTopViewClick() {
        if (view != null)
            collapseBottomSheet();
    }

    @Override
    public void onBottomSheetExpanded() {
        if (view != null)
            view.showTopViewBottomSheet();
    }

    @Override
    public void onTakePhotoClick(String name) {
        if (view == null)
            return;

        collapseBottomSheet();
        fileName = new File(ImageUtils.getImagePath(name + ConstantsUtils.CAMERA_REQUEST_INQUIRY));
        view.collapseBottomSheet();
        view.takePhoto(ConstantsUtils.CAMERA_REQUEST_INQUIRY, fileName, pictureImagePath);
    }

    @Override
    public void onAddFromGalleryClick() {
        if (view != null)
            view.openPhotoChooserIntent();
        collapseBottomSheet();
    }

    @Override
    public void onPreviewPhotoClick() {
        if (view == null)
            return;
        isPreview = true;
        view.showImagePreview(pictureImagePath);
    }

    @Override
    public void onPlusClick() {
        if (view != null) {
            isBottomSheetVisible = true;
            view.openBottomSheet();
        }
    }

    @Override
    public void setFilePath(String picturePath) {
        pictureImagePath = picturePath;
    }

    @Override
    public void onDeleteClick() {
        if (view != null)
            view.resetThumbnailLayout();
        fileName = null;
    }

    @Override
    public void onRemovePhotoClick() {
        view.resetThumbnailLayout();
        fileName = null;
        isPreview = false;
        view.hidePreview();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (view == null || resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == ConstantsUtils.SELECT_REQUEST_INQUIRY)
            pictureImagePath = data.getData().toString();
        else if (requestCode == ConstantsUtils.CAMERA_REQUEST_INQUIRY)
            pictureImagePath = fileName.getAbsolutePath();

        view.setImageInHolder(pictureImagePath);
    }

    @Override
    public void onDestroy() {
        if (fileName == null)
            return;

        File fileToDelete = new File(pictureImagePath);
        if (fileToDelete.exists())
            fileToDelete.delete();
    }
}