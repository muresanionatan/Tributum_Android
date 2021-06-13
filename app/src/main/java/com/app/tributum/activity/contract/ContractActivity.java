package com.app.tributum.activity.contract;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.app.tributum.utils.ValidationUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.app.tributum.R;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.fragment.invoices.listener.AsyncListener;
import com.app.tributum.helper.DrawingView;
import com.app.tributum.listener.SignatureListener;
import com.app.tributum.model.ContractModel;
import com.app.tributum.retrofit.InterfaceAPI;
import com.app.tributum.retrofit.RetrofitClientInstance;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.ImageUtils;
import com.app.tributum.utils.NetworkUtils;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UploadAsyncTask;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.ui.FileUtils;
import com.app.tributum.utils.ui.LoadingScreen;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

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

public class ContractActivity extends AppCompatActivity implements SignatureListener, AsyncListener {

    private Bitmap signatureFile;

    private String ppsFileFront;

    private String ppsFileBack;

    private String idFile;

    private EditText nameEditText;

    private EditText addressEditText;

    private EditText ppsNumberEditText;

    private EditText emailEditText;

    private EditText occupationEditText;

    private RelativeLayout parentView;

    private CheckBox singleCheck;

    private CheckBox marriedCheck;

    private CheckBox divorcedCheck;

    private CheckBox cohabitingCheck;

    private TextView contractDate;

    private EditText birthday;

    private CheckBox otherCheck;

    private boolean isSignatureSet;

    private int previousLength = 0;

    private LoadingScreen loadingScreen;

    private BottomSheetBehavior fileChooser;

    private View previewLayout;

    private ImageView clearImage;

    private File file;

    private DrawingView signatureDraw;

    private EditText phoneNumberEditText;

    private ContractModel contractModel;

    private String marriageCertificateFile;

    private EditText bankAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contract_activity);
        StatusBarUtils.makeStatusBarTransparent(this);
        UtilsGeneral.setAppLanguage(TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        setupViews();
    }

    private void setupViews() {
        findViewById(R.id.open_date_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog picker = new DatePickerDialog(ContractActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                String dayString = String.valueOf(dayOfMonth);
                                if (dayOfMonth < 10)
                                    dayString = "0" + dayOfMonth;
                                monthOfYear = monthOfYear + 1;
                                String monthString = String.valueOf(monthOfYear);
                                if (monthOfYear < 10)
                                    monthString = "0" + monthOfYear;
                                contractDate.setText(dayString + "/" + monthString + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        findViewById(R.id.scrollView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null && event.getAction() == MotionEvent.ACTION_MOVE) {
                    UtilsGeneral.hideSoftKeyboard(ContractActivity.this);
                }
                return false;
            }
        });

        TextView sendButton = findViewById(R.id.send_button);
        clearImage = findViewById(R.id.clear_image);

        nameEditText = findViewById(R.id.name_edit_text);
        addressEditText = findViewById(R.id.address_edit_text);
        ppsNumberEditText = findViewById(R.id.pps_number_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        occupationEditText = findViewById(R.id.occupation_edit_text);
        previewLayout = findViewById(R.id.preview_layout_id);
        phoneNumberEditText = findViewById(R.id.phone_edit_text);
        bankAccount = findViewById(R.id.bank_edit_text);
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(bankAccount, 34, true);

        nameEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!nameEditText.getText().toString().equals("")
                        && !TributumAppHelper.getBooleanSetting(AppKeysValues.CONTRACT_FORM_STARTED)) {
                    TributumAppHelper.saveSetting(AppKeysValues.CONTRACT_FORM_STARTED, AppKeysValues.TRUE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addressEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        ppsNumberEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(ppsNumberEditText, 9, true);

        emailEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        occupationEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        phoneNumberEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        singleCheck = findViewById(R.id.single_checkbox);
        marriedCheck = findViewById(R.id.married_checkbox);
        divorcedCheck = findViewById(R.id.divorced_checkbox);
        cohabitingCheck = findViewById(R.id.cohabiting_checkbox);

        contractDate = findViewById(R.id.starting_date);
        birthday = findViewById(R.id.edittext_birthday_id);
        UtilsGeneral.setMaxLengthEditText(birthday, 10);

        birthday.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > previousLength) {
                    if (s.length() == 2) {
                        if (Integer.parseInt(s.toString()) > 31) {
                            birthday.setText("31/");
                        } else {
                            birthday.setText(birthday.getText().toString() + "/");
                        }
                        birthday.setSelection(birthday.getText().length());
                    } else if (s.length() == 5) {
                        String string = s.toString();
                        string = string.substring(3);
                        if (Integer.parseInt(string) > 12) {
                            String firstString = s.toString();
                            birthday.setText(firstString.substring(0, 3) + "12/");
                        } else {
                            birthday.setText(birthday.getText().toString() + "/");
                        }
                        birthday.setSelection(birthday.getText().length());
                    } else if (s.length() == 10) {
                        String string = s.toString();
                        string = string.substring(6);
                        int currentYear = java.util.Calendar.getInstance().get(Calendar.YEAR);
                        if (Integer.parseInt(string) > currentYear) {
                            String firstString = s.toString();
                            birthday.setText(firstString.substring(0, 6) + currentYear);
                        }
                        birthday.setSelection(birthday.getText().length());
                    }
                }
            }
        });

        EditText otherEditText = findViewById(R.id.other_edit_id);
        UtilsGeneral.setMaxLengthEditText(otherEditText, 60);
        otherCheck = findViewById(R.id.ninth);
        otherCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsGeneral.setFocusOnInput(otherEditText);
            }
        });
        otherEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!otherCheck.isChecked())
                    otherCheck.setChecked(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendButtonClick();
            }
        });

        clearImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClearImageClick();
            }
        });

        parentView = findViewById(R.id.signature_drawing_view);
        signatureDraw = new DrawingView(ContractActivity.this, this);
        parentView.addView(signatureDraw);

        findViewById(R.id.make_signature).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.signature_layout).setVisibility(View.VISIBLE);
            }
        });

