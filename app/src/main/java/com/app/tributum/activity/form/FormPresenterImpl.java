package com.app.tributum.activity.form;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.MotionEvent;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.activity.form.adapter.FormAdapterState;
import com.app.tributum.activity.vat.model.VatModel;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.AsyncListener;
import com.app.tributum.listener.CombinePdfListener;
import com.app.tributum.listener.FormItemClickListener;
import com.app.tributum.listener.InvoicesDeleteListener;
import com.app.tributum.listener.RequestSentListener;
import com.app.tributum.model.EmailBody;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.thread.CombinePhotosInPdfTask;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.UploadAsyncTask;
import com.app.tributum.utils.ui.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FormPresenterImpl implements FormPresenter, RequestSentListener, InvoicesDeleteListener, FormItemClickListener,
        AsyncListener, CombinePdfListener {

    final private FormView view;

    private boolean isBottomSheetVisible;

    private List<VatModel> bankList;
    private List<VatModel> kidsList;
    private List<VatModel> expencesList;
    private List<VatModel> medicalList;

    private int state;
    private String pictureImagePath;

    private String rentFile;
    private String rtbFile;
    private String marriageFile;
    private String fisc1File;
    private String fisc2File;
    private String fullName;
    private String email;
    private String year;
    private String rent;
    private File bankPdfFile;
    private File kidsPdfFile;
    private File expensesPdfFile;
    private File medicalPdfFile;
    private File rentPdfFile;
    private File rtbPdfFile;
    private File marriagePdfFile;
    private File fisc1PdfFile;
    private File fisc2PdfFile;

    public FormPresenterImpl(FormView view) {
        this.view = view;
    }

    private void collapseBottomSheet() {
        isBottomSheetVisible = false;
        view.collapseBottomSheet();
    }

    @Override
    public void onBackPressed() {
        if (view == null)
            return;

        if (isBottomSheetVisible) {
            collapseBottomSheet();
        } else {
            view.closeActivity();
        }
    }

    @Override
    public void onCreate() {
        bankList = new ArrayList<>();
        bankList.add(new VatModel(""));

        kidsList = new ArrayList<>();
        kidsList.add(new VatModel(""));

        expencesList = new ArrayList<>();
        expencesList.add(new VatModel(""));

        medicalList = new ArrayList<>();
        medicalList.add(new VatModel(""));
    }

    @Override
    public void setFilePath(String pictureImagePath) {
        this.pictureImagePath = pictureImagePath;
    }

    @Override
    public void onBankYesClick() {
        view.showRentLayout();
    }

    @Override
    public void onBankNoClick() {
        view.hideRentLayout();
    }

    @Override
    public void onMarriageYesClick() {
        view.showMarriageLayout();
    }

    @Override
    public void onMarriageNoClick() {
        view.hideMarriageLayout();
    }

    @Override
    public void onExpensesYesClick() {
        view.showExpensesLayout();
    }

    @Override
    public void onExpensesNoClick() {
        view.hideExpensesLayout();
    }

    @Override
    public void onMedicalYesClick() {
        view.showMedicalLayout();
    }

    @Override
    public void onMedicalNoClick() {
        view.hideMedicalLayout();
    }

    @Override
    public void onOkClicked() {
        if (view != null)
            view.closeActivity();
    }

    @Override
    public List<VatModel> getBsList() {
        return bankList;
    }

    @Override
    public List<VatModel> getChildList() {
        return kidsList;
    }

    @Override
    public List<VatModel> getExpensesList() {
        return expencesList;
    }

    @Override
    public List<VatModel> getMedicalList() {
        return medicalList;
    }

    @Override
    public String[] getFormYears() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return new String[]{String.valueOf(currentYear - 5),
                String.valueOf(currentYear - 4),
                String.valueOf(currentYear - 3),
                String.valueOf(currentYear - 2),
                String.valueOf(currentYear - 1),
                String.valueOf(currentYear)};
    }

    @Override
    public void onBottomSheetExpanded() {
        if (view != null)
            view.showTopViewBottomSheet();
    }

    @Override
    public boolean onRecyclerViewTouch(MotionEvent event) {
        if (view != null && event != null && event.getAction() == MotionEvent.ACTION_MOVE) {
            view.hideKeyboard();
        }
        return false;
    }

    @Override
    public void onTopViewClick() {
        if (view != null) {
            collapseBottomSheet();
        }
    }

    @Override
    public void onPreviewPhotoClick(String filePath, int photoIndex, boolean arePrivates) {

    }

    @Override
    public void onPlusCLick(int state) {
        if (view != null) {
            isBottomSheetVisible = true;
            this.state = state;
            view.showBottomSheet();
        }
    }

    @Override
    public void onDeleteClick(String filePath, int photoIndex, int state) {
        if (view != null) {
            removeItemFromList(photoIndex, state);
        }
    }

    private void removeItemFromList(int photoClicked, int state) {
        if (state == FormAdapterState.BANK)
            view.removeItemFromBank(photoClicked);
        else if (state == FormAdapterState.KIDS)
            view.removeItemFromKids(photoClicked);
        else if (state == FormAdapterState.EXPENSES)
            view.removeItemFromExpenses(photoClicked);
        else if (state == FormAdapterState.MEDICAL)
            view.removeItemFromMedical(photoClicked);
    }

    @Override
    public void clearList() {

    }

    @Override
    public void onTakePhotoClick() {
        collapseBottomSheet();
        int requestCode;
        if (state == FormAdapterState.BANK)
            requestCode = ConstantsUtils.CAMERA_REQUEST_BANK_ID;
        else if (state == FormAdapterState.KIDS)
            requestCode = ConstantsUtils.CAMERA_REQUEST_KIDS_ID;
        else if (state == FormAdapterState.EXPENSES)
            requestCode = ConstantsUtils.CAMERA_REQUEST_EXPENSES_ID;
        else if (state == FormAdapterState.MEDICAL)
            requestCode = ConstantsUtils.CAMERA_REQUEST_MEDICAL_ID;
        else if (state == FormState.RENT)
            requestCode = ConstantsUtils.CAMERA_REQUEST_RENT_ID;
        else if (state == FormState.RTB)
            requestCode = ConstantsUtils.CAMERA_REQUEST_RTB_ID;
        else if (state == FormState.MARRIAGE)
            requestCode = ConstantsUtils.CAMERA_REQUEST_MARRIAGE_ID;
        else if (state == FormState.FISC_1)
            requestCode = ConstantsUtils.CAMERA_REQUEST_FISC_1_ID;
        else
            requestCode = ConstantsUtils.CAMERA_REQUEST_FISC_2_ID;
        if (view != null)
            view.takePhoto(pictureImagePath, requestCode);
    }

    @Override
    public void onAddFromGalleryClick() {
        pickPictureFromGallery();
        view.hideBottomSheet();
    }

    private void pickPictureFromGallery() {
        if (view == null)
            return;

        if (state == FormAdapterState.BANK)
            view.pickBank();
        else if (state == FormAdapterState.KIDS)
            view.pickKids();
        else if (state == FormAdapterState.EXPENSES)
            view.pickExpenses();
        else if (state == FormAdapterState.MEDICAL)
            view.pickMedical();
        else if (state == FormState.RENT)
            view.pickRent();
        else if (state == FormState.RTB)
            view.pickRtb();
        else if (state == FormState.MARRIAGE)
            view.pickMarriage();
        else if (state == FormState.FISC_1)
            view.pickFisc1();
        else
            view.pickFisc2();
    }

    @Override
    public void onAddPdfClick() {
        if (view != null)
            view.openPdfIntent();
    }

    @Override
    public void handlePdfSelected(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                Uri pdfUri = data.getData();

                // Copy PDF to internal storage on background thread
                new Thread(() -> {
                    try {
                        File pdfFile = copyPdfToInternalStorage(pdfUri);
                        if (pdfFile != null && pdfFile.exists()) {
                            // Store the PDF file based on current state
                            // Update UI on main thread
                            if (view != null) {
                                ((Activity) view).runOnUiThread(() -> {
                                    if (state == FormAdapterState.BANK)
                                        bankPdfFile = pdfFile;
                                    else if (state == FormAdapterState.KIDS)
                                        kidsPdfFile = pdfFile;
                                    else if (state == FormAdapterState.EXPENSES)
                                        expensesPdfFile = pdfFile;
                                    else if (state == FormAdapterState.MEDICAL)
                                        medicalPdfFile = pdfFile;
                                    else if (state == FormState.RENT) {
                                        rentPdfFile = pdfFile;
                                        view.setPdfDefaultImage(R.id.rent_id);
                                    } else if (state == FormState.RTB) {
                                        rtbPdfFile = pdfFile;
                                        view.setPdfDefaultImage(R.id.rtb_id);
                                    } else if (state == FormState.MARRIAGE) {
                                        marriagePdfFile = pdfFile;
                                        view.setPdfDefaultImage(R.id.marriage_id);
                                    } else if (state == FormState.FISC_1) {
                                        fisc1PdfFile = pdfFile;
                                        view.setPdfDefaultImage(R.id.fisc_1_id);
                                    } else if (state == FormState.FISC_2) {
                                        fisc2PdfFile = pdfFile;
                                        view.setPdfDefaultImage(R.id.fisc_2_id);
                                    }

                                    view.hideBottomSheet();
                                });
                            }
                        } else {
                            if (view != null) {
                                ((Activity) view).runOnUiThread(() ->
                                        view.showToast(R.string.something_went_wrong)
                                );
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (view != null) {
                            ((Activity) view).runOnUiThread(() ->
                                    view.showToast(R.string.something_went_wrong)
                            );
                        }
                    }
                }).start();
            }
        }
    }

    private File copyPdfToInternalStorage(Uri pdfUri) {
        try {
            // Get the filename
            String fileName = getFileName(pdfUri);
            if (fileName == null) {
                fileName = "document_" + System.currentTimeMillis() + ".pdf";
            }

            // Create directory in internal storage
            File pdfDir = new File(TributumApplication.getInstance().getFilesDir(), "pdfs");
            if (!pdfDir.exists()) {
                pdfDir.mkdirs();
            }

            // Create the output file
            File outputFile = new File(pdfDir, fileName);

            // Copy content from URI to file using try-with-resources
            try (java.io.InputStream inputStream = TributumApplication.getInstance()
                    .getContentResolver().openInputStream(pdfUri);
                 java.io.FileOutputStream outputStream = new java.io.FileOutputStream(outputFile)) {

                if (inputStream == null) {
                    return null;
                }

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            return outputFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = TributumApplication.getInstance().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    public void onSendClick(String fullName, String email, String year, String rent) {
        if (fullName.isEmpty())
            view.showToast(R.string.please_enter_full_name);
        else if (email.isEmpty())
            view.showToast(R.string.please_enter_correct_email);
        else if (rentFile != null && rent.isEmpty())
            view.showToast(R.string.please_enter_rent);
        else if (bankList.isEmpty())
            view.showToast(R.string.please_add_bank_statement);
        else
            handleSendButtonClick(fullName, email, year, rent);
    }

    private void handleSendButtonClick(String name, String email, String year, String rent) {
        fullName = name;
        this.email = email;
        this.year = year;
        this.rent = rent;
        view.hideKeyboard();
        view.showLoadingScreen();
        CombinePhotosInPdfTask combinePhotosInPdfTask = new CombinePhotosInPdfTask(this, bankList, fullName, "bs", bankPdfFile);
        combinePhotosInPdfTask.execute();
    }

    private void uploadSeparateFiles() {
        Map<String, String> uploadList = new HashMap<>();
        if (rentFile != null)
            uploadList.put("RENT", rentFile.replace("file://", ""));
        if (rtbFile != null)
            uploadList.put("RTB", rtbFile.replace("file://", ""));
        if (marriageFile != null)
            uploadList.put("MARRIAGE", marriageFile.replace("file://", ""));
        if (fisc1File != null)
            uploadList.put("FISC1", fisc1File.replace("file://", ""));
        if (fisc2File != null)
            uploadList.put("FISC2", fisc2File.replace("file://", ""));

        UploadAsyncTask uploadMultipleFilesTask = new UploadAsyncTask(
                fullName,
                uploadList,
                null,
                UploadAsyncTask.UploadType.MULTIPLE,
                "FORM11");
        uploadMultipleFilesTask.setProcess("uploadPdfs");
        uploadMultipleFilesTask.execute();
    }

    private String generateUserInfo(String name, String year, String rent) {
        return "Name: " + name
                + "\nClose year: " + year
                + "\nRent paid: " + rent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (view == null)
            return;

        if (requestCode == ConstantsUtils.CAMERA_REQUEST_BANK_ID && resultCode == Activity.RESULT_OK) {
            view.addItemToBankList(new VatModel(pictureImagePath));
            onTakePhotoClick();
        } else if (requestCode == ConstantsUtils.CAMERA_REQUEST_KIDS_ID && resultCode == Activity.RESULT_OK) {
            view.addItemToKidsList(new VatModel(pictureImagePath));
            onTakePhotoClick();
        } else if (requestCode == ConstantsUtils.CAMERA_REQUEST_EXPENSES_ID && resultCode == Activity.RESULT_OK) {
            view.addItemToExpensesList(new VatModel(pictureImagePath));
            onTakePhotoClick();
        } else if (requestCode == ConstantsUtils.CAMERA_REQUEST_RENT_ID && resultCode == Activity.RESULT_OK) {
            view.setImage(pictureImagePath, R.id.rent_id);
            rentFile = pictureImagePath;
        } else if (requestCode == ConstantsUtils.CAMERA_REQUEST_RTB_ID && resultCode == Activity.RESULT_OK) {
            view.setImage(pictureImagePath, R.id.rtb_id);
            rtbFile = pictureImagePath;
        } else if (requestCode == ConstantsUtils.CAMERA_REQUEST_MARRIAGE_ID && resultCode == Activity.RESULT_OK) {
            view.setImage(pictureImagePath, R.id.marriage_id);
            marriageFile = pictureImagePath;
        } else if (requestCode == ConstantsUtils.CAMERA_REQUEST_FISC_1_ID && resultCode == Activity.RESULT_OK) {
            view.setImage(pictureImagePath, R.id.fisc_1_id);
            fisc1File = pictureImagePath;
        } else if (requestCode == ConstantsUtils.CAMERA_REQUEST_FISC_2_ID && resultCode == Activity.RESULT_OK) {
            view.setImage(pictureImagePath, R.id.fisc_2_id);
            fisc2File = pictureImagePath;
        }
    }

    @Override
    public void onBankSelected(List<Uri> uris) {
        for (int i = 0; i < uris.size(); i++) {
            view.addItemToBankList(new VatModel(FileUtils.getPath(uris.get(i))));
        }
    }

    @Override
    public void onKidsSelected(List<Uri> uris) {
        for (int i = 0; i < uris.size(); i++) {
            view.addItemToKidsList(new VatModel(FileUtils.getPath(uris.get(i))));
        }
    }

    @Override
    public void onExpensesSelected(List<Uri> uris) {
        for (int i = 0; i < uris.size(); i++) {
            view.addItemToExpensesList(new VatModel(FileUtils.getPath(uris.get(i))));
        }
    }

    @Override
    public void onMedicalSelected(List<Uri> uris) {
        for (int i = 0; i < uris.size(); i++) {
            view.addItemToMedicalList(new VatModel(FileUtils.getPath(uris.get(i))));
        }
    }

    @Override
    public void onRentSelected(Uri uri) {
        view.startCrop(uri);
    }

    @Override
    public void handleCropping(String uriFilePath) {
        switch (state) {
            case FormState.RENT:
                if (uriFilePath != null)
                    view.setImage(uriFilePath, R.id.rent_id);
                rentFile = uriFilePath;
                break;
            case FormState.RTB:
                if (uriFilePath != null)
                    view.setImage(uriFilePath, R.id.rtb_id);
                rtbFile = uriFilePath;
                break;
            case FormState.MARRIAGE:
                if (uriFilePath != null)
                    view.setImage(uriFilePath, R.id.marriage_id);
                marriageFile = uriFilePath;
                break;
            case FormState.FISC_1:
                if (uriFilePath != null)
                    view.setImage(uriFilePath, R.id.fisc_1_id);
                fisc1File = uriFilePath;
                break;
            case FormState.FISC_2:
                if (uriFilePath != null)
                    view.setImage(uriFilePath, R.id.fisc_2_id);
                fisc2File = uriFilePath;
                break;
        }
    }

    @Override
    public void onRtbSelected(Uri uri) {
        view.startCrop(uri);
    }

    @Override
    public void onMarriageSelected(Uri uri) {
        view.startCrop(uri);
    }

    @Override
    public void onFisc1Selected(Uri uri) {
        view.startCrop(uri);
    }

    @Override
    public void onFisc2Selected(Uri uri) {
        view.startCrop(uri);
    }

    @Override
    public void onTaskCompleted(String process) {
        if (process.equals("uploadPdfs")) {
            Map<String, File> uploadList = new HashMap<>();
            if (rentPdfFile != null)
                uploadList.put("RENT", rentPdfFile);
            if (rtbPdfFile != null)
                uploadList.put("RTB", rtbPdfFile);
            if (marriagePdfFile != null)
                uploadList.put("MARRIAGE", marriagePdfFile);
            if (fisc1PdfFile != null)
                uploadList.put("FISC1", fisc1PdfFile);
            if (fisc2PdfFile != null)
                uploadList.put("FISC2", fisc2PdfFile);

            UploadAsyncTask uploadMultipleFilesTask = new UploadAsyncTask(
                    fullName,
                    uploadList,
                    null,
                    UploadAsyncTask.UploadType.PDFS);
            uploadMultipleFilesTask.setProcess("userInfo");
            uploadMultipleFilesTask.execute();
        } else if (process.equals("userInfo")) {
            UploadAsyncTask uploadOneFileTask = new UploadAsyncTask(
                    fullName,
                    FileUtils.createFile(generateUserInfo(fullName, year, rent), fullName + "_info"),
                    this,
                    UploadAsyncTask.UploadType.USER_INFO,
                    "FORM11");
            uploadOneFileTask.execute();
        } else {
            sendEmails();
        }
    }

    private void sendEmails() {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.TRIBUTUM_EMAIL, generateInternalEmailMessage(fullName), "Android"));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (!response.isSuccessful()) {
                    view.showToast(R.string.something_went_wrong);
                } else {
                    sendClientMail();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                view.hideLoadingScreen();
                view.showToast(R.string.something_went_wrong);
            }
        });
    }

    private void sendClientMail() {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(email, TributumApplication.getInstance().getString(R.string.contract_mail_message), "Android"));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    view.hideLoadingScreen();
                    view.showRequestSentScreen();
                } else {
                    view.showToast(R.string.something_went_wrong);
                }

                view.hideLoadingScreen();
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                view.hideLoadingScreen();
                view.showToast(R.string.something_went_wrong);
            }
        });
    }

    private String generateInternalEmailMessage(String name) {
        String formattedString = name.toUpperCase();
        formattedString = formattedString.replaceAll(" ", "%20");
        return "Please close the year for " + name
                + "\n\n" + "Click on below link to access files\n\n"
                + "https://www.dropbox.com/home/Apps/Tributum/FORM11/"
                + formattedString;
    }

    @Override
    public void onPdfCompleted(String process) {
        if (process.equals("bs") && kidsList.size() > 1) {
            CombinePhotosInPdfTask combinePhotosInPdfTask = new CombinePhotosInPdfTask(this, kidsList, fullName, "kids", kidsPdfFile);
            combinePhotosInPdfTask.execute();
        } else if (process.equals("kids") && expencesList.size() > 1) {
            CombinePhotosInPdfTask combinePhotosInPdfTask = new CombinePhotosInPdfTask(this, expencesList, fullName, "expenses", expensesPdfFile);
            combinePhotosInPdfTask.execute();
        } else if (process.equals("expenses") && medicalList.size() > 1) {
            CombinePhotosInPdfTask combinePhotosInPdfTask = new CombinePhotosInPdfTask(this, medicalList, fullName, "medical", medicalPdfFile);
            combinePhotosInPdfTask.execute();
        } else {
            uploadSeparateFiles();
        }
    }
}