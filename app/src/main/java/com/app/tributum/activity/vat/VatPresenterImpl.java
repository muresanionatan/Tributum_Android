package com.app.tributum.activity.vat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.activity.vat.model.VatModel;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.CombinePdfListener;
import com.app.tributum.listener.InvoiceItemClickListener;
import com.app.tributum.listener.InvoicesDeleteListener;
import com.app.tributum.listener.RequestSentListener;
import com.app.tributum.model.EmailBody;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.thread.CombinePhotosInPdfTask;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.ui.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VatPresenterImpl implements VatPresenter, InvoicesDeleteListener, InvoiceItemClickListener, RequestSentListener,
        CombinePdfListener {

    private final VatView vatView;

    private int photoClicked;

    public int PICTURE_NUMBER;

    private boolean isPreview;

    private boolean isBottomSheetVisible;

    private final Resources resources;

    private String pictureImagePath = "";

    private List<VatModel> invoicesList;

    private List<VatModel> privatesList;
    private List<VatModel> statementsList;

    private List<File> invoicesPdfList;
    private List<File> privatesPdfList;
    private List<File> statementsPdfList;

    private boolean hasPrivates;

    private int previewState = 0;
    private boolean hasVan;
    private String name;
    private String startingMonth;
    private String endingMonth;
    private String email;
    private String fileName;

    VatPresenterImpl(VatView vatView) {
        this.vatView = vatView;
        this.resources = TributumApplication.getInstance().getResources();
    }

    @Override
    public void onCreate() {
        invoicesList = new ArrayList<>();
        invoicesList.add(new VatModel(""));

        privatesList = new ArrayList<>();
        privatesList.add(new VatModel(""));

        statementsList = new ArrayList<>();
        statementsList.add(new VatModel(""));

        invoicesPdfList = new ArrayList<>();
        privatesPdfList = new ArrayList<>();
        statementsPdfList = new ArrayList<>();
    }

    @Override
    public void onRemovePhotoClick() {
        if (vatView == null)
            return;
        removeItemFromList(photoClicked, previewState);
        previewState = 0;
        clearPreview();
    }

    private void removeItemFromList(int photoClicked, int mode) {
        if (mode == 1)
            vatView.removeItemFromInvoicesList(photoClicked);
        else if (mode == 2)
            vatView.removeItemFromPrivatesList(photoClicked);
        else if (mode == 3)
            vatView.removeItemFromStatementsList(photoClicked);
    }

    @Override
    public List<VatModel> getInvoicesList() {
        return invoicesList;
    }

    @Override
    public List<VatModel> getPrivatesList() {
        return privatesList;
    }

    @Override
    public List<VatModel> getStatementsList() {
        return statementsList;
    }

    @Override
    public void onVanClick(boolean checkboxClicked) {
        hasVan = !hasVan;
        if (!checkboxClicked)
            vatView.checkVan(hasVan);
    }

    @Override
    public void onSendClick(String name, String email, String startingMonth, String endingMonth) {
        if (name.isEmpty()) {
            vatView.showToast(resources.getString(R.string.please_enter_name));
            vatView.setFocusOnName();
        } else if (email.isEmpty()) {
            vatView.showToast(resources.getString(R.string.please_enter_correct_email));
            vatView.setFocusOnEmail();
        } else if ((statementsList.size() == 1 && statementsList.get(0).getFilePath().isEmpty())
                && statementsPdfList.isEmpty()) {
            vatView.showToast(resources.getString(R.string.please_add_bank_statement));
        } else if (PICTURE_NUMBER > 0 &&
                (!invoicesList.get(0).getFilePath().isEmpty())
                || !invoicesPdfList.isEmpty()
                || !privatesList.get(0).getFilePath().isEmpty()
                || !privatesPdfList.isEmpty()) {
            this.name = name;
            this.email = email;
            this.startingMonth = startingMonth;
            this.endingMonth = endingMonth;

            vatView.hideKeyboard();
            vatView.showLoadingScreen();
            vatView.startPdfCreation(invoicesList, hasPrivates ? privatesList : null, invoicesPdfList, privatesPdfList);
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
            vatView.addItemToInvoicesList(new VatModel(pictureImagePath));

            if (PICTURE_NUMBER < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT) {
                onTakePhotoClick();
                PICTURE_NUMBER++;

                if (!TributumAppHelper.getBooleanSetting(AppKeysValues.INVOICES_TAKEN)) {
                    TributumAppHelper.saveSetting(AppKeysValues.INVOICES_TAKEN, AppKeysValues.TRUE);
                }
            }
        } else if (requestCode == ConstantsUtils.CAMERA_REQUEST_PRIVATES_ID && resultCode == Activity.RESULT_OK) {
            vatView.addItemToPrivatesList(new VatModel(pictureImagePath));

            if (PICTURE_NUMBER < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT) {
                onTakePhotoClick();
                PICTURE_NUMBER++;

                if (!TributumAppHelper.getBooleanSetting(AppKeysValues.INVOICES_TAKEN)) {
                    TributumAppHelper.saveSetting(AppKeysValues.INVOICES_TAKEN, AppKeysValues.TRUE);
                }
            }
        } else if (requestCode == ConstantsUtils.CAMERA_REQUEST_STATEMENTS_ID && resultCode == Activity.RESULT_OK) {
            vatView.addItemToStatementsList(new VatModel(pictureImagePath));

            if (PICTURE_NUMBER < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT) {
                onTakePhotoClick();
                PICTURE_NUMBER++;
            }
        }
    }

    @Override
    public void onPrivatesSelected(List<Uri> uris) {
        int count = uris.size();
        PICTURE_NUMBER = count;
        for (int i = 0; i < count; i++) {
            vatView.getFilesFromGalleryForPrivates(uris.get(i));
        }
    }

    @Override
    public void onInvoicesSelected(List<Uri> uris) {
        int count = uris.size();
        PICTURE_NUMBER = count;
        for (int i = 0; i < count; i++) {
            Uri imageUri = uris.get(i);
            vatView.getFilesFromGalleryForInvoices(imageUri);
        }
    }

    @Override
    public void onStatementsSelected(List<Uri> uris) {
        int count = uris.size();
        PICTURE_NUMBER = count;
        for (int i = 0; i < count; i++) {
            Uri imageUri = uris.get(i);
            vatView.getFilesFromGalleryForStatements(imageUri);
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
    public void onTaskCompleted(String name, String email, String startingMonth, String endingMonth, String fileName) {
        if (vatView == null)
            return;

        this.fileName = fileName;
        saveListToPreferences(name, email);
        CombinePhotosInPdfTask combinePhotosInPdfTask =
                new CombinePhotosInPdfTask(this, statementsList, name,
                        ("bank_statements" + "_" + startingMonth + "_" + endingMonth), statementsPdfList, "vat");
        combinePhotosInPdfTask.execute();
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
        int state;
        if (previewState == 1)
            state = ConstantsUtils.CAMERA_REQUEST_INVOICES_ID;
        else if (previewState == 2)
            state = ConstantsUtils.CAMERA_REQUEST_PRIVATES_ID;
        else
            state = ConstantsUtils.CAMERA_REQUEST_STATEMENTS_ID;
        if (vatView != null)
            vatView.takePhoto(pictureImagePath, state);
    }

    @Override
    public void onAddFromGalleryClick() {
        pickPictureFromGallery();
        collapseBottomSheet();
    }

    @Override
    public void onAddPdfClick() {
        vatView.openPdfIntent();
    }

    @Override
    public void handlePdfSelected(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            PICTURE_NUMBER++;
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                Uri pdfUri = data.getData();

                // Copy PDF to internal storage on background thread
                new Thread(() -> {
                    try {
                        File pdfFile = FileUtils.copyPdfToInternalStorage(pdfUri);
                        if (pdfFile != null && pdfFile.exists()) {
                            // Store the PDF file based on current state
                            // Update UI on main thread
                            ((Activity) vatView).runOnUiThread(() -> {
                                if (previewState == 1) {
                                    invoicesPdfList.add(pdfFile);
                                    vatView.addItemToInvoicesList(new VatModel(FileUtils.getFileName(pdfUri), true));
                                } else if (previewState == 2) {
                                    privatesPdfList.add(pdfFile);
                                    vatView.addItemToPrivatesList(new VatModel(FileUtils.getFileName(pdfUri), true));
                                } else if (previewState == 3) {
                                    statementsPdfList.add(pdfFile);
                                    vatView.addItemToStatementsList(new VatModel(FileUtils.getFileName(pdfUri), true));
                                }

                                vatView.collapseBottomSheet();
                            });
                        } else {
                            ((Activity) vatView).runOnUiThread(() ->
                                    vatView.showToast(TributumApplication.getInstance().getResources().getString(R.string.something_went_wrong)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ((Activity) vatView).runOnUiThread(() ->
                                vatView.showToast(TributumApplication.getInstance().getResources().getString(R.string.something_went_wrong)));
                    }
                }).start();
            }
        }
    }


    private void pickPictureFromGallery() {
        int state;
        if (previewState == 1)
            state = ConstantsUtils.SELECT_PICTURES_FOR_INVOICES;
        else if (previewState == 2)
            state = ConstantsUtils.SELECT_PICTURES_FOR_PRIVATES;
        else
            state = ConstantsUtils.SELECT_PICTURES_FOR_STATEMENTS;
        if (vatView != null)
            vatView.openPhotoChooserIntent(state);
    }

    private void collapseBottomSheet() {
        isBottomSheetVisible = false;
        vatView.collapseBottomSheet();
    }

    private void saveListToPreferences(String name, String email) {
        if (!name.isEmpty())
            TributumAppHelper.saveSetting(AppKeysValues.INVOICE_NAME, name);
        if (!email.isEmpty())
            TributumAppHelper.saveSetting(AppKeysValues.INVOICE_EMAIL, email);
    }

    private void clearFormStarted() {
        PICTURE_NUMBER = 0;
        if (TributumAppHelper.getBooleanSetting(AppKeysValues.INVOICES_TAKEN)) {
            TributumAppHelper.saveSetting(AppKeysValues.INVOICES_TAKEN, AppKeysValues.FALSE);
        }
    }

    private void sendInternalEmail(String name, String email, String startingMonth, String endingMonth, String fileName) {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.TRIBUTUM_EMAIL, generateInternalEmailMessage(name, startingMonth, endingMonth, fileName), "Android"));
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

        Call<Object> call = api.sendEmail(new EmailBody(email, generateClientEmailMessage(startingMonth, endingMonth), "Android"));
        call.enqueue(new Callback<>() {
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

    private String generateInternalEmailMessage(String name, String startingMonth, String endingMonth, String fileName) {
        String formattedString = name.toUpperCase();
        formattedString = formattedString.replaceAll(" ", "%20");
        String result = resources.getString(R.string.invoices_message_email) + name
                + resources.getString(R.string.invoices_message_email_part2) + startingMonth.replaceAll(" ", "_")
                + " - " + endingMonth.replaceAll(" ", "_")
                + "\n\n" + "Click on below link to access the pdf\n\n"
                + "https://www.dropbox.com/home/Apps/Tributum/VATS/"
                + formattedString + "?preview="
                + fileName
                + ".pdf";
        if (hasVan)
            result = result + "\n\n" + "Note that the client has a VAN";

        return result;
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
    public void onPreviewPhotoClick(String filePath, int photoIndex, int mode) {
        if (vatView == null)
            return;
        isPreview = true;
        photoClicked = photoIndex;
        previewState = mode;
        vatView.showImagePreview(filePath);
    }

    @Override
    public void onPlusCLick(int mode) {
        if (vatView != null) {
            isBottomSheetVisible = true;
            previewState = mode;
            vatView.openBottomSheet();
        }
    }

    @Override
    public void onDeleteClick(String filePath, int photoIndex, int mode) {
        if (vatView != null) {
            removeItemFromList(photoIndex, mode);
            previewState = 0;
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

    @Override
    public void onPrivatesClick() {
        if (vatView == null)
            return;

        hasPrivates = !hasPrivates;
        vatView.setPrivatesStates(hasPrivates);
        vatView.setPrivatesFont(hasPrivates ? R.font.manrope_bold : R.font.manrope_medium);
        vatView.setRecyclerViewVisibility(hasPrivates ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPdfCompleted(String process) {
        sendInternalEmail(name, email, startingMonth, endingMonth, fileName);
    }
}