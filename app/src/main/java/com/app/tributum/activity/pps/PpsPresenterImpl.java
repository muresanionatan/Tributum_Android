package com.app.tributum.activity.pps;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.activity.contract.PhotoCrop;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.AsyncListener;
import com.app.tributum.listener.RequestSentListener;
import com.app.tributum.model.EmailBody;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.ImageUtils;
import com.app.tributum.utils.UploadAsyncTask;
import com.app.tributum.utils.ValidationUtils;
import com.app.tributum.utils.ui.FileUtils;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PpsPresenterImpl implements PpsPresenter, AsyncListener, RequestSentListener {

    private String billFile;

    private String letterFile;

    private String idFile;

    private int previousEircodeLength = 0;

    private File file;

    private PpsModel ppsModel;

    @PhotoCrop
    private int photoState;

    private final PpsView view;

    private final Resources resources;

    private boolean isPreview;

    private String filePath;

    PpsPresenterImpl(PpsView contractView) {
        this.view = contractView;
        this.resources = TributumApplication.getInstance().getResources();
    }

    @Override
    public void beforeEircodeChanged(int length) {
        previousEircodeLength = length;
    }

    @Override
    public void afterEircodeChanged(Editable s) {
        if (view == null)
            return;

        if (s.length() > previousEircodeLength) {
            if (s.length() == 3) {
                view.setEircodeText(s + " ");
                view.moveEircodeCursorToEnd(4);
            } else if (s.length() == 4) {
                String string = s.toString();
                if (!string.startsWith(" ", 3)) {
                    char lastChar = string.charAt(3);
                    view.setEircodeText(string.substring(0, 3) + " " + lastChar);
                    view.moveEircodeCursorToEnd(5);
                }
            }
        }
    }

    @Override
    public void handleSendButtonClick(String firstName, String lastName, String momString, String address, String eircode,
                                      String email, String phone, String ownerPhone) {
        if (view == null)
            return;

        if (firstName.equals("")) {
            view.showToast(R.string.please_enter_first_name);
            view.focusOnFirstName();
        } else if (lastName.equals("")) {
            view.showToast(R.string.please_enter_last_name);
            view.focusOnLastName();
        } else if (momString.equals("")) {
            view.showToast(R.string.please_enter_mom_name);
            view.focusOnMomName();
        } else if (address.equals("")) {
            view.showToast(R.string.please_enter_address);
            view.focusOnAddress();
        } else if (!TextUtils.isEmpty(eircode) && !ValidationUtils.isEircodeValid(eircode)) {
            view.showToast(R.string.please_enter_correct_eircode);
            view.focusOnEircode();
        } else if (phone.equals("")) {
            view.showToast(R.string.please_enter_phone);
            view.focusOnPhone();
        } else if (!ValidationUtils.isEmailValid(email)) {
            view.showToast(R.string.please_enter_correct_email);
            view.focusOnEmail();
        } else if (idFile == null) {
            view.showToast(R.string.please_add_id);
            view.scrollToId();
        } else if (billFile == null) {
            view.showToast(R.string.please_add_bill);
            view.scrollToBill();
        } else if (letterFile == null) {
            view.showToast(R.string.please_add_letter);
            view.scrollToLetter();
        } else {
            sendInfo(firstName, lastName, momString, address, eircode, email, phone, ownerPhone);
        }
    }

    @Override
    public void onBackPressed() {
        if (view == null)
            return;

        if (isPreview) {
            isPreview = false;
            view.closePreview();
        } else {
            view.closeActivity();
        }
    }

    @Override
    public void onFileChooserTopClicked() {
        if (view != null)
            view.hideBottomSheet();
    }

    @Override
    public void onBillClicked() {
        if (billFile == null && view != null) {
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_BILL,
                    ConstantsUtils.CAMERA_REQUEST_BILL);
            view.removeFocus();
        }
    }

    @Override
    public void onBillPreviewClicked() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(billFile);
    }

    @Override
    public void onBillRemoveClicked() {
        if (view != null)
            view.resetBillLayout();
        billFile = null;
    }

    @Override
    public void onLetterClicked() {
        if (letterFile == null && view != null) {
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_LETTER,
                    ConstantsUtils.CAMERA_REQUEST_LETTER);
            view.removeFocus();
        }
    }

    @Override
    public void onLetterPreviewClicked() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(letterFile);
    }

    @Override
    public void onLetterRemoveClicked() {
        if (view != null)
            view.resetLetterLayout();
        letterFile = null;
    }

    @Override
    public void onIdClicked() {
        if (idFile == null && view != null) {
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_ID,
                    ConstantsUtils.CAMERA_REQUEST_ID);
            view.removeFocus();
        }
    }

    @Override
    public void onIdPreviewClicked() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(idFile);
    }

    @Override
    public void onIdRemoveClicked() {
        if (view != null)
            view.resetIdLayout();
        idFile = null;
    }

    @Override
    public void onBottomSheetExpanded() {
        if (view != null)
            view.setFileChooserToVisible();
    }

    @Override
    public void onAddFromGalleryClicked(int requestCode) {
        pickPictureFromGallery(requestCode);
        view.hideBottomSheet();
    }

    @Override
    public void onTakePhotoClicked(String name, int requestId) {
        if (view == null)
            return;

        file = new File(ImageUtils.getImagePath(name + requestId));
        view.takePicture(requestId, file, filePath);
        view.hideBottomSheet();
    }

    @Override
    public void onRemovePhotoClicked(String fileName) {
        if (fileName == null)
            return;

        if (fileName.equals(billFile)) {
            view.resetBillLayout();
            billFile = null;
        } else if (fileName.equals(letterFile)) {
            view.resetLetterLayout();
            letterFile = null;
        } else {
            view.resetIdLayout();
            idFile = null;
        }

        isPreview = false;
        view.closePreview();
    }

    private void sendInfo(String firstName, String lastName, String momName, String address, String eircode,
                          String email, String phone, String ownerPhone) {
        ppsModel = new PpsModel(
                firstName + " " + lastName,
                momName,
                address + " " + eircode,
                email,
                phone,
                ownerPhone);

        ppsModel.setMessage(resources.getString(R.string.contract_mail_message));

        view.showLoadingScreen();

        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.TRIBUTUM_EMAIL, generateInternalEmailMessage(firstName + " " + lastName)));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (!response.isSuccessful()) {
                    view.showToast(R.string.something_went_wrong);
                } else {
                    sendClientMail(email, ppsModel.getMessage());
                    try {
                        uploadFiles(firstName + " " + lastName, momName, address + ", eircode:" + eircode, email, phone + ", owner phone: " + ownerPhone);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                view.hideLoadingScreen();
                view.showToast(R.string.something_went_wrong);
            }
        });
    }

    private void sendClientMail(String email, String message) {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(email, message));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
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

    private void uploadFiles(String name, String mom, String address, String email, String phone) throws IOException {
        ppsModel.setEmail(email);
        Map<String, String> uploadList = new HashMap<>();
        uploadList.put("BILL", billFile.replace("file://", ""));
        uploadList.put("LETTER", letterFile.replace("file://", ""));
        uploadList.put("ID", idFile.replace("file://", ""));
        UploadAsyncTask uploadMultipleFilesTask = new UploadAsyncTask(
                name,
                uploadList,
                null,
                UploadAsyncTask.UploadType.MULTIPLE,
                "PPS");
        uploadMultipleFilesTask.execute();
        UploadAsyncTask uploadOneFileTask = new UploadAsyncTask(
                name,
                FileUtils.createFile(generateUserInfo(name, mom, address, email, phone), name + "_info"),
                this,
                UploadAsyncTask.UploadType.USER_INFO,
                "PPS");
        uploadOneFileTask.execute();
    }

    private String generateUserInfo(String name, String mom, String address, String email, String phone) {
        return "Name: " + name
                + "\nMom's name before marriage: " + mom
                + "\nAddress: " + address
                + "\nmail: " + email
                + "\nPhone: " + phone;
    }

    private String generateInternalEmailMessage(String name) {
        String formattedString = name.toUpperCase();
        formattedString = formattedString.replaceAll(" ", "%20");
        return resources.getString(R.string.pps_message_email) + name
                + "\n\n" + "Click on below link to access files\n\n"
                + "https://www.dropbox.com/home/Apps/Tributum/PPS/"
                + formattedString;
    }

    private void pickPictureFromGallery(int requestId) {
        if (view != null)
            view.openFilePicker(requestId);
    }

    @Override
    public void onTaskCompleted() {
        if (view != null) {
            view.hideLoadingScreen();
            view.showRequestSentScreen();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (view == null)
            return;

        switch (requestCode) {
            case ConstantsUtils.SELECTED_PICTURE_REQUEST_BILL:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.BILL_SELECT;
                    billFile = ImageUtils.getFilePathFromGallery(data);
                    view.startCrop(data.getData());
                }
                break;
            case ConstantsUtils.SELECTED_PICTURE_REQUEST_LETTER:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.LETTER_SELECT;
                    letterFile = ImageUtils.getFilePathFromGallery(data);
                    view.startCrop(data.getData());
                }
                break;
            case ConstantsUtils.SELECTED_PICTURE_REQUEST_ID:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.ID_SELECT;
                    idFile = ImageUtils.getFilePathFromGallery(data);
                    view.startCrop(data.getData());
                }
                break;
            case ConstantsUtils.CAMERA_REQUEST_BILL:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.BILL_CAMERA;
                    billFile = file.getAbsolutePath();
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAMERA_REQUEST_LETTER:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.LETTER_CAMERA;
                    letterFile = file.getAbsolutePath();
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAMERA_REQUEST_ID:
                if (resultCode == Activity.RESULT_OK && file != null) {
                    photoState = PhotoCrop.ID_CAMERA;
                    idFile = file.getAbsolutePath();
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                handleCropping(data);
                break;
            default:
                break;
        }
    }

    private void handleCropping(Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);

        switch (photoState) {
            case PhotoCrop.ID_CAMERA:
            case PhotoCrop.ID_SELECT:
                if (result != null) {
                    idFile = result.getUri().toString();
                    view.setIdImage(idFile);
                } else {
                    idFile = null;
                }
                break;
            case PhotoCrop.BILL_CAMERA:
            case PhotoCrop.BILL_SELECT:
                if (result != null) {
                    billFile = result.getUri().toString();
                    view.setBillImage(billFile);
                } else {
                    billFile = null;
                }
                break;
            case PhotoCrop.LETTER_CAMERA:
            case PhotoCrop.LETTER_SELECT:
                if (result != null) {
                    letterFile = result.getUri().toString();
                    view.setLetterImage(letterFile);
                } else {
                    letterFile = null;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void setFilePath(String pictureImagePath) {
        filePath = pictureImagePath;
    }

    @Override
    public void onOkClicked() {
        if (view != null)
            view.closeActivity();
    }

    @Override
    public void onDestroy() {
        deleteImage(idFile);
        deleteImage(billFile);
        deleteImage(letterFile);
    }

    private void deleteImage(String file) {
        if (file == null)
            return;

        File fileToDelete = new File(file);
        if (fileToDelete.exists())
            fileToDelete.delete();
    }
}