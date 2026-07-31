package com.app.tributum.activity.company;

import android.app.Activity;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.text.Editable;

import androidx.annotation.NonNull;

import com.app.tributum.R;
import com.app.tributum.activity.company.model.Company;
import com.app.tributum.activity.company.model.Director;
import com.app.tributum.activity.company.model.Secretary;
import com.app.tributum.activity.contract.PhotoCrop;
import com.app.tributum.application.FintrexApplication;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CompanyPresenterImpl implements AsyncListener, CompanyPresenter, RequestSentListener {

    final private CompanyView view;

    private boolean hasSecondDirector;

    private boolean hasThirdDirector;

    private int previousDirector1Length;
    private int previousDirector2Length;
    private int previousDirector3Length;
    private int previousSecretaryLength;

    private Company company;
    private Director director1 = new Director();
    private Director director2 = new Director();
    private Director director3 = new Director();

    private boolean acceptedTerms;

    private boolean isPreview;

    private File file;
    private String filePath;
    @PhotoCrop
    private int photoState;

    @CompanyProgressState
    private int state = CompanyProgressState.COMPANY;

    public CompanyPresenterImpl(CompanyView view) {
        this.view = view;
    }

    @Override
    public void onMainButtonClick() {
        if (state == CompanyProgressState.COMPANY) {
            moveToDirectorScreen();
        } else if (state == CompanyProgressState.DIRECTOR) {
            moveToSecretaryScreen();
        } else {
            sendInfo();
        }
    }

    @Override
    public void onBackPressed() {
        if (view == null)
            return;
        if (isPreview) {
            isPreview = false;
            view.closePreview();
        } else if (state == CompanyProgressState.SECRETARY) {
            state = CompanyProgressState.DIRECTOR;
            view.hideSecretaryView();
            view.showDirectorViewFromLeft();
            view.setConfirmationButtonText(R.string.continue_label);
        } else if (state == CompanyProgressState.DIRECTOR) {
            state = CompanyProgressState.COMPANY;
            view.hideDirectorViewToRight();
            view.showCompanyView();
        } else if (state == CompanyProgressState.COMPANY) {
            view.closeActivity();
        }
    }

    private void moveToDirectorScreen() {
        company = view.getCompanyDetails();
        if (company.getFirstName().isEmpty())
            view.showToast(R.string.please_enter_first_name);
        else if (company.getSurName().isEmpty())
            view.showToast(R.string.please_enter_sur_name);
        else if (!ValidationUtils.isEmailValid(company.getEmail()))
            view.showToast(R.string.please_enter_correct_email);
        else if (company.getPhone().isEmpty())
            view.showToast(R.string.please_enter_phone);
        else if (company.getCompanyName().isEmpty())
            view.showToast(R.string.please_enter_company_name);
        else if (company.getProposedCompanyName().isEmpty())
            view.showToast(R.string.please_enter_proposed);
        else if (company.getAddress().isEmpty())
            view.showToast(R.string.please_enter_address);
        else if (company.getTown().isEmpty())
            view.showToast(R.string.please_enter_town);
        else if (company.getCountry().isEmpty())
            view.showToast(R.string.please_enter_country);
        else if (company.getActivities().isEmpty())
            view.showToast(R.string.please_enter_activities);
        else if (company.getRegisteredOffice().isEmpty())
            view.showToast(R.string.please_enter_registered_address);
        else {
            state = CompanyProgressState.DIRECTOR;
            view.hideCompanyView();
            view.showDirectorViewFromRight();
            view.setConfirmationButtonText(R.string.continue_label);
        }
    }

    private void moveToSecretaryScreen() {
        view.getDirector1Details();
        if (hasSecondDirector)
            view.getDirector2Details();
        if (hasThirdDirector)
            view.getDirector3Details();

        if (director1.getFirstName().isEmpty()
                || (hasSecondDirector && director2.getFirstName().isEmpty())
                || (hasThirdDirector && director3.getFirstName().isEmpty()))
            view.showToast(R.string.please_enter_first_name);
        else if (director1.getSurName().isEmpty()
                || (hasSecondDirector && director2.getSurName().isEmpty())
                || (hasThirdDirector && director3.getSurName().isEmpty()))
            view.showToast(R.string.please_enter_sur_name);
        else if (director1.getBirthday().isEmpty()
                || (hasSecondDirector && director2.getBirthday().isEmpty())
                || (hasThirdDirector && director3.getBirthday().isEmpty()))
            view.showToast(R.string.please_enter_birthday_format);
        else if (!ValidationUtils.isPpsValid(director1.getPps())
                || (hasSecondDirector && !ValidationUtils.isPpsValid(director2.getPps()))
                || (hasThirdDirector && !ValidationUtils.isPpsValid(director3.getPps())))
            view.showToast(R.string.please_enter_pps);
        else if (director1.getNationality().isEmpty()
                || (hasSecondDirector && director2.getNationality().isEmpty())
                || (hasThirdDirector && director3.getNationality().isEmpty()))
            view.showToast(R.string.please_enter_nationality);
        else if (director1.getOccupation().isEmpty()
                || (hasSecondDirector && director2.getOccupation().isEmpty())
                || (hasThirdDirector && director3.getOccupation().isEmpty()))
            view.showToast(R.string.please_enter_occupation);
        else if (director1.getAddress().isEmpty()
                || (hasSecondDirector && director2.getAddress().isEmpty())
                || (hasThirdDirector && director3.getAddress().isEmpty()))
            view.showToast(R.string.please_enter_address);
        else if (director1.getPpsFrontFile() == null
                || (hasSecondDirector && director2.getPpsFrontFile() == null)
                || (hasThirdDirector && director3.getPpsFrontFile() == null))
            view.showToast(R.string.please_add_pps_front);
        else if (director1.getPpsBackFile() == null
                || (hasSecondDirector && director2.getPpsBackFile() == null)
                || (hasThirdDirector && director3.getPpsBackFile() == null))
            view.showToast(R.string.please_add_pps_back);
        else if (director1.getIdFile() == null
                || (hasSecondDirector && director2.getIdFile() == null)
                || (hasThirdDirector && director3.getIdFile() == null))
            view.showToast(R.string.please_add_id_mandatory);
        else if (director1.getPassport() == null
                || (hasSecondDirector && director2.getPassport() == null)
                || (hasThirdDirector && director3.getPassport() == null))
            view.showToast(R.string.please_add_passport);
        else {
            state = CompanyProgressState.SECRETARY;
            view.hideDirectorViewToLeft();
            view.showSecretaryView();
            view.setConfirmationButtonText(R.string.send_form_label);
        }
    }

    private void sendInfo() {
        Secretary secretary = view.getSecretaryDetails();
        if (secretary.getFirstName().isEmpty())
            view.showToast(R.string.please_enter_first_name);
        else if (secretary.getSurName().isEmpty())
            view.showToast(R.string.please_enter_sur_name);
        else if (secretary.getBirthday().isEmpty())
            view.showToast(R.string.please_enter_birthday_format);
        else if (!ValidationUtils.isEmailValid(secretary.getEmail()))
            view.showToast(R.string.please_enter_correct_email);
        else if (!ValidationUtils.isPpsValid(secretary.getPps()))
            view.showToast(R.string.please_enter_pps);
        else if (secretary.getNationality().isEmpty())
            view.showToast(R.string.please_enter_nationality);
        else if (secretary.getAddress().isEmpty())
            view.showToast(R.string.please_enter_address);
        else if (!acceptedTerms)
            view.showToast(R.string.please_accept_terms);
        else {
            view.showLoadingScreen();
            UploadAsyncTask uploadOneFileTask = new UploadAsyncTask(
                    company.getFirstName() + " " + company.getSurName(),
                    FileUtils.createCompanyFile(company, director1, director2, director3, secretary),
                    this,
                    UploadAsyncTask.UploadType.USER_INFO,
                    "COMPANY_FOUNDATION");
            uploadOneFileTask.setProcess("user_info");
            uploadOneFileTask.execute();
        }
    }

    @Override
    public void setDirector1Details(String firstName, String surName, String birthday,
                                    String pps, String nationality, String occupation, String home, String directorships, String other) {
        director1.setFirstName(firstName);
        director1.setSurName(surName);
        director1.setBirthday(birthday);
        director1.setPps(pps);
        director1.setNationality(nationality);
        director1.setOccupation(occupation);
        director1.setAddress(home);
        director1.setDirectorships(directorships);
        director1.setOther(other);
    }

    @Override
    public void setDirector2Details(String firstName, String surName, String birthday,
                                    String pps, String nationality, String occupation, String home, String directorships, String other) {
        director2.setFirstName(firstName);
        director2.setSurName(surName);
        director2.setBirthday(birthday);
        director2.setPps(pps);
        director2.setNationality(nationality);
        director2.setOccupation(occupation);
        director2.setAddress(home);
        director2.setDirectorships(directorships);
        director2.setOther(other);
    }

    @Override
    public void setDirector3Details(String firstName, String surName, String birthday,
                                    String pps, String nationality, String occupation, String home, String directorships, String other) {
        director3.setFirstName(firstName);
        director3.setSurName(surName);
        director3.setBirthday(birthday);
        director3.setPps(pps);
        director3.setNationality(nationality);
        director3.setOccupation(occupation);
        director3.setAddress(home);
        director3.setDirectorships(directorships);
        director3.setOther(other);
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
    public void onFileChooserTopClicked() {
        if (view != null)
            view.hideBottomSheet();
    }

    private void pickPictureFromGallery(int requestId) {
        if (view == null)
            return;

        if (requestId == ConstantsUtils.SELECT_PIC_DIRECTOR_1_FRONT)
            view.pickDirector1PpsFront();
        else if (requestId == ConstantsUtils.SELECT_PIC_DIRECTOR_1_BACK)
            view.pickDirector1PpsBack();
        else if (requestId == ConstantsUtils.SELECT_PIC_DIRECTOR_1_ID)
            view.pickDirector1Id();
        else if (requestId == ConstantsUtils.SELECT_PIC_DIRECTOR_1_PASS)
            view.pickDirector1Pass();
        else if (requestId == ConstantsUtils.SELECT_PIC_DIRECTOR_2_FRONT)
            view.pickDirector2PpsFront();
        else if (requestId == ConstantsUtils.SELECT_PIC_DIRECTOR_2_BACK)
            view.pickDirector2PpsBack();
        else if (requestId == ConstantsUtils.SELECT_PIC_DIRECTOR_2_ID)
            view.pickDirector2Id();
        else if (requestId == ConstantsUtils.SELECT_PIC_DIRECTOR_2_PASS)
            view.pickDirector2Pass();
        else if (requestId == ConstantsUtils.SELECT_PIC_DIRECTOR_3_FRONT)
            view.pickDirector3PpsFront();
        else if (requestId == ConstantsUtils.SELECT_PIC_DIRECTOR_3_BACK)
            view.pickDirector3PpsBack();
        else if (requestId == ConstantsUtils.SELECT_PIC_DIRECTOR_3_ID)
            view.pickDirector3Id();
        else if (requestId == ConstantsUtils.SELECT_PIC_DIRECTOR_3_PASS)
            view.pickDirector3Pass();
    }

    @Override
    public void setFilePath(String pictureImagePath) {
        filePath = pictureImagePath;
    }

    @Override
    public void onRemovePhotoClicked(String fileName) {
        if (fileName == null)
            return;

        if (fileName.equals(director1.getPpsFrontFile())) {
            view.resetDirector1PpsFrontLayout();
            director1.setPpsFrontFile(null);
        } else if (fileName.equals(director1.getPpsBackFile())) {
            view.resetDirector1PpsBackLayout();
            director1.setPpsBackFile(null);
        } else if (fileName.equals(director1.getIdFile())) {
            view.resetDirector1IdLayout();
            director1.setIdFile(null);
        } else if (fileName.equals(director1.getPassport())) {
            view.resetDirector1PassLayout();
            director1.setPassport(null);
        } else if (fileName.equals(director2.getPpsFrontFile())) {
            view.resetDirector2PpsFrontLayout();
            director2.setPpsFrontFile(null);
        } else if (fileName.equals(director2.getPpsBackFile())) {
            view.resetDirector2PpsBackLayout();
            director2.setPpsBackFile(null);
        } else if (fileName.equals(director2.getIdFile())) {
            view.resetDirector2IdLayout();
            director2.setIdFile(null);
        } else if (fileName.equals(director2.getPassport())) {
            view.resetDirector2PassLayout();
            director2.setPassport(null);
        } else if (fileName.equals(director3.getPpsFrontFile())) {
            view.resetDirector3PpsFrontLayout();
            director3.setPpsFrontFile(null);
        } else if (fileName.equals(director3.getPpsBackFile())) {
            view.resetDirector3PpsBackLayout();
            director3.setPpsBackFile(null);
        } else if (fileName.equals(director3.getIdFile())) {
            view.resetDirector3IdLayout();
            director3.setIdFile(null);
        } else if (fileName.equals(director3.getPassport())) {
            view.resetDirector3PassLayout();
            director3.setPassport(null);
        }

        isPreview = false;
        view.closePreview();
    }

    @Override
    public void onDirector1PpsFrontPicker(Uri uri) {
        photoState = PhotoCrop.DIR_1_FRONT_SELECT;
        director1.setPpsFrontFile(uri.getPath());
        view.startCrop(uri);
    }

    @Override
    public void onDirector1PpsBackPicker(Uri uri) {
        photoState = PhotoCrop.DIR_1_BACK_SELECT;
        director1.setPpsBackFile(uri.getPath());
        view.startCrop(uri);
    }

    @Override
    public void onDirector1IdPicker(Uri uri) {
        photoState = PhotoCrop.DIR_1_ID_SELECT;
        director1.setIdFile(uri.getPath());
        view.startCrop(uri);
    }

    @Override
    public void onDirector1PassPicker(Uri uri) {
        photoState = PhotoCrop.DIR_1_PASS_SELECT;
        director1.setPassport(uri.getPath());
        view.startCrop(uri);
    }

    @Override
    public void onDirector2PpsFrontPicker(Uri uri) {
        photoState = PhotoCrop.DIR_2_FRONT_SELECT;
        director2.setPpsFrontFile(uri.getPath());
        view.startCrop(uri);
    }

    @Override
    public void onDirector2PpsBackPicker(Uri uri) {
        photoState = PhotoCrop.DIR_2_BACK_SELECT;
        director2.setPpsBackFile(uri.getPath());
        view.startCrop(uri);
    }

    @Override
    public void onDirector2IdPicker(Uri uri) {
        photoState = PhotoCrop.DIR_2_ID_SELECT;
        director2.setIdFile(uri.getPath());
        view.startCrop(uri);
    }

    @Override
    public void onDirector2PassPicker(Uri uri) {
        photoState = PhotoCrop.DIR_2_PASS_SELECT;
        director2.setPassport(uri.getPath());
        view.startCrop(uri);
    }

    @Override
    public void onDirector3PpsFrontPicker(Uri uri) {
        photoState = PhotoCrop.DIR_3_FRONT_SELECT;
        director3.setPpsFrontFile(uri.getPath());
        view.startCrop(uri);
    }

    @Override
    public void onDirector3PpsBackPicker(Uri uri) {
        photoState = PhotoCrop.DIR_3_BACK_SELECT;
        director3.setPpsBackFile(uri.getPath());
        view.startCrop(uri);
    }

    @Override
    public void onDirector3IdPicker(Uri uri) {
        photoState = PhotoCrop.DIR_3_ID_SELECT;
        director3.setIdFile(uri.getPath());
        view.startCrop(uri);
    }

    @Override
    public void onDirector3PassPicker(Uri uri) {
        photoState = PhotoCrop.DIR_3_PASS_SELECT;
        director3.setPassport(uri.getPath());
        view.startCrop(uri);
    }

    @Override
    public void handleCropping(String result) {
        switch (photoState) {
            case PhotoCrop.DIR_1_FRONT_SELECT:
            case PhotoCrop.DIR_1_FRONT_CAMERA:
                director1.setPpsFrontFile(result);
                if (result != null)
                    view.setDirector1PpsFrontImage(result);
                break;
            case PhotoCrop.DIR_1_BACK_SELECT:
            case PhotoCrop.DIR_1_BACK_CAMERA:
                director1.setPpsBackFile(result);
                if (result != null)
                    view.setDirector1PpsBackImage(result);
                break;
            case PhotoCrop.DIR_1_ID_SELECT:
            case PhotoCrop.DIR_1_ID_CAMERA:
                director1.setIdFile(result);
                if (result != null)
                    view.setDirector1IdImage(result);
                break;
            case PhotoCrop.DIR_1_PASS_SELECT:
            case PhotoCrop.DIR_1_PASS_CAMERA:
                director1.setPassport(result);
                if (result != null)
                    view.setDirector1PassImage(result);
                break;
            case PhotoCrop.DIR_2_FRONT_SELECT:
            case PhotoCrop.DIR_2_FRONT_CAMERA:
                director2.setPpsFrontFile(result);
                if (result != null)
                    view.setDirector2PpsFrontImage(result);
                break;
            case PhotoCrop.DIR_2_BACK_SELECT:
            case PhotoCrop.DIR_2_BACK_CAMERA:
                director2.setPpsBackFile(result);
                if (result != null)
                    view.setDirector2PpsBackImage(result);
                break;
            case PhotoCrop.DIR_2_ID_SELECT:
            case PhotoCrop.DIR_2_ID_CAMERA:
                director2.setIdFile(result);
                if (result != null)
                    view.setDirector2IdImage(result);
                break;
            case PhotoCrop.DIR_2_PASS_SELECT:
            case PhotoCrop.DIR_2_PASS_CAMERA:
                director2.setPassport(result);
                if (result != null)
                    view.setDirector2PassImage(result);
                break;
            case PhotoCrop.DIR_3_FRONT_SELECT:
            case PhotoCrop.DIR_3_FRONT_CAMERA:
                director3.setPpsFrontFile(result);
                if (result != null)
                    view.setDirector3PpsFrontImage(result);
                break;
            case PhotoCrop.DIR_3_BACK_SELECT:
            case PhotoCrop.DIR_3_BACK_CAMERA:
                director3.setPpsBackFile(result);
                if (result != null)
                    view.setDirector3PpsBackImage(result);
                break;
            case PhotoCrop.DIR_3_ID_SELECT:
            case PhotoCrop.DIR_3_ID_CAMERA:
                director3.setIdFile(result);
                if (result != null)
                    view.setDirector3IdImage(result);
                break;
            case PhotoCrop.DIR_3_PASS_SELECT:
            case PhotoCrop.DIR_3_PASS_CAMERA:
                director3.setPassport(result);
                if (result != null)
                    view.setDirector3PassImage(result);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDirector1PpsFrontClick() {
        view.getDirector1Details();
        if (director1.getPpsFrontFile() == null) {
            view.showFileChooser(ConstantsUtils.SELECT_PIC_DIRECTOR_1_FRONT,
                    ConstantsUtils.CAM_PIC_DIRECTOR_1_FRONT);
            view.hideKeyboard();
        }
    }

    @Override
    public void onDirector1PpsBackClick() {
        view.getDirector1Details();
        if (director1.getPpsBackFile() == null) {
            view.showFileChooser(ConstantsUtils.SELECT_PIC_DIRECTOR_1_BACK,
                    ConstantsUtils.CAM_PIC_DIRECTOR_1_BACK);
            view.hideKeyboard();
        }
    }

    @Override
    public void onDirector1IdClick() {
        view.getDirector1Details();
        if (director1.getIdFile() == null) {
            view.showFileChooser(ConstantsUtils.SELECT_PIC_DIRECTOR_1_ID,
                    ConstantsUtils.CAM_PIC_DIRECTOR_1_ID);
            view.hideKeyboard();
        }
    }

    @Override
    public void onDirector1PassClick() {
        view.getDirector1Details();
        if (director1.getPassport() == null) {
            view.showFileChooser(ConstantsUtils.SELECT_PIC_DIRECTOR_1_PASS,
                    ConstantsUtils.CAM_PIC_DIRECTOR_1_PASS);
            view.hideKeyboard();
        }
    }

    @Override
    public void onDirector2PpsFrontClick() {
        view.getDirector2Details();
        if (director2.getPpsFrontFile() == null) {
            view.showFileChooser(ConstantsUtils.SELECT_PIC_DIRECTOR_2_FRONT,
                    ConstantsUtils.CAM_PIC_DIRECTOR_2_FRONT);
            view.hideKeyboard();
        }
    }

    @Override
    public void onDirector2PpsBackClick() {
        view.getDirector2Details();
        if (director2.getPpsBackFile() == null) {
            view.showFileChooser(ConstantsUtils.SELECT_PIC_DIRECTOR_2_BACK,
                    ConstantsUtils.CAM_PIC_DIRECTOR_2_BACK);
            view.hideKeyboard();
        }
    }

    @Override
    public void onDirector2IdClick() {
        view.getDirector2Details();
        if (director2.getIdFile() == null) {
            view.showFileChooser(ConstantsUtils.SELECT_PIC_DIRECTOR_2_ID,
                    ConstantsUtils.CAM_PIC_DIRECTOR_2_ID);
            view.hideKeyboard();
        }
    }

    @Override
    public void onDirector2PassClick() {
        view.getDirector2Details();
        if (director2.getPassport() == null) {
            view.showFileChooser(ConstantsUtils.SELECT_PIC_DIRECTOR_2_PASS,
                    ConstantsUtils.CAM_PIC_DIRECTOR_2_PASS);
            view.hideKeyboard();
        }
    }

    @Override
    public void onDirector3PpsFrontClick() {
        view.getDirector3Details();
        if (director3.getPpsFrontFile() == null) {
            view.showFileChooser(ConstantsUtils.SELECT_PIC_DIRECTOR_3_FRONT,
                    ConstantsUtils.CAM_PIC_DIRECTOR_3_FRONT);
            view.hideKeyboard();
        }
    }

    @Override
    public void onDirector3PpsBackClick() {
        view.getDirector3Details();
        if (director3.getPpsBackFile() == null) {
            view.showFileChooser(ConstantsUtils.SELECT_PIC_DIRECTOR_3_BACK,
                    ConstantsUtils.CAM_PIC_DIRECTOR_3_BACK);
            view.hideKeyboard();
        }
    }

    @Override
    public void onDirector3IdClick() {
        view.getDirector3Details();
        if (director3.getIdFile() == null) {
            view.showFileChooser(ConstantsUtils.SELECT_PIC_DIRECTOR_3_ID,
                    ConstantsUtils.CAM_PIC_DIRECTOR_3_ID);
            view.hideKeyboard();
        }
    }

    @Override
    public void onDirector3PassClick() {
        view.getDirector3Details();
        if (director3.getPassport() == null) {
            view.showFileChooser(ConstantsUtils.SELECT_PIC_DIRECTOR_3_PASS,
                    ConstantsUtils.CAM_PIC_DIRECTOR_3_PASS);
            view.hideKeyboard();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (view == null)
            return;

        switch (requestCode) {
            case ConstantsUtils.CAM_PIC_DIRECTOR_1_FRONT:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.DIR_1_FRONT_CAMERA;
                    director1.setPpsFrontFile(file.getAbsolutePath());
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAM_PIC_DIRECTOR_1_BACK:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.DIR_1_BACK_CAMERA;
                    director1.setPpsBackFile(file.getAbsolutePath());
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAM_PIC_DIRECTOR_1_ID:
                if (resultCode == Activity.RESULT_OK && file != null) {
                    photoState = PhotoCrop.DIR_1_ID_CAMERA;
                    director1.setIdFile(file.getAbsolutePath());
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAM_PIC_DIRECTOR_1_PASS:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.DIR_1_PASS_CAMERA;
                    director1.setPassport(file.getAbsolutePath());
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAM_PIC_DIRECTOR_2_FRONT:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.DIR_2_FRONT_CAMERA;
                    director2.setPpsFrontFile(file.getAbsolutePath());
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAM_PIC_DIRECTOR_2_BACK:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.DIR_2_BACK_CAMERA;
                    director2.setPpsBackFile(file.getAbsolutePath());
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAM_PIC_DIRECTOR_2_ID:
                if (resultCode == Activity.RESULT_OK && file != null) {
                    photoState = PhotoCrop.DIR_2_ID_CAMERA;
                    director2.setIdFile(file.getAbsolutePath());
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAM_PIC_DIRECTOR_2_PASS:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.DIR_2_PASS_CAMERA;
                    director2.setPassport(file.getAbsolutePath());
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAM_PIC_DIRECTOR_3_FRONT:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.DIR_3_FRONT_CAMERA;
                    director3.setPpsFrontFile(file.getAbsolutePath());
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAM_PIC_DIRECTOR_3_BACK:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.DIR_3_BACK_CAMERA;
                    director3.setPpsBackFile(file.getAbsolutePath());
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAM_PIC_DIRECTOR_3_ID:
                if (resultCode == Activity.RESULT_OK && file != null) {
                    photoState = PhotoCrop.DIR_3_ID_CAMERA;
                    director3.setIdFile(file.getAbsolutePath());
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            case ConstantsUtils.CAM_PIC_DIRECTOR_3_PASS:
                if (resultCode == Activity.RESULT_OK) {
                    photoState = PhotoCrop.DIR_3_PASS_CAMERA;
                    director3.setPassport(file.getAbsolutePath());
                    view.startCrop(ImageUtils.getUriFromFile(file));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDirector1PpsFrontPreview() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(director1.getPpsFrontFile());
    }

    @Override
    public void onDirector1PpsBackPreview() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(director1.getPpsBackFile());
    }

    @Override
    public void onDirector1IdPreview() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(director1.getIdFile());
    }

    @Override
    public void onDirector1PassPreview() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(director1.getPassport());
    }

    @Override
    public void onDirector1PpsFrontDelete() {
        if (view != null)
            view.resetDirector1PpsFrontLayout();
        director1.setPpsFrontFile(null);
    }

    @Override
    public void onDirector1PpsBackDelete() {
        if (view != null)
            view.resetDirector1PpsBackLayout();
        director1.setPpsBackFile(null);
    }

    @Override
    public void onDirector1IdDelete() {
        if (view != null)
            view.resetDirector1IdLayout();
        director1.setIdFile(null);
    }

    @Override
    public void onDirector1PassDelete() {
        if (view != null)
            view.resetDirector1PassLayout();
        director1.setPassport(null);
    }

    @Override
    public void onDirector2PpsFrontPreview() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(director2.getPpsFrontFile());
    }

    @Override
    public void onDirector2PpsBackPreview() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(director2.getPpsBackFile());
    }

    @Override
    public void onDirector2IdPreview() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(director2.getIdFile());
    }

    @Override
    public void onDirector2PassPreview() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(director2.getPassport());
    }

    @Override
    public void onDirector2PpsFrontDelete() {
        if (view != null)
            view.resetDirector2PpsFrontLayout();
        director2.setPpsFrontFile(null);
    }

    @Override
    public void onDirector2PpsBackDelete() {
        if (view != null)
            view.resetDirector2PpsBackLayout();
        director2.setPpsBackFile(null);
    }

    @Override
    public void onDirector2IdDelete() {
        if (view != null)
            view.resetDirector2IdLayout();
        director2.setIdFile(null);
    }

    @Override
    public void onDirector2PassDelete() {
        if (view != null)
            view.resetDirector2PassLayout();
        director2.setPassport(null);
    }

    @Override
    public void onDirector3PpsFrontPreview() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(director3.getPpsFrontFile());
    }

    @Override
    public void onDirector3PpsBackPreview() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(director3.getPpsBackFile());
    }

    @Override
    public void onDirector3IdPreview() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(director3.getIdFile());
    }

    @Override
    public void onDirector3PassPreview() {
        isPreview = true;
        if (view != null)
            view.openFilePreview(director3.getPassport());
    }

    @Override
    public void onDirector3PpsFrontDelete() {
        if (view != null)
            view.resetDirector3PpsFrontLayout();
        director3.setPpsFrontFile(null);
    }

    @Override
    public void onDirector3PpsBackDelete() {
        if (view != null)
            view.resetDirector3PpsBackLayout();
        director3.setPpsBackFile(null);
    }

    @Override
    public void onDirector3IdDelete() {
        if (view != null)
            view.resetDirector3IdLayout();
        director3.setIdFile(null);
    }

    @Override
    public void onDirector3PassDelete() {
        if (view != null)
            view.resetDirector3PassLayout();
        director3.setPassport(null);
    }

    @Override
    public void afterBirthdayChanged(Editable s, int resourceId) {
        if (resourceId == R.id.director_1_layout_id)
            handleDates(s, previousDirector1Length, resourceId);
        else if (resourceId == R.id.director_2_layout_id)
            handleDates(s, previousDirector2Length, resourceId);
        else if ((resourceId == R.id.director_3_layout_id))
            handleDates(s, previousDirector3Length, resourceId);
        else
            handleDates(s, previousSecretaryLength, resourceId);
    }

    @Override
    public void beforeBirthdayChanged(int length, int resourceId) {
        if (resourceId == R.id.director_1_layout_id)
            previousDirector1Length = length;
        else if (resourceId == R.id.director_2_layout_id)
            previousDirector2Length = length;
        else if (resourceId == R.id.director_3_layout_id)
            previousDirector3Length = length;
        else
            previousSecretaryLength = length;
    }

    private void handleDates(Editable s, int previousValue, int id) {
        if (view == null)
            return;

        if (s.length() > previousValue) {
            if (s.length() == 2) {
                try {
                    if (Integer.parseInt(s.toString()) > 31) {
                        view.setBirthdayText("31/", id);
                    } else {
                        view.setBirthdayText(s + "/", id);
                    }
                } catch (NumberFormatException e) {
                    int currentDay = java.util.Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    String day = String.valueOf(currentDay);
                    if (currentDay < 10) {
                        day = "0" + day;
                    }
                    view.setBirthdayText(day + "/", id);
                }
                view.moveBirthdayCursorToEnd(id);
            } else if (s.length() == 3) {
                String birthday = String.valueOf(s);
                if (!birthday.endsWith("/")) {
                    String string = birthday.substring(0, birthday.length() - 1) + "/" + birthday.substring(birthday.length() - 1);
                    view.setBirthdayText(string, id);
                }
                view.moveBirthdayCursorToEnd(id);
            } else if (s.length() == 4) {
                int month;
                try {
                    month = Integer.parseInt(s.toString().substring(s.toString().length() - 1));
                    if (month > 1) {
                        String string = s.toString().substring(0, 3) + "0" + month + "/";
                        view.setBirthdayText(string, id);
                    }
                } catch (NumberFormatException e) {
                    int currentMonth = java.util.Calendar.getInstance().get(Calendar.MONTH) + 1;
                    String monthString = String.valueOf(currentMonth);
                    if (currentMonth < 10) {
                        monthString = "0" + monthString;
                    }
                    String string = s.toString().substring(0, 3) + monthString + "/";
                    view.setBirthdayText(string, id);
                }
                view.moveBirthdayCursorToEnd(id);
            } else if (s.length() == 5) {
                String string = s.toString();
                string = string.substring(3);
                try {
                    if (Integer.parseInt(string) > 12) {
                        String firstString = s.toString();
                        view.setBirthdayText(firstString.substring(0, 3) + "12/", id);
                    } else {
                        view.setBirthdayText(s + "/", id);
                    }
                } catch (NumberFormatException e) {
                    int c = s.toString().charAt(3) - '0';
                    if (c == 1) {
                        view.setBirthdayText(s.toString().substring(0, 3) + "01/", id);
                    } else {
                        int currentMonth = java.util.Calendar.getInstance().get(Calendar.MONTH) + 1;
                        String month = String.valueOf(currentMonth);
                        if (currentMonth < 10) {
                            month = "0" + month;
                        }
                        String string1 = s.toString().substring(0, 3) + month + "/";
                        view.setBirthdayText(string1, id);
                    }
                }
                view.moveBirthdayCursorToEnd(id);
            } else if (s.length() == 6) {
                String birthday = String.valueOf(s);
                if (!birthday.endsWith("/")) {
                    String string = birthday.substring(0, birthday.length() - 1) + "/" + birthday.substring(birthday.length() - 1);
                    view.setBirthdayText(string, id);
                }
                view.moveBirthdayCursorToEnd(id);
            } else if (s.length() == 10) {
                String string = s.toString();
                string = string.substring(6);
                int currentYear = java.util.Calendar.getInstance().get(Calendar.YEAR);
                try {
                    if (Integer.parseInt(string) > currentYear) {
                        String firstString = s.toString();
                        view.setBirthdayText(firstString.substring(0, 6) + currentYear, id);
                    }
                } catch (NumberFormatException nfe) {
                    String string1 = s.toString().substring(0, 6) + currentYear;
                    view.setBirthdayText(string1, id);
                }
                view.moveBirthdayCursorToEnd(id);
            }
        }
    }

    @Override
    public void onDirector2Click() {
        if (hasSecondDirector)
            view.hideSecondDirector();
        else
            view.showSecondDirector();
        hasSecondDirector = !hasSecondDirector;
    }

    @Override
    public void onDirector3Click() {
        if (hasThirdDirector)
            view.hideThirdDirector();
        else
            view.showThirdDirector();
        hasThirdDirector = !hasThirdDirector;
    }

    @Override
    public void onAgreeTermsClick(boolean checkboxClicked) {
        acceptedTerms = !acceptedTerms;
        if (!checkboxClicked)
            view.checkTheAgreeBox(acceptedTerms);
    }

    @Override
    public void onOkClicked() {
        if (view != null)
            view.closeActivity();
    }

    @Override
    public void onTaskCompleted(String process) {
        if (process != null && process.equals("user_info")) {
            Map<String, String> uploadList = new HashMap<>();
            uploadList.put("DIRECTOR_1_PPS_FRONT", director1.getPpsFrontFile().replace("file://", ""));
            uploadList.put("DIRECTOR_1_PPS_BACK", director1.getPpsBackFile().replace("file://", ""));
            uploadList.put("DIRECTOR_1_ID", director1.getIdFile().replace("file://", ""));
            uploadList.put("DIRECTOR_1_PASSPORT", director1.getPassport().replace("file://", ""));
            if (hasSecondDirector) {
                uploadList.put("DIRECTOR_2_PPS_FRONT", director2.getPpsFrontFile().replace("file://", ""));
                uploadList.put("DIRECTOR_2_PPS_BACK", director2.getPpsBackFile().replace("file://", ""));
                uploadList.put("DIRECTOR_2_ID", director2.getIdFile().replace("file://", ""));
                uploadList.put("DIRECTOR_2_PASSPORT", director2.getPassport().replace("file://", ""));
            }
            if (hasThirdDirector) {
                uploadList.put("DIRECTOR_3_PPS_FRONT", director3.getPpsFrontFile().replace("file://", ""));
                uploadList.put("DIRECTOR_3_PPS_BACK", director3.getPpsBackFile().replace("file://", ""));
                uploadList.put("DIRECTOR_3_ID", director3.getIdFile().replace("file://", ""));
                uploadList.put("DIRECTOR_3_PASSPORT", director3.getPassport().replace("file://", ""));
            }

            UploadAsyncTask uploadMultipleFilesTask = new UploadAsyncTask(
                    company.getFirstName() + " " + company.getSurName(),
                    uploadList,
                    this,
                    UploadAsyncTask.UploadType.MULTIPLE,
                    "COMPANY_FOUNDATION");
            uploadMultipleFilesTask.execute();
        } else {
            Retrofit retrofit = RetrofitClientInstance.getInstance();
            final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

            Call<Object> call = api.sendEmail(new EmailBody(ConstantsUtils.FINTREX_EMAIL, generateInternalEmailMessage(company.getFirstName() + " " + company.getSurName()), "Android"));
            call.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                    if (!response.isSuccessful()) {
                        view.showToast(R.string.something_went_wrong);
                    } else {
                        sendClientMail(company.getEmail(), FintrexApplication.getInstance().getString(R.string.contract_mail_message));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                    view.hideLoadingScreen();
                    view.showToast(R.string.something_went_wrong);
                }
            });
        }
    }

    private String generateInternalEmailMessage(String name) {
        String formattedString = name.toUpperCase();
        formattedString = formattedString.replaceAll(" ", "%20");
        return "New Company request for " + company.getFirstName() + " " + company.getSurName()
                + "\n\n" + "Click on below link to access files\n\n"
                + "https://www.dropbox.com/home/Apps/Tributum/COMPANY_FOUNDATION/"
                + formattedString;
    }

    private void sendClientMail(String email, String message) {
        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendEmail(new EmailBody(email, message, "Android"));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    view.hideLoadingScreen();
                    view.showRequestSent();
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
}