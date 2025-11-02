package com.app.tributum.activity.contract;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.activity.contract.model.ContractModel;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.AsyncListener;
import com.app.tributum.listener.RequestSentListener;
import com.app.tributum.listener.SignatureListener;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.utils.CalendarUtils;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.ImageUtils;
import com.app.tributum.utils.UploadAsyncTask;
import com.app.tributum.utils.ValidationUtils;
import com.app.tributum.utils.ui.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ContractPresenterImpl implements ContractPresenter, SignatureListener, AsyncListener, RequestSentListener {

    private Bitmap signatureFile;

    private String ppsFileFront;

    private String ppsFileBack;

    private String idFile;

    private int previousBirthdayLength = 0;

    private int previousEircodeLength = 0;

    private File file;

    private ContractModel contractModel;

    private String marriageCertificateFile;

    private boolean isSelf = true;

    private boolean isEmployee;

    @PhotoCrop
    private int photoState;

    @ProgressState
    private int state = ProgressState.PERSONAL;

    @ProgressState
    private int maritalStatus = MaritalStatus.SINGLE;

    private final ContractView view;

    private final Resources resources;

    private boolean first;

    private boolean second;

    private boolean third;

    private boolean fourth;

    private boolean sixth;

    private boolean isPreview;

    private String filePath;

    private boolean signatureDrawn;

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
    public void beforeBirthdayChanged(int length) {
        previousBirthdayLength = length;
    }

    @Override
    public void afterBirthdayChanged(Editable s) {
        handleDates(s, previousBirthdayLength);
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
    public void onClearSignatureClick() {
        if (view == null)
            return;
        view.clearSignature();
        view.hideClearButton();
        signatureFile = null;
        signatureDrawn = false;
    }

    @Override
    public void handleSendButtonClick(String firstName, String lastName, String address1, String address2, String address3, String eircode,
                                      String birthday, String occupation, String phone, String email, String bankAccount,
                                      String pps, String noOfKids) {
        if (view == null)
            return;

        if (state == ProgressState.PERSONAL) {
            if (firstName.isEmpty()) {
                view.showToast(R.string.please_enter_first_name);
                view.focusOnFirstName();
            } else if (lastName.isEmpty()) {
                view.showToast(R.string.please_enter_last_name);
                view.focusOnLastName();
            } else if (address1.isEmpty()) {
                view.showToast(R.string.please_enter_address1);
                view.focusOnAddress1();
            } else if (address2.isEmpty()) {
                view.showToast(R.string.please_enter_address2);
                view.focusOnAddress2();
            } else if (!TextUtils.isEmpty(eircode) && !ValidationUtils.isEircodeValid(eircode)) {
                view.showToast(R.string.please_enter_correct_eircode);
                view.focusOnEircode();
            } else if (birthday.isEmpty() || birthday.length() < 10) {
                view.showToast(R.string.please_enter_birthday_format);
                view.focusOnBirthday();
            } else if (occupation.isEmpty()) {
                view.showToast(R.string.please_enter_occupation);
                view.focusOnOccupation();
            } else if (phone.isEmpty()) {
                view.showToast(R.string.please_enter_phone);
                view.focusOnPhone();
            } else if (!ValidationUtils.isEmailValid(email)) {
                view.showToast(R.string.please_enter_correct_email);
                view.focusOnEmail();
            } else if (idFile == null) {
                view.showToast(R.string.please_add_id);
                view.scrollToId();
            } else if (maritalStatus == MaritalStatus.MARRIED && marriageCertificateFile == null) {
                view.showToast(R.string.please_add_marriage);
            } else {
                moveToEmploymentScreen();
            }
        } else if (state == ProgressState.EMPLOYMENT) {
            if (!ValidationUtils.isPpsValid(pps)) {
                view.showToast(R.string.please_enter_pps);
                view.focusOnPps();
            } else if (isSelf && (!first && !second && !third && !fourth && !sixth)) {
                view.showToast(R.string.please_select_taxes);
                view.scrollToTaxes();
            } else if (ppsFileFront == null) {
                view.showToast(R.string.please_add_pps_front);
                view.scrollToPpsFront();
            } else {
                moveToSignatureScreen();
            }
        } else if (state == ProgressState.SIGNATURE) {
            if (signatureDrawn)
                sendInfo(firstName, lastName, address1, address2, address3, eircode, pps, email, birthday, occupation, phone, bankAccount, noOfKids);
            else
                view.showToast(R.string.please_add_signature);
        }
    }

    private void handleDates(Editable s, int previousValue) {
        if (view == null)
            return;

        if (s.length() > previousValue) {
            if (s.length() == 2) {
                try {
                    if (Integer.parseInt(s.toString()) > 31) {
                        view.setBirthdayText("31/");
                    } else {
                        view.setBirthdayText(s + "/");
                    }
                } catch (NumberFormatException e) {
                    int currentDay = java.util.Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    String day = String.valueOf(currentDay);
                    if (currentDay < 10) {
                        day = "0" + day;
                    }
                    view.setBirthdayText(day + "/");
                }
                view.moveBirthdayCursorToEnd();
            } else if (s.length() == 3) {
                String birthday = String.valueOf(s);
                if (!birthday.endsWith("/")) {
                    String string = birthday.substring(0, birthday.length() - 1) + "/" + birthday.substring(birthday.length() - 1);
                    view.setBirthdayText(string);
                }
                view.moveBirthdayCursorToEnd();
            } else if (s.length() == 4) {
                int month;
                try {
                    month = Integer.parseInt(s.toString().substring(s.toString().length() - 1));
                    if (month > 1) {
                        String string = s.toString().substring(0, 3) + "0" + month + "/";
                        view.setBirthdayText(string);
                    }
                } catch (NumberFormatException e) {
                    int currentMonth = java.util.Calendar.getInstance().get(Calendar.MONTH) + 1;
                    String monthString = String.valueOf(currentMonth);
                    if (currentMonth < 10) {
                        monthString = "0" + monthString;
                    }
                    String string = s.toString().substring(0, 3) + monthString + "/";
                    view.setBirthdayText(string);
                }
                view.moveBirthdayCursorToEnd();
            } else if (s.length() == 5) {
                String string = s.toString();
                string = string.substring(3);
                try {
                    if (Integer.parseInt(string) > 12) {
                        String firstString = s.toString();
                        view.setBirthdayText(firstString.substring(0, 3) + "12/");
                    } else {
                        view.setBirthdayText(s + "/");
                    }
                } catch (NumberFormatException e) {
                    int c = s.toString().charAt(3) - '0';
                    if (c == 1) {
                        view.setBirthdayText(s.toString().substring(0, 3) + "01/");
                    } else {
                        int currentMonth = java.util.Calendar.getInstance().get(Calendar.MONTH) + 1;
                        String month = String.valueOf(currentMonth);
                        if (currentMonth < 10) {
                            month = "0" + month;
                        }
                        String string1 = s.toString().substring(0, 3) + month + "/";
                        view.setBirthdayText(string1);
                    }
                }
                view.moveBirthdayCursorToEnd();
            } else if (s.length() == 6) {
                String birthday = String.valueOf(s);
                if (!birthday.endsWith("/")) {
                    String string = birthday.substring(0, birthday.length() - 1) + "/" + birthday.substring(birthday.length() - 1);
                    view.setBirthdayText(string);
                }
                view.moveBirthdayCursorToEnd();
            } else if (s.length() == 10) {
                String string = s.toString();
                string = string.substring(6);
                int currentYear = java.util.Calendar.getInstance().get(Calendar.YEAR);
                try {
                    if (Integer.parseInt(string) > currentYear) {
                        String firstString = s.toString();
                        view.setBirthdayText(firstString.substring(0, 6) + currentYear);
                    }
                } catch (NumberFormatException nfe) {
                    String string1 = s.toString().substring(0, 6) + currentYear;
                    view.setBirthdayText(string1);
                }
                view.moveBirthdayCursorToEnd();
            }
        }
    }

    private void moveToEmploymentScreen() {
        state = ProgressState.EMPLOYMENT;
        view.hidePersonalInfoLayout();
        view.showEmploymentInfoLayoutFromRight();
        view.setTitle(R.string.employment_info_label);
        view.setSubtitle(R.string.contract_subtitle);
        view.setConfirmationButtonText(R.string.continue_label);
    }

    private void moveToSignatureScreen() {
        state = ProgressState.SIGNATURE;
        view.hideEmploymentInfoLayoutToLeft();
        view.showSignatureLayout();
        view.setTitle(R.string.almost_done);
        view.setSubtitle(R.string.add_your_signature_below);
        view.hideAsteriskView();
        view.setConfirmationButtonText(R.string.sign_send);
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
            view.showEmploymentInfoLayoutFromLeft();
            view.setTitle(R.string.employment_info_label);
            view.setConfirmationButtonText(R.string.continue_label);
            view.setSubtitle(R.string.contract_subtitle);
            view.showAsteriskView();
        } else if (state == ProgressState.EMPLOYMENT) {
            state = ProgressState.PERSONAL;
            view.hideEmploymentInfoLayoutToRight();
            view.showPersonalInfoLayout();
            view.setTitle(R.string.personal_info_label);
        } else if (state == ProgressState.PERSONAL) {
            view.closeActivity();
        }
    }

    @Override
    public void onDrawingStarted() {
        if (view != null) {
            view.showClearButton();
            signatureDrawn = true;
        }
    }

    @Override
    public void onFileChooserTopClicked() {
        if (view != null)
            view.hideBottomSheet();
    }

    @Override
    public void onPpsFrontClicked() {
        if (ppsFileFront == null && view != null) {
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_FRONT,
                    ConstantsUtils.CAMERA_REQUEST_PPS_FRONT);
            view.removeFocus();
        }
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
        if (ppsFileBack == null && view != null) {
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_BACK,
                    ConstantsUtils.CAMERA_REQUEST_PPS_BACK);
            view.removeFocus();
        }
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
    public void onMarriageCertificateClicked() {
        if (marriageCertificateFile == null && view != null) {
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_MARRIAGE,
                    ConstantsUtils.CAMERA_REQUEST_MARRIAGE);
            view.removeFocus();
        }
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

        file = new File(ImageUtils.getImagePath(name + requestId));
        view.takePicture(requestId, file, filePath);
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

    private void sendInfo(String firstName, String lastName, String address1, String address2, String address3, String eircode,
                          String pps, String email, String birthday, String occupation,
                          String phone, String bankAccount, String noOfKids) {
        contractModel = new ContractModel(
                firstName + " " + lastName,
                address1 + " " + address2 + " " + address3 + " " + eircode,
                pps,
                email,
                CalendarUtils.getCurrentDay(),
                birthday,
                "Android");

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
        if (sixth)
            taxes.add(resources.getString(R.string.relevant_contract_label));

        contractModel.setOccupation(occupation);
        contractModel.setMessage(resources.getString(R.string.contract_mail_message));
        contractModel.setStartingDate(new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));

        saveSignatureImage();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        signatureFile.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

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
                    view.showToast(R.string.something_went_wrong);
                } else {
                    //upload PPS and ID to Dropbox
                    try {
                        uploadFiles(firstName + " " + lastName, phone, bankAccount, noOfKids, email);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                if (t.getMessage() != null && t.getMessage().contains("timeout")) {
                    try {
                        uploadFiles(firstName + " " + lastName, phone, bankAccount, noOfKids, email);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    view.hideLoadingScreen();
                    view.showToast(R.string.something_went_wrong);
                }
            }
        });
    }

    private void uploadFiles(String name, String phone, String bankAccount, String noOfKids, String email) throws IOException {
        contractModel.setEmail(email);
        Map<String, String> uploadList = new HashMap<>();
        uploadList.put("PPS_FRONT", ppsFileFront.replace("file://", ""));
        if (ppsFileBack != null)
            uploadList.put("PPS_BACK", ppsFileBack.replace("file://", ""));
        uploadList.put("ID", idFile.replace("file://", ""));
        if (marriageCertificateFile != null)
            uploadList.put("MARRIAGE", marriageCertificateFile.replace("file://", ""));
        UploadAsyncTask uploadMultipleFilesTask = new UploadAsyncTask(
                name,
                uploadList,
                null,
                UploadAsyncTask.UploadType.MULTIPLE,
                "DATABASE");
        uploadMultipleFilesTask.execute();
        UploadAsyncTask uploadOneFileTask = new UploadAsyncTask(
                name,
                FileUtils.createFile(generateUserInfo(phone, bankAccount, noOfKids), name + "_info"),
                this,
                UploadAsyncTask.UploadType.USER_INFO,
                "DATABASE");
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
                + "\nContract date: " + contractModel.getDate()
                + "\nStarting date: " + contractModel.getStartingDate();
        return message;
    }

    private void pickPictureFromGallery(int requestId) {
        if (view == null)
            return;

        if (requestId == ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_FRONT)
            view.pickPpsFront();
        else if (requestId == ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_BACK)
            view.pickPpsBack();
        else if (requestId == ConstantsUtils.SELECTED_PICTURE_REQUEST_ID)
            view.pickId();
        else if (requestId == ConstantsUtils.SELECTED_PICTURE_REQUEST_MARRIAGE)
            view.pickMarriage();
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
    public void onTaskCompleted(String process) {
        if (view != null) {
            view.hideLoadingScreen();
            view.showRequestSentScreen();
        }
    }

    @Override
    public void onPpsFrontSelected(Uri uri) {
        photoState = PhotoCrop.PPS_SELECT;
        ppsFileFront = uri.getPath();
        view.startCrop(uri);
    }

    @Override
    public void onPpsBackSelected(Uri uri) {
        photoState = PhotoCrop.BACK_SELECT;
        ppsFileBack = uri.getPath();
        view.startCrop(uri);
    }

    @Override
    public void onIdSelected(Uri uri) {
        photoState = PhotoCrop.ID_SELECT;
        idFile = uri.getPath();
        view.startCrop(uri);
    }

    @Override
    public void onMarriageSelected(Uri uri) {
        photoState = PhotoCrop.MARRIAGE_SELECT;
        marriageCertificateFile = uri.getPath();
        view.startCrop(uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (view == null)
            return;

        switch (requestCode) {
            case ConstantsUtils.CAMERA_REQUEST_PPS_FRONT:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.PPS_CAMERA;
                    ppsFileFront = file.getAbsolutePath();
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAMERA_REQUEST_PPS_BACK:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.BACK_CAMERA;
                    ppsFileBack = file.getAbsolutePath();
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
            case ConstantsUtils.CAMERA_REQUEST_MARRIAGE:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.MARRIAGE_CAMERA;
                    marriageCertificateFile = file.getAbsolutePath();
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void handleCropping(String result) {
        switch (photoState) {
            case PhotoCrop.ID_CAMERA:
            case PhotoCrop.ID_SELECT:
                if (result != null) {
                    idFile = result;
                    view.setIdImage(idFile);
                } else {
                    idFile = null;
                }
                break;
            case PhotoCrop.MARRIAGE_CAMERA:
            case PhotoCrop.MARRIAGE_SELECT:
                if (result != null) {
                    marriageCertificateFile = result;
                    view.setMarriageCertificateImage(marriageCertificateFile);
                } else {
                    marriageCertificateFile = null;
                }
                break;
            case PhotoCrop.PPS_CAMERA:
            case PhotoCrop.PPS_SELECT:
                if (result != null) {
                    ppsFileFront = result;
                    view.setPpsFrontImage(ppsFileFront);
                } else {
                    ppsFileFront = null;
                }
                break;
            case PhotoCrop.BACK_CAMERA:
            case PhotoCrop.BACK_SELECT:
                if (result != null) {
                    ppsFileBack = result;
                    view.setPpsBackImage(ppsFileBack);
                } else {
                    ppsFileBack = null;
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
    public void onBirthdayDateSet(int year, int monthOfYear, int dayOfMonth) {
        if (view == null)
            return;

        String dayString = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10)
            dayString = "0" + dayOfMonth;
        monthOfYear = monthOfYear + 1;
        String monthString = String.valueOf(monthOfYear);
        if (monthOfYear < 10)
            monthString = "0" + monthOfYear;
        view.setBirthdayText(dayString + "/" + monthString + "/" + year);
    }

    @Override
    public void onDestroy() {
        deleteImage(idFile);
        deleteImage(marriageCertificateFile);
        deleteImage(ppsFileFront);
        deleteImage(ppsFileBack);
    }

    private void deleteImage(String file) {
        if (file == null)
            return;

        File fileToDelete = new File(file);
        if (fileToDelete.exists())
            fileToDelete.delete();
    }
}