package com.app.tributum.activity.newcontract;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.os.Build;
import android.text.Editable;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.fragment.invoices.listener.AsyncListener;
import com.app.tributum.listener.SignatureListener;
import com.app.tributum.model.ContractModel;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.ImageUtils;
import com.app.tributum.utils.UploadAsyncTask;
import com.app.tributum.utils.ui.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ContractPresenterImpl implements ContractPresenter, SignatureListener, AsyncListener {

    private Bitmap signatureFile;

    private String ppsFileFront;

    private String ppsFileBack;

    private String idFile;

    private int previousLength = 0;

    private File file;

    private ContractModel contractModel;

    private String marriageCertificateFile;

    private boolean isSelf = true;

    private boolean isEmployee;

    @ProgressState
    private int state = ProgressState.PERSONAL;

    @ProgressState
    private int maritalStatus = MaritalStatus.SINGLE;

    private final ContractView view;

    private final Resources resources;

    private boolean hasOther;

    private boolean first;

    private boolean second;

    private boolean third;

    private boolean fourth;

    private boolean fifth;

    private boolean sixth;

    private boolean seventh;

    private boolean eight;

    private boolean ninth;

    private boolean isPreview;

    private String filePath;

    ContractPresenterImpl(ContractView contractView) {
        this.view = contractView;
        this.resources = TributumApplication.getInstance().getResources();
    }

    @Override
    public void onCreate() {
        if (view != null) {
            view.selectSingle();
            view.selectText(R.id.self_employed_text_id);
        }
    }

    @Override
    public void onFormStarted(String name) {
        if (!name.equals("")
                && !TributumAppHelper.getBooleanSetting(AppKeysValues.CONTRACT_FORM_STARTED)) {
            TributumAppHelper.saveSetting(AppKeysValues.CONTRACT_FORM_STARTED, AppKeysValues.TRUE);
        }
    }

    @Override
    public void beforeBirthdayChanged(int length) {
        previousLength = length;
    }

    @Override
    public void afterBirthdayChanged(Editable s) {
        if (view == null)
            return;

//        try {
//            Integer.parseInt(s.toString());
//        } catch (NumberFormatException ex) {
//            view.showToast("use only numbers");
//            String birthday = String.valueOf(s);
//            view.setBirthdayText(birthday.substring(0, birthday.length() - 1));
//            view.moveBirthdayCursorToEnd();
//        return;
//        }

        if (s.length() > previousLength) {
            if (s.length() == 2) {
                if (Integer.parseInt(s.toString()) > 31) {
                    view.setBirthdayText("31/");
                } else {
                    view.setBirthdayText(s + "/");
                }
                view.moveBirthdayCursorToEnd();
            } else if (s.length() == 3) {
                String birthday = String.valueOf(s);
                if (!birthday.endsWith("/")) {
                    view.setBirthdayText(birthday.substring(0, birthday.length() - 1) + "/" + birthday.substring(birthday.length() - 1));
                }
                view.moveBirthdayCursorToEnd();
            } else if (s.length() == 5) {
                String string = s.toString();
                string = string.substring(3);
                if (Integer.parseInt(string) > 12) {
                    String firstString = s.toString();
                    view.setBirthdayText(firstString.substring(0, 3) + "12/");
                } else {
                    view.setBirthdayText(s + "/");
                }
                view.moveBirthdayCursorToEnd();
            } else if (s.length() == 6) {
                String birthday = String.valueOf(s);
                if (!birthday.endsWith("/")) {
                    view.setBirthdayText(birthday.substring(0, birthday.length() - 1) + "/" + birthday.substring(birthday.length() - 1));
                }
                view.moveBirthdayCursorToEnd();
            } else if (s.length() == 10) {
                String string = s.toString();
                string = string.substring(6);
                int currentYear = java.util.Calendar.getInstance().get(Calendar.YEAR);
                if (Integer.parseInt(string) > currentYear) {
                    String firstString = s.toString();
                    view.setBirthdayText(firstString.substring(0, 6) + currentYear);
                }
                view.moveBirthdayCursorToEnd();
            }
        }
    }

    @Override
    public void afterContractDateChanged(Editable s) {
        if (view == null)
            return;

        if (s.length() > previousLength) {
            if (s.length() == 2) {
                if (Integer.parseInt(s.toString()) > 31) {
                    view.setContractDateText("31/");
                } else {
                    view.setContractDateText(s + "/");
                }
                view.moveContractCursorToEnd();
            } else if (s.length() == 5) {
                String string = s.toString();
                string = string.substring(3);
                if (Integer.parseInt(string) > 12) {
                    String firstString = s.toString();
                    view.setContractDateText(firstString.substring(0, 3) + "12/");
                } else {
                    view.setContractDateText(s + "/");
                }
                view.moveContractCursorToEnd();
            } else if (s.length() == 10) {
                String string = s.toString();
                string = string.substring(6);
                int currentYear = java.util.Calendar.getInstance().get(Calendar.YEAR);
                if (Integer.parseInt(string) > currentYear) {
                    String firstString = s.toString();
                    view.setContractDateText(firstString.substring(0, 6) + currentYear);
                }
                view.moveContractCursorToEnd();
            }
        }
    }

    @Override
    public void onSingleClicked() {
        if (view == null)
            return;

        if (maritalStatus == MaritalStatus.MARRIED)
            view.hideMaritalLayout();
        maritalStatus = MaritalStatus.SINGLE;
        view.selectSingle();
        view.changeSingleState(true);
        view.changeMarriedState(false);
        view.changeDivorcedState(false);
        view.changeCohabitingState(false);
    }

    @Override
    public void onMarriedClicked() {
        if (view == null)
            return;
        maritalStatus = MaritalStatus.MARRIED;
        view.selectMarriage();
        view.changeSingleState(false);
        view.changeMarriedState(true);
        view.changeDivorcedState(false);
        view.changeCohabitingState(false);
        view.showMaritalLayout();
    }

    @Override
    public void onDivorcedClick() {
        if (view == null)
            return;

        if (maritalStatus == MaritalStatus.MARRIED)
            view.hideMaritalLayout();
        maritalStatus = MaritalStatus.DIVORCED;
        view.selectDivorced();
        view.changeSingleState(false);
        view.changeMarriedState(false);
        view.changeDivorcedState(true);
        view.changeCohabitingState(false);
    }

    @Override
    public void onCohabitingClicked() {
        if (view == null)
            return;

        if (maritalStatus == MaritalStatus.MARRIED)
            view.hideMaritalLayout();
        maritalStatus = MaritalStatus.COHABITING;
        view.selectCohabiting();
        view.changeSingleState(false);
        view.changeMarriedState(false);
        view.changeDivorcedState(false);
        view.changeCohabitingState(true);
    }

    @Override
    public void onSelfEmployeeClick() {
        isSelf = !isSelf;
        if (isSelf) {
            view.showTaxesView();
            view.selectText(R.id.self_employed_text_id);
        } else {
            view.deselectText(R.id.self_employed_text_id);
            view.hideTaxesView();
            if (!isEmployee) {
                isEmployee = true;
                view.changeEmployeeState(true);
                view.selectText(R.id.employee_text_id);
            }
        }

        view.changeSelfEmployedState(isSelf);
    }

    @Override
    public void onEmployeeClick() {
        if (view != null) {
            isEmployee = !isEmployee;
            view.changeEmployeeState(isEmployee);
            if (!isEmployee) {
                view.deselectText(R.id.employee_text_id);
                if (!isSelf) {
                    isSelf = true;
                    view.showTaxesView();
                    view.changeSelfEmployedState(true);
                    view.selectText(R.id.self_employed_text_id);
                    view.deselectText(R.id.employee_text_id);
                }
            } else {
                view.selectText(R.id.employee_text_id);
            }
        }
    }

    @Override
    public void beforeOtherChanged() {
        if (!hasOther)
            hasOther = true;
    }

    @Override
    public void onClearSignatureClick() {
        if (view == null)
            return;
        view.clearSignature();
        view.hideClearButton();
        view.setSendDisabled();
        signatureFile = null;
    }

    @Override
    public void handleSendButtonClick(String name, String address, String pps, String email, String contractDate, String birthday, String occupation, String otherText,
                                      String phone, String bankAccount, String noOfKids) {
        if (view == null)
            return;

        if (state == ProgressState.PERSONAL) {
            state = ProgressState.EMPLOYMENT;
            view.hidePersonalInfoLayout();
            view.showEmploymentInfoLayout();
            view.setTitle(resources.getString(R.string.employment_info_label));
            view.setConfirmationButtonText(resources.getString(R.string.continue_label));
        } else if (state == ProgressState.EMPLOYMENT) {
            state = ProgressState.SIGNATURE;
            view.hideEmploymentInfoLayout();
            view.showSignatureLayout();
            view.setTitle(resources.getString(R.string.almost_done));
            view.setSubtitle(resources.getString(R.string.add_your_signature_below));
            view.setConfirmationButtonText(resources.getString(R.string.sign_send));
            view.setSendDisabled();
        } else if (state == ProgressState.SIGNATURE) {
            if (signatureFile != null)
                sendInfo(name, address, pps, email, contractDate, birthday, occupation, otherText, phone, bankAccount, noOfKids);
        }
    }

    @Override
    public void onBackPressed() {
        if (view == null)
            return;
        if (isPreview) {
            isPreview = false;
            view.closePreview();
        } else if (state == ProgressState.SIGNATURE) {
            state = ProgressState.EMPLOYMENT;
            view.hideSignatureLayout();
            view.showEmploymentInfoLayout();
            view.setTitle(resources.getString(R.string.employment_info_label));
            view.setConfirmationButtonText(resources.getString(R.string.continue_label));
            view.setSendEnabled();
        } else if (state == ProgressState.EMPLOYMENT) {
            state = ProgressState.PERSONAL;
            view.hideEmploymentInfoLayout();
            view.showPersonalInfoLayout();
            view.setTitle(resources.getString(R.string.personal_info_label));
        } else if (state == ProgressState.PERSONAL) {
            if (TributumAppHelper.getBooleanSetting(AppKeysValues.CONTRACT_FORM_STARTED)) {

            } else {
                view.closeActivity();
            }
        }
    }

    @Override
    public void onDrawingStarted() {
        if (view != null) {
            view.showClearButton();
            view.setSendEnabled();
        }
    }

    @Override
    public void onDrawingFinished() {
        saveSignatureImage();
    }

    @Override
    public void onFileChooserTopClicked() {
        if (view != null)
            view.hideBottomSheet();
    }

    @Override
    public void onPpsFrontClicked() {
        if (view != null)
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_FRONT, ConstantsUtils.CAMERA_REQUEST_PPS_FRONT);
    }

    @Override
    public void onPpsFrontPreviewClicked() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(ppsFileFront);
    }

    @Override
    public void onPpsFrontRemoveClicked() {
        if (view != null)
            view.resetPpsFrontLayout();
        ppsFileFront = null;
    }

    @Override
    public void onPpsBackClicked() {
        if (view != null)
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_BACK, ConstantsUtils.CAMERA_REQUEST_PPS_BACK);
    }

    @Override
    public void onPpsBackPreviewClicked() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(ppsFileBack);
    }

    @Override
    public void onPpsBackRemoveClicked() {
        if (view != null)
            view.resetPpsBackLayout();
        ppsFileBack = null;
    }

    @Override
    public void onIdClicked() {
        if (view != null)
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_ID, ConstantsUtils.CAMERA_REQUEST_ID);
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
    public void onMarriageCertificateClicked() {
        if (view != null)
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_MARRIAGE, ConstantsUtils.CAMERA_REQUEST_MARRIAGE);
    }

    @Override
    public void onMarriagePreviewClicked() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(marriageCertificateFile);
    }

    @Override
    public void onMarriageRemoveClicked() {
        if (view != null)
            view.resetMarriageCertificateLayout();
        marriageCertificateFile = null;
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

        if (checkPermissions(requestId)) {
            file = new File(ImageUtils.getImagePath(name + requestId));
            view.takePicture(requestId, file, filePath);
        }
        view.hideBottomSheet();
    }

    @Override
    public void onRemovePhotoClicked(String fileName) {
        if (fileName == null)
            return;

        if (fileName.equals(ppsFileFront)) {
            view.resetPpsFrontLayout();
            ppsFileFront = null;
        } else if (fileName.equals(ppsFileBack)) {
            view.resetPpsBackLayout();
            ppsFileBack = null;
        } else if (fileName.equals(idFile)) {
            view.resetIdLayout();
            idFile = null;
        } else {
            view.resetMarriageCertificateLayout();
            marriageCertificateFile = null;
        }

        isPreview = false;
        view.closePreview();
    }

    @Override
    public void onFirstCheckboxClicked() {
        if (view != null) {
            first = !first;
            view.setFirstCheckboxState(first);
            if (first)
                view.selectText(R.id.first_text_id);
            else
                view.deselectText(R.id.first_text_id);
        }
    }

    @Override
    public void onSecondCheckboxClicked() {
        if (view != null) {
            second = !second;
            view.setSecondCheckboxState(second);
            if (second)
                view.selectText(R.id.second_text_id);
            else
                view.deselectText(R.id.second_text_id);
        }
    }

    @Override
    public void onThirdCheckboxClicked() {
        if (view != null) {
            third = !third;
            view.setThirdCheckboxState(third);
            if (third)
                view.selectText(R.id.third_text_id);
            else
                view.deselectText(R.id.third_text_id);
        }
    }

    @Override
    public void onFourthCheckboxClicked() {
        if (view != null) {
            fourth = !fourth;
            view.setFourthCheckboxState(fourth);
            if (fourth)
                view.selectText(R.id.fourth_text_id);
            else
                view.deselectText(R.id.fourth_text_id);
        }
    }

    @Override
    public void onFifthCheckboxClicked() {
        if (view != null) {
            fifth = !fifth;
            view.setFifthCheckboxState(fifth);
            if (fifth)
                view.selectText(R.id.fifth_text_id);
            else
                view.deselectText(R.id.fifth_text_id);
        }
    }

    @Override
    public void onSixthCheckboxClicked() {
        if (view != null) {
            sixth = !sixth;
            view.setSixthCheckboxState(sixth);
            if (sixth)
                view.selectText(R.id.sixth_text_id);
            else
                view.deselectText(R.id.sixth_text_id);
        }
    }

    @Override
    public void onSeventhCheckboxClicked() {
        if (view != null) {
            seventh = !seventh;
            view.setSeventhCheckboxState(seventh);
            if (seventh)
                view.selectText(R.id.seventh_text_id);
            else
                view.deselectText(R.id.seventh_text_id);
        }
    }

    @Override
    public void onEightCheckboxClicked() {
        if (view != null) {
            eight = !eight;
            view.setEightCheckboxState(eight);
            if (eight)
                view.selectText(R.id.eight_text_id);
            else
                view.deselectText(R.id.eight_text_id);
        }
    }

    @Override
    public void onNinthCheckboxClicked() {
        if (view != null) {
            if (!ninth)
                view.setFocusOnOther();
            else
                view.hideOther();
            ninth = !ninth;
            view.setNinthCheckboxState(ninth);
            if (ninth)
                view.selectText(R.id.ninth_text_id);
            else
                view.deselectText(R.id.ninth_text_id);
        }
    }

    private void sendInfo(String name, String address, String pps, String email, String contractDate, String birthday, String occupation, String otherText,
                          String phone, String bankAccount, String noOfKids) {
        contractModel = new ContractModel(
                name,
                address,
                pps,
                email,
                contractDate,
                birthday);

        if (maritalStatus == MaritalStatus.SINGLE)
            contractModel.setMaritalStatus(resources.getString(R.string.single_label));
        else if (maritalStatus == MaritalStatus.MARRIED)
            contractModel.setMaritalStatus(resources.getString(R.string.married_label));
        else if (maritalStatus == MaritalStatus.DIVORCED)
            contractModel.setMaritalStatus(resources.getString(R.string.divorced_label));
        else if (maritalStatus == MaritalStatus.COHABITING)
            contractModel.setMaritalStatus(resources.getString(R.string.cohabiting_label));

        List<String> employmentList = new ArrayList<>();
        if (isSelf)
            employmentList.add(resources.getString(R.string.self_employed_label));
        if (isEmployee)
            employmentList.add(resources.getString(R.string.employee_label));
        contractModel.setEmployment(employmentList);

        List<String> taxes = new ArrayList<>();
        if (first)
            taxes.add(resources.getString(R.string.income_tax_label));
        if (second)
            taxes.add(resources.getString(R.string.corporation_tax_label));
        if (third)
            taxes.add(resources.getString(R.string.value_added_tax_label));
        if (fourth)
            taxes.add(resources.getString(R.string.employer_paye_label));
        if (fifth)
            taxes.add(resources.getString(R.string.capital_gains_label));
        if (sixth)
            taxes.add(resources.getString(R.string.relevant_contract_label));
        if (seventh)
            taxes.add(resources.getString(R.string.environment_levy_label));
        if (eight)
            taxes.add(resources.getString(R.string.divided_withholding_label));
        if (ninth)
            taxes.add(resources.getString(R.string.other_label));

        contractModel.setOccupation(occupation);
        contractModel.setMessage(resources.getString(R.string.contract_mail_message));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        signatureFile.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        contractModel.setOther(otherText);
        contractModel.setTaxes(taxes);
        contractModel.setSignature(byteArray);

        view.showLoadingScreen();

        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendContract(contractModel);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (!response.isSuccessful()) {
                    view.hideLoadingScreen();
                    view.showToast(resources.getString(R.string.something_went_wrong));
                }

                //upload PPS and ID to Dropbox
                try {
                    uploadFiles(name, phone, bankAccount, noOfKids);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                view.hideLoadingScreen();
                view.showToast(resources.getString(R.string.something_went_wrong));
            }
        });
    }

    private void uploadFiles(String name, String phone, String bankAccount, String noOfKids) throws IOException {
        Map<String, String> uploadList = new HashMap<>();
        uploadList.put("PPS_FRONT", ppsFileFront);
        uploadList.put("PPS_BACK", ppsFileBack);
        uploadList.put("ID", idFile);
        uploadList.put("MARRIAGE", marriageCertificateFile);
        UploadAsyncTask uploadMultipleFilesTask = new UploadAsyncTask(
                name,
                uploadList,
                null,
                UploadAsyncTask.UploadType.MULTIPLE);
        uploadMultipleFilesTask.execute();
        UploadAsyncTask uploadOneFileTask = new UploadAsyncTask(
                name,
                FileUtils.createFile(generateUserInfo(phone, bankAccount, noOfKids), name + "_info"),
                this,
                UploadAsyncTask.UploadType.ONE);
        uploadOneFileTask.execute();
    }

    private String generateUserInfo(String phone, String bankAccount, String noOfKids) {
        String message = "Name: " + contractModel.getName()
                + "\nAddress: " + contractModel.getAddress()
                + "\nPPS: " + contractModel.getPpsNumber()
                + "\nmail: " + contractModel.getEmail()
                + "\nOccupation: " + contractModel.getOccupation()
                + "\nPhone: " + phone
                + "\nIBAN: " + bankAccount
                + "\nMarital status: " + contractModel.getMaritalStatus();
        if (!noOfKids.equals(""))
            message = message + "\nNumber of kids: " + noOfKids;
        message = message + "\nDate of birth: " + contractModel.getBirthday()
                + "\nContract date: " + contractModel.getDate();
        return message;
    }

    @SuppressLint("ObsoleteSdkInt")
    private void pickPictureFromGallery(int requestId) {
        if (view == null)
            return;

        if (Build.VERSION.SDK_INT >= 23) {
            if (view.hasStoragePermission())
                view.openFilePicker(requestId);
            else
                view.requestOnePermission(requestId);
        }
    }

    private boolean checkPermissions(int requestId) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : ConstantsUtils.PERMISSIONS) {
            result = view.hasPermission(permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        int permission;
        if (requestId == ConstantsUtils.CAMERA_REQUEST_PPS_FRONT)
            permission = ConstantsUtils.MULTIPLE_PERMISSIONS_PPS_FRONT;
        else if (requestId == ConstantsUtils.CAMERA_REQUEST_PPS_BACK)
            permission = ConstantsUtils.MULTIPLE_PERMISSIONS_PPS_BACK;
        else
            permission = ConstantsUtils.MULTIPLE_PERMISSIONS_ID;

        if (!listPermissionsNeeded.isEmpty()) {
            view.requestListOfPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), permission);
            return false;
        }
        return true;
    }

    private void clearFormStarted() {
        if (TributumAppHelper.getBooleanSetting(AppKeysValues.CONTRACT_FORM_STARTED)) {
            TributumAppHelper.saveSetting(AppKeysValues.CONTRACT_FORM_STARTED, AppKeysValues.FALSE);
        }
    }

    private void saveSignatureImage() {
        if (view == null)
            return;
        view.setDrawingCacheEnabled();
        signatureFile = view.getSignatureFile();
        File signature = new File(TributumApplication.getInstance().getFilesDir(), "/signature.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(signature);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        signatureFile.compress(Bitmap.CompressFormat.PNG, 95, fos);
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

        if ((requestCode == ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_FRONT
                || requestCode == ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_BACK
                || requestCode == ConstantsUtils.SELECTED_PICTURE_REQUEST_ID
                || requestCode == ConstantsUtils.SELECTED_PICTURE_REQUEST_MARRIAGE)
                && data == null)
            return;

        switch (requestCode) {
            case ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_FRONT:
                if (resultCode == Activity.RESULT_OK) {
                    ppsFileFront = ImageUtils.getFilePathFromGallery(data);
                    view.setPpsFrontImage(ppsFileFront);
                }
                break;
            case ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_BACK:
                if (resultCode == Activity.RESULT_OK) {
                    ppsFileBack = ImageUtils.getFilePathFromGallery(data);
                    view.setPpsBackImage(ppsFileBack);
                }
                break;
            case ConstantsUtils.SELECTED_PICTURE_REQUEST_ID:
                if (resultCode == Activity.RESULT_OK) {
                    idFile = ImageUtils.getFilePathFromGallery(data);
                    view.setIdImage(idFile);
                }
                break;
            case ConstantsUtils.SELECTED_PICTURE_REQUEST_MARRIAGE:
                if (resultCode == Activity.RESULT_OK) {
                    marriageCertificateFile = ImageUtils.getFilePathFromGallery(data);
                    view.setMarriageCertificateImage(marriageCertificateFile);
                }
                break;
            case ConstantsUtils.CAMERA_REQUEST_PPS_FRONT:
                if (resultCode == Activity.RESULT_OK) {
                    ppsFileFront = file.getAbsolutePath();
                    view.setPpsFrontImage(ppsFileFront);
                }
                break;
            case ConstantsUtils.CAMERA_REQUEST_PPS_BACK:
                if (resultCode == Activity.RESULT_OK) {
                    ppsFileBack = file.getAbsolutePath();
                    view.setPpsBackImage(ppsFileBack);
                }
                break;
            case ConstantsUtils.CAMERA_REQUEST_ID:
                if (resultCode == Activity.RESULT_OK) {
                    idFile = file.getAbsolutePath();
                    view.setIdImage(idFile);
                }
                break;
            case ConstantsUtils.CAMERA_REQUEST_MARRIAGE:
                if (resultCode == Activity.RESULT_OK) {
                    marriageCertificateFile = file.getAbsolutePath();
                    view.setMarriageCertificateImage(marriageCertificateFile);
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
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void onDestroy() {
        clearFormStarted();
    }
}