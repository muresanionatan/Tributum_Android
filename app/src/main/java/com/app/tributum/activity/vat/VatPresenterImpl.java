package com.app.tributum.activity.vat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.activity.vat.model.VatModel;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.InvoiceItemClickListener;
import com.app.tributum.listener.InvoicesDeleteListener;
import com.app.tributum.listener.RequestSentListener;
import com.app.tributum.model.EmailBody;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.utils.ConstantsUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VatPresenterImpl implements VatPresenter, InvoicesDeleteListener, InvoiceItemClickListener, RequestSentListener {

    private final VatView vatView;

    private int photoClicked;

    public int PICTURE_NUMBER = 1;

    private boolean isPreview;

    private boolean isBottomSheetVisible;

    private final Resources resources;

    private String pictureImagePath = "";

    private List<VatModel> list;

    VatPresenterImpl(VatView vatView) {
        this.vatView = vatView;
        this.resources = TributumApplication.getInstance().getResources();
    }

    @Override
    public void onCreate() {
        list = new ArrayList<>();
        list.add(new VatModel(""));
    }

    @Override
    public void onRemovePhotoClick() {
        if (vatView == null)
            return;
        removeItemFromList(photoClicked);
        clearPreview();
    }

    private void removeItemFromList(int photoClicked) {
        vatView.removeItemFromList(photoClicked);
    }

    @Override
    public List<VatModel> getList() {
        return list;
    }

    @Override
    public void onSendClick(String name, String email, String startingMonth, String endingMonth) {
        if (name.equals("")) {
            vatView.showToast(resources.getString(R.string.please_enter_name));
            vatView.setFocusOnName();
        } else if (email.equals("")) {
            vatView.showToast(resources.getString(R.string.please_enter_correct_email));
            vatView.setFocusOnEmail();
        } else if (startingMonth.equals("")) {
            vatView.showToast(resources.getString(R.string.please_enter_starting_month));
            vatView.setFocusOnStartingMonth();
        } else if (endingMonth.equals("")) {
            vatView.showToast(resources.getString(R.string.please_enter_ending_month));
            vatView.setFocusOnEndingMonth();
        } else if (PICTURE_NUMBER > 1) {
            vatView.hideKeyboard();
            vatView.showLoadingScreen();
            vatView.startPdfCreation();
        } else {
            vatView.showToast(resources.getString(R.string.no_photo_taken));
        }
    }

    @Override
    public void setFilePath(String pictureImagePath) {
        this.pictureImagePath = pictureImagePath;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (vatView == null)
            return;

        if (requestCode == ConstantsUtils.CAMERA_REQUEST_INVOICES_ID && resultCode == Activity.RESULT_OK) {
            vatView.addItemToList(new VatModel(pictureImagePath));

            if (PICTURE_NUMBER < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT) {
                onTakePhotoClick();
                PICTURE_NUMBER++;

                if (!TributumAppHelper.getBooleanSetting(AppKeysValues.INVOICES_TAKEN)) {
                    TributumAppHelper.saveSetting(AppKeysValues.INVOICES_TAKEN, AppKeysValues.TRUE);
                }
            }
        } else if (requestCode == ConstantsUtils.SELECT_PICTURES && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                PICTURE_NUMBER = count;
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    vatView.getFilesFromGallery(imageUri);
                }
            } else if (data.getData() != null) {
                PICTURE_NUMBER++;
                vatView.getFilesFromGallery(data.getData());
            }
        }
    }

    @Override
    public boolean onRecyclerViewTouch(MotionEvent event) {
        if (vatView != null && event != null && event.getAction() == MotionEvent.ACTION_MOVE) {
            vatView.hideKeyboard();
        }
        return false;
    }

    @Override
    public void onDestroy() {
        clearFormStarted();
    }

    @Override
    public void onTaskCompleted(String name, String email, String startingMonth, String endingMonth) {
        if (vatView == null)
            return;
        saveListToPreferences(name, email);
        sendInternalEmail(name, email, startingMonth, endingMonth);
    }

    @Override
    public void onTopViewClick() {
        if (vatView != null) {
            collapseBottomSheet();
        }
    }

    @Override
    public void onBottomSheetExpanded() {
        if (vatView != null)
            vatView.showTopViewBottomSheet();
    }

    @Override
    public void onTakePhotoClick() {
        collapseBottomSheet();
        if (vatView != null)
            vatView.takePhoto(pictureImagePath);
    }

    @Override
    public void onAddFromGalleryClick() {
        pickPictureFromGallery();
        collapseBottomSheet();
    }

    private void pickPictureFromGallery() {
        if (vatView != null)
            vatView.openPhotoChooserIntent();
    }

    private void collapseBottomSheet() {
        isBottomSheetVisible = false;
        vatView.collapseBottomSheet();
    }

    private void saveListToPreferences(String name, String email) {
        if (!name.equals(""))
            TributumAppHelper.saveSetting(AppKeysValues.INVOICE_NAME, name);
        if (!email.equals(""))
            TributumAppHelper.saveSetting(AppKeysValues.INVOICE_EMAIL, email);
    }

    private void clearFormStarted() {
        PICTURE_NUMBER = 1;
        if (TributumAppHelper.getBooleanSetting(AppKeysValues.INVOICES_TAKEN)) {
            TributumAppHelper.saveSetting(AppKeysValues.INVOICES_TAKEN, AppKeysValues.FALSE);
        }
    }

    private void sendInternalEmail(String name, String email, String startingMonth, String endingMonth) {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.TRIBUTUM_EMAIL, generateInternalEmailMessage(name, startingMonth, endingMonth)));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful())
                    sendClientEmail(email, startingMonth, endingMonth);
                else
                    vatView.showToast(resources.getString(R.string.something_went_wrong));

                vatView.hideLoadingScreen();
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                vatView.hideLoadingScreen();
                vatView.showToast(resources.getString(R.string.something_went_wrong));
            }
        });
    }

    private void sendClientEmail(String email, String startingMonth, String endingMonth) {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(email, generateClientEmailMessage(startingMonth, endingMonth)));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    vatView.showRequestSentScreen();
                } else {
                    vatView.showToast(resources.getString(R.string.something_went_wrong));
                }

                vatView.hideLoadingScreen();
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                vatView.hideLoadingScreen();
                vatView.showToast(resources.getString(R.string.something_went_wrong));
            }
        });
    }

    private String generateInternalEmailMessage(String name, String startingMonth, String endingMonth) {
        String formattedString = name.toUpperCase();
        formattedString = formattedString.replaceAll(" ", "%20");
        return resources.getString(R.string.invoices_message_email) + name
                + resources.getString(R.string.invoices_message_email_part2) + startingMonth.replaceAll(" ", "_")
                + " - " + endingMonth.replaceAll(" ", "_")
                + "\n\n" + "Click on below link to access the pdf\n\n"
                + "https://www.dropbox.com/home/Apps/Tributum/VATS/"
                + formattedString + "?preview="
                + startingMonth.replaceAll(" ", "_")
                + "_" + endingMonth.replaceAll(" ", "_")
                + ".pdf";
    }

    private String generateClientEmailMessage(String startingMonth, String endingMonth) {
        return "The receipts"
                + resources.getString(R.string.invoices_message_email_part2) + startingMonth
                + " - " + endingMonth
                + " were sent and will be processed.";
    }

    private void clearPreview() {
        isPreview = false;
        vatView.hidePreview();
    }

    @Override
    public void clearList() {
        clearFormStarted();
    }

    @Override
    public void onPreviewPhotoClick(String filePath, int photoIndex) {
        if (vatView == null)
            return;
        isPreview = true;
        photoClicked = photoIndex;
        vatView.showImagePreview(filePath);
    }

    @Override
    public void onPlusCLick() {
        if (vatView != null) {
            isBottomSheetVisible = true;
            vatView.openBottomSheet();
        }
    }

    @Override
    public void onDeleteClick(String filePath, int photoIndex) {
        if (vatView != null)
            removeItemFromList(photoIndex);
    }

    @Override
    public void onBackPressed() {
        if (vatView == null)
            return;
        if (isPreview)
            clearPreview();
        else if (isBottomSheetVisible)
            collapseBottomSheet();
        else
            vatView.closeActivity();
    }

    @Override
    public void onOkClicked() {
        if (vatView != null)
            vatView.closeActivity();
    }
}