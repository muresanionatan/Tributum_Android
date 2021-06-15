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
import com.app.tributum.application.TributumApplication;
import com.app.tributum.fragment.invoices.listener.AsyncListener;
import com.app.tributum.listener.RequestSentListener;
import com.app.tributum.listener.SignatureListener;
import com.app.tributum.model.ContractModel;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
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

    private int previousContractDateLength = 0;

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
            view.setSendDisabled();
        }
    }

    private void validateContinueButton(String name, String address, String birthday, String occupation, String phone, String email, String bankAccount) {
        if (arePersonalInfoAdded(name, address, birthday, occupation, phone, email, bankAccount)) {
            view.setSendEnabled();
        } else {
            view.setSendDisabled();
        }
    }

    private void validateEmployeeButton(String pps, String contractDate) {
        if (areEmploymentInfoAdded(pps, contractDate)) {
            view.setSendEnabled();
        } else {
            view.setSendDisabled();
        }
    }

    @Override
    public void beforeBirthdayChanged(int length) {
        previousBirthdayLength = length;
    }

    @Override
    public void afterBirthdayChanged(Editable s) {
        handleDates(s, previousBirthdayLength, true);
    }

    @Override
    public void beforeContractDateChanged(int length) {
        previousContractDateLength = length;
    }

    @Override
    public void afterContractDateChanged(Editable s) {
        handleDates(s, previousContractDateLength, false);
    }

    @Override
    public void checkPersonalValidation(String name, String address, String birthday, String occupation, String phone, String email, String bankAccount,
                                        String pps, String contractDate) {
        if (state == ProgressState.PERSONAL)
            validateContinueButton(name, address, birthday, occupation, phone, email, bankAccount);
        else if (state == ProgressState.EMPLOYMENT)
            validateEmployeeButton(pps, contractDate);
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
    public void handleSendButtonClick(String name, String address, String birthday, String occupation, String phone, String email, String bankAccount,
                                      String pps, String contractDate, String noOfKids, String otherText) {
        if (view == null)
            return;

        if (state == ProgressState.PERSONAL) {
            if (arePersonalInfoAdded(name, address, email, birthday, occupation, phone, bankAccount)) {
                if (!ValidationUtils.isEmailValid(email)) {
                    view.showToast(resources.getString(R.string.please_enter_correct_email));
                } else if (birthday.length() < 10) {
                    view.showToast(resources.getString(R.string.please_enter_birthday_format));
                } else {
                    moveToEmploymentScreen();
                }
            } else {
                view.showToast(resources.getString(R.string.add_all_info));
            }
        } else if (state == ProgressState.EMPLOYMENT) {
            if (areEmploymentInfoAdded(pps, contractDate)) {
                if (!ValidationUtils.isPpsValid(pps)) {
                    view.showToast(resources.getString(R.string.please_enter_pps));
                } else if (contractDate.length() < 10) {
                    view.showToast(resources.getString(R.string.please_enter_contract_format));
                } else {
                    moveToSignatureScreen();
                }
            } else {
                view.showToast(resources.getString(R.string.add_all_info_to_continue));
            }
        } else if (state == ProgressState.SIGNATURE) {
            if (signatureFile != null)
                sendInfo(name, address, pps, email, contractDate, birthday, occupation, otherText, phone, bankAccount, noOfKids);
        }
    }

    private void handleDates(Editable s, int previousValue, boolean isBirthday) {
        if (view == null)
            return;

        if (s.length() > previousValue) {
            if (s.length() == 2) {
                try {
                    if (Integer.parseInt(s.toString()) > 31) {
                        if (isBirthday)
                            view.setBirthdayText("31/");
                        else
                            view.setContractDateText("31/");
                    } else {
                        if (isBirthday)
                            view.setBirthdayText(s + "/");
                        else
                            view.setContractDateText(s + "/");
                    }
                } catch (NumberFormatException e) {
                    int currentDay = java.util.Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    String day = String.valueOf(currentDay);
                    if (currentDay < 10) {
                        day = "0" + day;
                    }
                    if (isBirthday)
                        view.setBirthdayText(day + "/");
                    else
                        view.setContractDateText(day + "/");
                }
                if (isBirthday)
                    view.moveBirthdayCursorToEnd();
                else
                    view.moveContractCursorToEnd();
            } else if (s.length() == 3) {
                String birthday = String.valueOf(s);
                if (!birthday.endsWith("/")) {
                    String string = birthday.substring(0, birthday.length() - 1) + "/" + birthday.substring(birthday.length() - 1);
                    if (isBirthday)
                        view.setBirthdayText(string);
                    else
                        view.setContractDateText(string);
                }
                if (isBirthday)
                    view.moveBirthdayCursorToEnd();
                else
                    view.moveContractCursorToEnd();
            } else if (s.length() == 4) {
                int month;
                try {
                    month = Integer.parseInt(s.toString().substring(s.toString().length() - 1));
                    if (month > 1) {
                        String string = s.toString().substring(0, 3) + "0" + month + "/";
                        if (isBirthday)
                            view.setBirthdayText(string);
                        else
                            view.setContractDateText(string);
                    }
                } catch (NumberFormatException e) {
                    int currentMonth = java.util.Calendar.getInstance().get(Calendar.MONTH) + 1;
                    String monthString = String.valueOf(currentMonth);
                    if (currentMonth < 10) {
                        monthString = "0" + monthString;
                    }
                    String string = s.toString().substring(0, 3) + monthString + "/";
                    if (isBirthday)
                        view.setBirthdayText(string);
                    else
                        view.setContractDateText(string);
                }
                if (isBirthday)
                    view.moveBirthdayCursorToEnd();
                else
                    view.moveContractCursorToEnd();
            } else if (s.length() == 5) {
                String string = s.toString();
                string = string.substring(3);
                try {
                    if (Integer.parseInt(string) > 12) {
                        String firstString = s.toString();
                        if (isBirthday)
                            view.setBirthdayText(firstString.substring(0, 3) + "12/");
                        else
                            view.setContractDateText(firstString.substring(0, 3) + "12/");
                    } else {
                        if (isBirthday)
                            view.setBirthdayText(s + "/");
                        else
                            view.setContractDateText(s + "/");
                    }
                } catch (NumberFormatException e) {
                    int c = s.toString().charAt(3) - '0';
                    if (c == 1) {
                        if (isBirthday)
                            view.setBirthdayText(s.toString().substring(0, 3) + "01/");
                        else
                            view.setContractDateText(s.toString().substring(0, 3) + "01/");
                    } else {
                        int currentMonth = java.util.Calendar.getInstance().get(Calendar.MONTH) + 1;
                        String month = String.valueOf(currentMonth);
                        if (currentMonth < 10) {
                            month = "0" + month;
                        }
                        String string1 = s.toString().substring(0, 3) + month + "/";
                        if (isBirthday)
                            view.setBirthdayText(string1);
                        else
                            view.setContractDateText(string1);
                    }
                }
                if (isBirthday)
                    view.moveBirthdayCursorToEnd();
                else
                    view.moveContractCursorToEnd();
            } else if (s.length() == 6) {
                String birthday = String.valueOf(s);
                if (!birthday.endsWith("/")) {
                    String string = birthday.substring(0, birthday.length() - 1) + "/" + birthday.substring(birthday.length() - 1);
                    if (isBirthday)
                        view.setBirthdayText(string);
                    else
                        view.setContractDateText(string);
                }
                if (isBirthday)
                    view.moveBirthdayCursorToEnd();
                else
                    view.moveContractCursorToEnd();
            } else if (s.length() == 10) {
                String string = s.toString();
                string = string.substring(6);
                int currentYear = java.util.Calendar.getInstance().get(Calendar.YEAR);
                try {
                    if (Integer.parseInt(string) > currentYear) {
                        String firstString = s.toString();
                        if (isBirthday)
                            view.setBirthdayText(firstString.substring(0, 6) + currentYear);
                        else
                            view.setContractDateText(firstString.substring(0, 6) + currentYear);
                    }
                } catch (NumberFormatException nfe) {
                    String string1 = s.toString().substring(0, 6) + currentYear;
                    if (isBirthday)
                        view.setBirthdayText(string1);
                    else
                        view.setContractDateText(string1);
                }
                if (isBirthday)
                    view.moveBirthdayCursorToEnd();
                else
                    view.moveContractCursorToEnd();
            }
        }
    }

    private boolean arePersonalInfoAdded(String name, String address, String email, String birthday, String occupation,
                                         String phone, String bankAccount) {
        if (!name.equals("")
                && !address.equals("")
                && !birthday.equals("")
                && !occupation.equals("")
                && !phone.equals("")
                && !email.equals("")
                && !bankAccount.equals("")
                && idFile != null) {
            if (maritalStatus == MaritalStatus.MARRIED) {
                return marriageCertificateFile != null;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean areEmploymentInfoAdded(String pps, String contractDate) {
        if (!pps.equals("")
                && !contractDate.equals("")
                && ppsFileFront != null) {
            if (isSelf) {
                return first || second || third || fourth || fifth || sixth || seventh || eight || ninth;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private void moveToEmploymentScreen() {
        state = ProgressState.EMPLOYMENT;
        view.hidePersonalInfoLayout();
        view.showEmploymentInfoLayoutFromRight();
        view.setTitle(resources.getString(R.string.employment_info_label));
        view.setSubtitle(resources.getString(R.string.contract_subtitle));
        view.setConfirmationButtonText(resources.getString(R.string.continue_label));
    }

    private void moveToSignatureScreen() {
        state = ProgressState.SIGNATURE;
        view.hideEmploymentInfoLayoutToLeft();
        view.showSignatureLayout();
        view.setTitle(resources.getString(R.string.almost_done));
        view.setSubtitle(resources.getString(R.string.add_your_signature_below));
        view.setConfirmationButtonText(resources.getString(R.string.sign_send));
        if (signatureFile == null)
            view.setSendDisabled();
        else
            view.setSendEnabled();
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
            view.setTitle(resources.getString(R.string.employment_info_label));
            view.setConfirmationButtonText(resources.getString(R.string.continue_label));
            view.setSubtitle(resources.getString(R.string.contract_subtitle));
            view.setSendEnabled();
        } else if (state == ProgressState.EMPLOYMENT) {
            state = ProgressState.PERSONAL;
            view.hideEmploymentInfoLayoutToRight();
            view.showPersonalInfoLayout();
            view.setTitle(resources.getString(R.string.personal_info_label));
            view.setSendEnabled();
        } else if (state == ProgressState.PERSONAL) {
            view.closeActivity();
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
        if (ppsFileFront == null && view != null)
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_FRONT,
                    ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_PPS_FRONT,
                    ConstantsUtils.CAMERA_REQUEST_PPS_FRONT,
                    ConstantsUtils.MULTIPLE_PERMISSIONS_PPS_FRONT);
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
        if (ppsFileBack == null && view != null)
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_BACK,
                    ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_PPS_BACK,
                    ConstantsUtils.CAMERA_REQUEST_PPS_BACK,
                    ConstantsUtils.MULTIPLE_PERMISSIONS_PPS_BACK);
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
        if (idFile == null && view != null)
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_ID,
                    ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_ID,
                    ConstantsUtils.CAMERA_REQUEST_ID,
                    ConstantsUtils.MULTIPLE_PERMISSIONS_ID);
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
        if (marriageCertificateFile == null && view != null)
            view.showFileChooser(ConstantsUtils.SELECTED_PICTURE_REQUEST_MARRIAGE,
                    ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_MARRIAGE,
                    ConstantsUtils.CAMERA_REQUEST_MARRIAGE,
                    ConstantsUtils.MULTIPLE_PERMISSIONS_MARRIAGE);
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
    public void onAddFromGalleryClicked(int requestCode, int storagePermissionId) {
        pickPictureFromGallery(requestCode, storagePermissionId);
        view.hideBottomSheet();
    }

    @Override
    public void onTakePhotoClicked(String name, int requestId, int permissionId) {
        if (view == null)
            return;

        if (checkPermissions(permissionId)) {
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
    private void pickPictureFromGallery(int requestId, int permissionId) {
        if (view == null)
            return;

        if (Build.VERSION.SDK_INT >= 23) {
            if (view.hasStoragePermission())
                view.openFilePicker(requestId);
            else
                view.requestOnePermission(permissionId);
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
        else if (requestId == ConstantsUtils.CAMERA_REQUEST_ID)
            permission = ConstantsUtils.MULTIPLE_PERMISSIONS_ID;
        else
            permission = ConstantsUtils.MULTIPLE_PERMISSIONS_MARRIAGE;

        if (!listPermissionsNeeded.isEmpty()) {
            view.requestListOfPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), permission);
            return false;
        }
        return true;
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
    public void onRequestPermissionResult(String name, int requestCode, int[] grantResults) {
        switch (requestCode) {
            case ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_PPS_FRONT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    view.openFilePicker(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_FRONT);
                break;
            case ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_PPS_BACK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    view.openFilePicker(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_BACK);
                break;
            case ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_ID:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    view.openFilePicker(ConstantsUtils.SELECTED_PICTURE_REQUEST_ID);
                break;
            case ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_MARRIAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    view.openFilePicker(ConstantsUtils.SELECTED_PICTURE_REQUEST_MARRIAGE);
                break;
            case ConstantsUtils.MULTIPLE_PERMISSIONS_PPS_FRONT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    onTakePhotoClicked(name, ConstantsUtils.CAMERA_REQUEST_PPS_FRONT, ConstantsUtils.MULTIPLE_PERMISSIONS_PPS_FRONT);
                break;
            case ConstantsUtils.MULTIPLE_PERMISSIONS_PPS_BACK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    onTakePhotoClicked(name, ConstantsUtils.CAMERA_REQUEST_PPS_BACK, ConstantsUtils.MULTIPLE_PERMISSIONS_PPS_BACK);
                break;
            case ConstantsUtils.MULTIPLE_PERMISSIONS_ID:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    onTakePhotoClicked(name, ConstantsUtils.CAMERA_REQUEST_ID, ConstantsUtils.MULTIPLE_PERMISSIONS_ID);
                break;
            case ConstantsUtils.MULTIPLE_PERMISSIONS_MARRIAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    onTakePhotoClicked(name, ConstantsUtils.CAMERA_REQUEST_MARRIAGE, ConstantsUtils.MULTIPLE_PERMISSIONS_MARRIAGE);
                break;
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
    public void onContractDateSet(int year, int monthOfYear, int dayOfMonth) {
        if (view == null)
            return;

        String dayString = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10)
            dayString = "0" + dayOfMonth;
        monthOfYear = monthOfYear + 1;
        String monthString = String.valueOf(monthOfYear);
        if (monthOfYear < 10)
            monthString = "0" + monthOfYear;
        view.setContractDateText(dayString + "/" + monthString + "/" + year);
    }
}