//        findViewById(R.id.save_signature_id).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveSignatureImage();
//                findViewById(R.id.signature_layout).setVisibility(View.GONE);
//            }
//        });
//
//        findViewById(R.id.delete_signature_id).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signatureDraw.clear();
//                isSignatureSet = false;
//                findViewById(R.id.signature_layout).setVisibility(View.GONE);
//            }
//        });

        singleCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marriedCheck.isChecked())
                    findViewById(R.id.married_id).setVisibility(View.GONE);
                singleCheck.setChecked(true);
                marriedCheck.setChecked(false);
                divorcedCheck.setChecked(false);
                cohabitingCheck.setChecked(false);
            }
        });

        marriedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleCheck.setChecked(false);
                marriedCheck.setChecked(true);
                divorcedCheck.setChecked(false);
                cohabitingCheck.setChecked(false);
                showMaritalLayout();
            }
        });

        divorcedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marriedCheck.isChecked())
                    findViewById(R.id.married_id).setVisibility(View.GONE);
                singleCheck.setChecked(false);
                marriedCheck.setChecked(false);
                divorcedCheck.setChecked(true);
                cohabitingCheck.setChecked(false);
            }
        });

        cohabitingCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marriedCheck.isChecked())
                    findViewById(R.id.married_id).setVisibility(View.GONE);
                singleCheck.setChecked(false);
                marriedCheck.setChecked(false);
                divorcedCheck.setChecked(false);
                cohabitingCheck.setChecked(true);
            }
        });

        final View taxesView = findViewById(R.id.taxes_id);
        final CheckBox selfEmployed = findViewById(R.id.self_employed_id);
        final CheckBox employee = findViewById(R.id.employee_id);
        selfEmployed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selfEmployed.isChecked())
                    taxesView.setVisibility(View.VISIBLE);
                else
                    taxesView.setVisibility(View.GONE);

                if (!employee.isChecked())
                    employee.setChecked(true);
            }
        });

        employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!employee.isChecked() && !selfEmployed.isChecked()) {
                    selfEmployed.setChecked(true);
                    taxesView.setVisibility(View.VISIBLE);
                }
            }
        });

        setupFilesLayout();

        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), 0);
    }

    private void showMaritalLayout() {
        View maritalLayout = findViewById(R.id.married_id);
        maritalLayout.setVisibility(View.VISIBLE);
        ((TextView) maritalLayout.findViewById(R.id.contract_file_text_id)).setText(R.string.add_marriage_record);
        maritalLayout.findViewById(R.id.marriage_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marriageCertificateFile == null)
                    setupFileChooserClicks(FileChooser.MARRIAGE);
                else
                    openFilePreview(R.id.marriage_layout_id);
            }
        });
    }

    private void setupFilesLayout() {
        ((TextView) findViewById(R.id.pps_front_layout_id).findViewById(R.id.contract_file_text_id)).setText(R.string.add_pps_front);
        ((TextView) findViewById(R.id.pps_back_layout_id).findViewById(R.id.contract_file_text_id)).setText(R.string.add_pps_back);
        ((TextView) findViewById(R.id.id_layout_id).findViewById(R.id.contract_file_text_id)).setText(R.string.add_id);

        // get the bottom sheet view
        RelativeLayout llBottomSheet = findViewById(R.id.file_chooser_id);
        fileChooser = BottomSheetBehavior.from(llBottomSheet);
        fileChooser.setDraggable(false);
        findViewById(R.id.file_chooser_top_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseBottomSheet();
            }
        });

        findViewById(R.id.pps_front_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ppsFileFront == null)
                    setupFileChooserClicks(FileChooser.PPS_FRONT);
                else
                    openFilePreview(R.id.pps_front_layout_id);
            }
        });

        findViewById(R.id.pps_back_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ppsFileBack == null)
                    setupFileChooserClicks(FileChooser.PPS_BACK);
                else
                    openFilePreview(R.id.pps_back_layout_id);
            }
        });

        findViewById(R.id.id_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idFile == null)
                    setupFileChooserClicks(FileChooser.ID);
                else
                    openFilePreview(R.id.id_layout_id);
            }
        });
    }

    private void collapseBottomSheet() {
        fileChooser.setHideable(true);
        fileChooser.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void setupFileChooserClicks(int button) {
        fileChooser.setState(BottomSheetBehavior.STATE_EXPANDED);
        fileChooser.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED)
                    findViewById(R.id.file_chooser_top_id).setVisibility(View.VISIBLE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        findViewById(R.id.add_from_gallery_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button == FileChooser.PPS_FRONT)
                    pickPictureFromGallery(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_FRONT);
                else if (button == FileChooser.PPS_BACK)
                    pickPictureFromGallery(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_BACK);
                else if (button == FileChooser.ID)
                    pickPictureFromGallery(ConstantsUtils.SELECTED_PICTURE_REQUEST_ID);
                else
                    pickPictureFromGallery(ConstantsUtils.SELECTED_PICTURE_REQUEST_MARRIAGE);

                collapseBottomSheet();
            }
        });
        findViewById(R.id.take_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button == FileChooser.PPS_FRONT)
                    openCamera(ConstantsUtils.CAMERA_REQUEST_PPS_FRONT);
                else if (button == FileChooser.PPS_BACK)
                    openCamera(ConstantsUtils.CAMERA_REQUEST_PPS_BACK);
                else if (button == FileChooser.ID)
                    openCamera(ConstantsUtils.CAMERA_REQUEST_ID);
                else
                    openCamera(ConstantsUtils.CAMERA_REQUEST_MARRIAGE);

                collapseBottomSheet();
            }
        });
    }

    private void openFilePreview(int id) {
        clearImage.setVisibility(View.GONE);
        previewLayout.setVisibility(View.VISIBLE);
        ImageView previewImage = findViewById(R.id.image_preview_id);
        if (id == R.id.pps_front_layout_id)
            Glide.with(this).load("file://" + ppsFileFront).into(previewImage);
        else if (id == R.id.pps_back_layout_id)
            Glide.with(this).load("file://" + ppsFileBack).into(previewImage);
        else if (id == R.id.id_layout_id)
            Glide.with(this).load("file://" + idFile).into(previewImage);
        else
            Glide.with(this).load("file://" + marriageCertificateFile).into(previewImage);

//        findViewById(R.id.delete_photo_button_id).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                removeImageToContractFile(id);
//                if (id == R.id.pps_front_layout_id)
//                    ppsFileFront = null;
//                else if (id == R.id.pps_back_layout_id)
//                    ppsFileBack = null;
//                else if (id == R.id.id_layout_id)
//                    idFile = null;
//                else
//                    marriageCertificateFile = null;
//
//                previewLayout.setVisibility(View.GONE);
//            }
//        });
//        findViewById(R.id.close_camera_id).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                previewLayout.setVisibility(View.GONE);
//                clearImage.setVisibility(View.VISIBLE);
//            }
//        });
    }

    private void removeImageToContractFile(int id) {
        ImageView imageView = findViewById(id).findViewById(R.id.preview_image_id);
        imageView.setImageResource(0);
        imageView.setVisibility(View.GONE);
        findViewById(id).findViewById(R.id.contract_file_layout_id).setVisibility(View.VISIBLE);
    }

    private void saveSignatureImage() {
        parentView.setDrawingCacheEnabled(true);
        signatureFile = parentView.getDrawingCache();
        File signature = new File(getFilesDir(), "/signature.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(signature);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        signatureFile.compress(Bitmap.CompressFormat.PNG, 95, fos);
    }

    @Override
    public void onDrawingFinished() {

    }

    private void handleSendButtonClick() {
        if (NetworkUtils.isNetworkConnected()) {
            if (nameEditText.getText().toString().equals("")) {
                Toast.makeText(this, getString(R.string.please_enter_name), Toast.LENGTH_SHORT).show();
            } else if (addressEditText.getText().toString().equals("")) {
                Toast.makeText(this, getString(R.string.please_enter_address), Toast.LENGTH_SHORT).show();
            } else if (ppsNumberEditText.getText().toString().equals("") || !ValidationUtils.isPpsValid(ppsNumberEditText.getText().toString())) {
                Toast.makeText(this, getString(R.string.please_enter_pps), Toast.LENGTH_SHORT).show();
            } else if (!ValidationUtils.isEmailValid(emailEditText.getText().toString())) {
                Toast.makeText(this, getString(R.string.please_enter_correct_email), Toast.LENGTH_SHORT).show();
            } else if (occupationEditText.getText().toString().equals("")) {
                Toast.makeText(this, getString(R.string.please_enter_occupation), Toast.LENGTH_SHORT).show();
            } else if (phoneNumberEditText.getText().toString().equals("")) {
                Toast.makeText(this, getString(R.string.please_enter_phone), Toast.LENGTH_SHORT).show();
            } else if (bankAccount.getText().toString().equals("")) {
                Toast.makeText(this, getString(R.string.please_add_bank), Toast.LENGTH_SHORT).show();
            } else if (marriedCheck.isChecked() && marriageCertificateFile == null) {
                Toast.makeText(this, getString(R.string.please_add_marriage), Toast.LENGTH_SHORT).show();
            } else if (birthday.getText().toString().equals("")) {
                Toast.makeText(this, getString(R.string.please_enter_birthday), Toast.LENGTH_SHORT).show();
            } else if (birthday.getText().toString().length() < 10) {
                Toast.makeText(this, getString(R.string.please_enter_birthday_format), Toast.LENGTH_SHORT).show();
            } else if (contractDate.getText().toString().equals("")) {
                Toast.makeText(this, getString(R.string.please_enter_contract_date), Toast.LENGTH_SHORT).show();
            } else if (((CheckBox) findViewById(R.id.self_employed_id)).isChecked()
                    && !((CheckBox) findViewById(R.id.first)).isChecked()
                    && !((CheckBox) findViewById(R.id.second)).isChecked()
                    && !((CheckBox) findViewById(R.id.third)).isChecked()
                    && !((CheckBox) findViewById(R.id.fourth)).isChecked()
                    && !((CheckBox) findViewById(R.id.fifth)).isChecked()
                    && !((CheckBox) findViewById(R.id.sixth)).isChecked()
                    && !((CheckBox) findViewById(R.id.seventh)).isChecked()
                    && !((CheckBox) findViewById(R.id.eight)).isChecked()
                    && !otherCheck.isChecked()) {
                Toast.makeText(this, getString(R.string.please_enter_applying_for), Toast.LENGTH_SHORT).show();
            } else if (otherCheck.isChecked() && ((EditText) findViewById(R.id.other_edit_id)).getText().toString().equals("")) {
                Toast.makeText(this, getString(R.string.please_enter_other), Toast.LENGTH_SHORT).show();
            } else if (ppsFileFront == null) {
                Toast.makeText(this, getString(R.string.please_add_pps), Toast.LENGTH_SHORT).show();
            } else if (idFile == null) {
                Toast.makeText(this, getString(R.string.please_add_id), Toast.LENGTH_SHORT).show();
            } else if (signatureFile == null || !isSignatureSet) {
                Toast.makeText(this, getString(R.string.please_add_signature), Toast.LENGTH_SHORT).show();
            } else {
                sendInfo();
            }
        } else {
            Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendInfo() {
        contractModel = new ContractModel(
                nameEditText.getText().toString(),
                addressEditText.getText().toString(),
                ppsNumberEditText.getText().toString(),
                emailEditText.getText().toString(),
                contractDate.getText().toString(),
                birthday.getText().toString());

        if (singleCheck.isChecked())
            contractModel.setMaritalStatus(getString(R.string.single_label));
        else if (marriedCheck.isChecked())
            contractModel.setMaritalStatus(getString(R.string.married_label));
        else if (divorcedCheck.isChecked())
            contractModel.setMaritalStatus(getString(R.string.divorced_label));
        else if (cohabitingCheck.isChecked())
            contractModel.setMaritalStatus(getString(R.string.cohabiting_label));

        List<String> employmentList = new ArrayList<>();
        if (((CheckBox) findViewById(R.id.self_employed_id)).isChecked())
            employmentList.add(getString(R.string.self_employed_label));
        if (((CheckBox) findViewById(R.id.employee_id)).isChecked())
            employmentList.add(getString(R.string.employee_label));
        contractModel.setEmployment(employmentList);

        List<String> taxes = new ArrayList<>();
        if (((CheckBox) findViewById(R.id.first)).isChecked())
            taxes.add(getString(R.string.income_tax_label));
        if (((CheckBox) findViewById(R.id.second)).isChecked())
            taxes.add(getString(R.string.corporation_tax_label));
        if (((CheckBox) findViewById(R.id.third)).isChecked())
            taxes.add(getString(R.string.value_added_tax_label));
        if (((CheckBox) findViewById(R.id.fourth)).isChecked())
            taxes.add(getString(R.string.employer_paye_label));
        if (((CheckBox) findViewById(R.id.fifth)).isChecked())
            taxes.add(getString(R.string.capital_gains_label));
        if (((CheckBox) findViewById(R.id.sixth)).isChecked())
            taxes.add(getString(R.string.relevant_contract_label));
        if (((CheckBox) findViewById(R.id.seventh)).isChecked())
            taxes.add(getString(R.string.environment_levy_label));
        if (((CheckBox) findViewById(R.id.eight)).isChecked())
            taxes.add(getString(R.string.divided_withholding_label));
        if (otherCheck.isChecked())
            taxes.add(getString(R.string.other_label));

        contractModel.setOccupation(occupationEditText.getText().toString());
        contractModel.setMessage(getString(R.string.contract_mail_message));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        signatureFile.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        contractModel.setOther(((EditText) findViewById(R.id.other_edit_id)).getText().toString());
        contractModel.setTaxes(taxes);
        contractModel.setSignature(byteArray);

        loadingScreen.show();

        Retrofit retrofit = RetrofitClientInstance.getInstance();
        final InterfaceAPI api = retrofit.create(InterfaceAPI.class);

        Call<Object> call = api.sendContract(contractModel);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (!response.isSuccessful())
                    Toast.makeText(ContractActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                //upload PPS and ID to Dropbox
                try {
                    uploadFiles();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                loadingScreen.hide();
                Toast.makeText(ContractActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadFiles() throws IOException {
        Map<String, String> uploadList = new HashMap<>();
        uploadList.put("PPS_FRONT", ppsFileFront);
        uploadList.put("PPS_BACK", ppsFileBack);
        uploadList.put("ID", idFile);
        uploadList.put("MARRIAGE", marriageCertificateFile);
        UploadAsyncTask uploadMultipleFilesTask = new UploadAsyncTask(
                nameEditText.getText().toString(),
                uploadList,
                null,
                UploadAsyncTask.UploadType.MULTIPLE);
        uploadMultipleFilesTask.execute();
        UploadAsyncTask uploadOneFileTask = new UploadAsyncTask(
                nameEditText.getText().toString(),
                FileUtils.createFile(generateUserInfo(), nameEditText.getText().toString() + "_info"),
                this,
                UploadAsyncTask.UploadType.ONE);
        uploadOneFileTask.execute();
    }

    private String generateUserInfo() {
        String message = "Name: " + contractModel.getName()
                + "\nAddress: " + contractModel.getAddress()
                + "\nPPS: " + contractModel.getPpsNumber()
                + "\nmail: " + contractModel.getEmail()
                + "\nOccupation: " + contractModel.getOccupation()
                + "\nPhone: " + phoneNumberEditText.getText().toString()
                + "\nIBAN: " + bankAccount.getText().toString()
                + "\nMarital status: " + contractModel.getMaritalStatus();
        String noOfKids = ((EditText) findViewById(R.id.married_id).findViewById(R.id.number_kids_id)).getText().toString();
        if (!noOfKids.equals(""))
            message = message + "\nNumber of kids: " + noOfKids;
        message = message + "\nDate of birth: " + contractModel.getBirthday()
                + "\nContract date: " + contractModel.getDate();
        return message;
    }

    @SuppressLint("ObsoleteSdkInt")
    private void pickPictureFromGallery(int requestId) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openFilePicker(requestId);
            } else {
                if (requestId == ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_FRONT) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_PPS_FRONT);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_ID);
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void openCamera(int requestId) {
        if (Build.VERSION.SDK_INT >= 23 && checkPermissions(requestId))
            takePicture(requestId);
    }

    private void takePicture(int requestId) {
        file = new File(ImageUtils.getImagePath(nameEditText.getText().toString() + requestId));
        Uri outputFileUri = FileProvider.getUriForFile(this,
                "com.app.tributum.fragment.invoices.provider", file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, requestId);
    }

    private void openFilePicker(int requestId) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                    setImageToContractFile(R.id.pps_front_layout_id, ppsFileFront);
                }
                break;
            case ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_BACK:
                if (resultCode == Activity.RESULT_OK) {
                    ppsFileBack = ImageUtils.getFilePathFromGallery(data);
                    setImageToContractFile(R.id.pps_back_layout_id, ppsFileBack);
                }
                break;
            case ConstantsUtils.SELECTED_PICTURE_REQUEST_ID:
                if (resultCode == Activity.RESULT_OK) {
                    idFile = ImageUtils.getFilePathFromGallery(data);
                    setImageToContractFile(R.id.id_layout_id, idFile);
                }
                break;
            case ConstantsUtils.SELECTED_PICTURE_REQUEST_MARRIAGE:
                if (resultCode == Activity.RESULT_OK) {
                    marriageCertificateFile = ImageUtils.getFilePathFromGallery(data);
                    setImageToContractFile(R.id.marriage_layout_id, marriageCertificateFile);
                }
                break;
            case ConstantsUtils.CAMERA_REQUEST_PPS_FRONT:
                if (resultCode == Activity.RESULT_OK) {
                    ppsFileFront = file.getAbsolutePath();
                    setImageToContractFile(R.id.pps_front_layout_id, ppsFileFront);
                }
                break;
            case ConstantsUtils.CAMERA_REQUEST_PPS_BACK:
                if (resultCode == Activity.RESULT_OK) {
                    ppsFileBack = file.getAbsolutePath();
                    setImageToContractFile(R.id.pps_back_layout_id, ppsFileBack);
                }
                break;
            case ConstantsUtils.CAMERA_REQUEST_ID:
                if (resultCode == Activity.RESULT_OK) {
                    idFile = file.getAbsolutePath();
                    setImageToContractFile(R.id.id_layout_id, idFile);
                }
                break;
            case ConstantsUtils.CAMERA_REQUEST_MARRIAGE:
                if (resultCode == Activity.RESULT_OK) {
                    marriageCertificateFile = file.getAbsolutePath();
                    setImageToContractFile(R.id.marriage_layout_id, marriageCertificateFile);
                }
                break;
            default:
                break;
        }
    }

    private void setImageToContractFile(int id, String filePath) {
        ImageView imageView = ((ImageView) findViewById(id).findViewById(R.id.preview_image_id));
        imageView.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load("file://" + filePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f).into(imageView);
        findViewById(id).findViewById(R.id.contract_file_layout_id).setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_PPS_FRONT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openFilePicker(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_FRONT);
                break;
            case ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_PPS_BACK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openFilePicker(ConstantsUtils.SELECTED_PICTURE_REQUEST_PPS_BACK);
                break;
            case ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_ID:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openFilePicker(ConstantsUtils.SELECTED_PICTURE_REQUEST_ID);
                break;
            case ConstantsUtils.STORAGE_PERMISSION_REQUEST_CODE_MARRIAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openFilePicker(ConstantsUtils.SELECTED_PICTURE_REQUEST_MARRIAGE);
                break;
            case ConstantsUtils.MULTIPLE_PERMISSIONS_PPS_FRONT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    takePicture(ConstantsUtils.CAMERA_REQUEST_PPS_FRONT);
                break;
            case ConstantsUtils.MULTIPLE_PERMISSIONS_PPS_BACK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    takePicture(ConstantsUtils.CAMERA_REQUEST_PPS_BACK);
                break;
            case ConstantsUtils.MULTIPLE_PERMISSIONS_ID:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    takePicture(ConstantsUtils.CAMERA_REQUEST_ID);
                break;
            case ConstantsUtils.MULTIPLE_PERMISSIONS_MARRIAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    takePicture(ConstantsUtils.CAMERA_REQUEST_MARRIAGE);
                break;
        }
    }

    @Override
    public void onDrawingStarted() {
        isSignatureSet = true;
    }

    private void handleClearImageClick() {
        clearFieldsInPps();
        ppsFileFront = null;
        ppsFileBack = null;
        idFile = null;
        marriageCertificateFile = null;
        signatureFile = null;
        if (isSignatureSet) {
            isSignatureSet = false;
            signatureDraw.clear();
        }
        removeImageToContractFile(R.id.pps_front_layout_id);
        removeImageToContractFile(R.id.pps_back_layout_id);
        removeImageToContractFile(R.id.id_layout_id);

        ((CheckBox) findViewById(R.id.first)).setChecked(false);
        ((CheckBox) findViewById(R.id.second)).setChecked(false);
        ((CheckBox) findViewById(R.id.third)).setChecked(false);
        ((CheckBox) findViewById(R.id.fourth)).setChecked(false);
        ((CheckBox) findViewById(R.id.fifth)).setChecked(false);
        ((CheckBox) findViewById(R.id.sixth)).setChecked(false);
        ((CheckBox) findViewById(R.id.seventh)).setChecked(false);
        ((CheckBox) findViewById(R.id.eight)).setChecked(false);
        ((CheckBox) findViewById(R.id.ninth)).setChecked(false);
    }

    private void clearFieldsInPps() {
        nameEditText.setText("");
        addressEditText.setText("");
        ppsNumberEditText.setText("");
        emailEditText.setText("");
        occupationEditText.setText("");
        phoneNumberEditText.setText("");
        bankAccount.setText("");
        birthday.setText("");
        contractDate.setText("");
        otherCheck.setText("");
    }

    private boolean checkPermissions(int requestId) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : ConstantsUtils.PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(this, permission);
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
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), permission);
            return false;
        }
        return true;
    }

    private void clearFormStarted() {
        if (TributumAppHelper.getBooleanSetting(AppKeysValues.CONTRACT_FORM_STARTED)) {
            TributumAppHelper.saveSetting(AppKeysValues.CONTRACT_FORM_STARTED, AppKeysValues.FALSE);
        }
    }

    private void showCloseContractDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.work_in_progress))
                .setMessage(getString(R.string.contract_in_progress_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void onTaskCompleted() {
        loadingScreen.hide();
        Toast.makeText(ContractActivity.this, getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (TributumAppHelper.getBooleanSetting(AppKeysValues.CONTRACT_FORM_STARTED)) {
            showCloseContractDialog();
        } else if (fileChooser.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            collapseBottomSheet();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        clearFormStarted();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        clearFormStarted();
        super.onDestroy();
    }
}