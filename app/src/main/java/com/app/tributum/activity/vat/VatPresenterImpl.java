package com.app.tributum.activity.vat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.fragment.invoices.listener.InvoiceItemClickListener;
import com.app.tributum.fragment.invoices.model.InvoiceModel;
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

    private VatView vatView;

    private int photoClicked;

    public int PICTURE_NUMBER = 1;

    private boolean isPreview;

    private boolean isBottomSheetVisible;

    private Resources resources;

    private String pictureImagePath = "";

    private List<InvoiceModel> list;

    VatPresenterImpl(VatView vatView) {
        this.vatView = vatView;
        this.resources = TributumApplication.getInstance().getResources();
    }

    @Override
    public void onCreate() {
        list = new ArrayList<>();
        list.add(new InvoiceModel(""));
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
    public List<InvoiceModel> getList() {
        return list;
    }

    @Override
    public void onSendClick(String name, String email, String startingMonth, String endingMonth) {
        if (name.equals("")
                || email.equals("")
                || startingMonth.equals("")
                || endingMonth.equals("")) {
            vatView.showToast(resources.getString(R.string.add_all_info));
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
            vatView.addItemToList(new InvoiceModel(pictureImagePath));

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
        if (vatView == null)
            return;

        if (checkPermissions()) {
            vatView.takePhoto(pictureImagePath);
        } else {
            vatView.requestPermissions(new String[]{android.Manifest.permission.CAMERA}, ConstantsUtils.CAMERA_REQUEST_INVOICES_ID);
        }
    }

    @Override
    public void onAddFromGalleryClick() {
        pickPictureFromGallery();
        collapseBottomSheet();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void pickPictureFromGallery() {
        if (Build.VERSION.SDK_INT >= 23 && vatView != null) {
            if (vatView.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                vatView.openPhotoChooserIntent();
            } else {
                vatView.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_VAT);
            }
        }
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
                    vatView.hideLoadingScreen();
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
        formattedString = formattedString.replace(" ", "%20");
        return resources.getString(R.string.invoices_message_email) + name
                + resources.getString(R.string.invoices_message_email_part2) + startingMonth
                + " - " + endingMonth
                + "\n\n" + "Click on below link to access the pdf\n\n"
                + "https://www.dropbox.com/home/Apps/Tributum/VATS/"
                + formattedString + "?preview="
                + startingMonth
                + "_" + endingMonth
                + ".pdf";
    }

    private String generateClientEmailMessage(String startingMonth, String endingMonth) {
        return "The receipts"
                + resources.getString(R.string.invoices_message_email_part2) + startingMonth
                + " - " + endingMonth
                + " were sent and we'll be processed.";
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : ConstantsUtils.PERMISSIONS) {
            result = vatView.checkPermission(permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty() && vatView != null) {
            vatView.requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), ConstantsUtils.MULTIPLE_PERMISSIONS_PPS_FRONT);
            return false;
        }
        return true;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (requestCode == ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_VAT) {
                pickPictureFromGallery();
            } else {
                onTakePhotoClick();
            }
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