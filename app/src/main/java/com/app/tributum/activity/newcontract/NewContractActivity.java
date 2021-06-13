package com.app.tributum.activity.newcontract;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import com.app.tributum.R;
import com.app.tributum.helper.DrawingView;
import com.app.tributum.utils.CustomTextWatcher;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.ui.CustomScrollView;
import com.app.tributum.utils.ui.LoadingScreen;
import com.app.tributum.utils.ui.RequestSent;
import com.app.tributum.utils.ui.UiUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;

public class NewContractActivity extends AppCompatActivity implements ContractView {

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

    private EditText contractDate;

    private EditText birthday;

    private CheckBox otherCheck;

    private BottomSheetBehavior fileChooser;

    private View previewLayout;

    private DrawingView signatureDraw;

    private EditText phoneNumberEditText;

    private EditText bankAccount;

    private NestedScrollView scrollView;

    private LoadingScreen loadingScreen;

    private RequestSent requestSent;

    private ContractPresenterImpl presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_contract_activity);
        StatusBarUtils.makeStatusBarTransparent(this);

        presenter = new ContractPresenterImpl(this);
        presenter.onCreate();
        setupViews();
    }

    private void setupViews() {
        scrollView = findViewById(R.id.scrollView);
        findViewById(R.id.contract_back_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackPressed();
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null && event.getAction() == MotionEvent.ACTION_MOVE) {
                    UtilsGeneral.hideSoftKeyboard(NewContractActivity.this);
                }
                return false;
            }
        });

        nameEditText = findViewById(R.id.name_edit_text);
        addressEditText = findViewById(R.id.address_edit_text);
        ppsNumberEditText = findViewById(R.id.pps_number_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        occupationEditText = findViewById(R.id.occupation_edit_text);
        previewLayout = findViewById(R.id.preview_layout_id);
        phoneNumberEditText = findViewById(R.id.phone_edit_text);
        bankAccount = findViewById(R.id.bank_edit_text);
        bankAccount.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPersonalInfo();
            }
        });
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(bankAccount, 34, true);

        nameEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        nameEditText.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.onFormStarted(
                        nameEditText.getText().toString(),
                        addressEditText.getText().toString(),
                        birthday.getText().toString(),
                        occupationEditText.getText().toString(),
                        phoneNumberEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        bankAccount.getText().toString());
            }
        });
        addressEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        addressEditText.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPersonalInfo();
            }
        });
        ppsNumberEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        phoneNumberEditText.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPersonalInfo();
            }
        });
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(ppsNumberEditText, 9, true);

        emailEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        emailEditText.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPersonalInfo();
            }
        });
        occupationEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        occupationEditText.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPersonalInfo();
            }
        });
        phoneNumberEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        phoneNumberEditText.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPersonalInfo();
            }
        });

        singleCheck = findViewById(R.id.single_checkbox);
        marriedCheck = findViewById(R.id.married_checkbox);
        divorcedCheck = findViewById(R.id.divorced_checkbox);
        cohabitingCheck = findViewById(R.id.cohabiting_checkbox);

        contractDate = findViewById(R.id.starting_date);
        birthday = findViewById(R.id.edittext_birthday_id);
        UtilsGeneral.setMaxLengthEditText(birthday, 10);
        UtilsGeneral.setMaxLengthEditText(contractDate, 10);

        findViewById(R.id.personal_info_id).findViewById(R.id.plus_id).setBackgroundResource(R.drawable.photo_holder_contract);
        findViewById(R.id.pps_front_image_holder_id).findViewById(R.id.plus_id).setBackgroundResource(R.drawable.photo_holder_contract);
        findViewById(R.id.pps_back_image_holder_id).findViewById(R.id.plus_id).setBackgroundResource(R.drawable.photo_holder_contract);
        findViewById(R.id.marriage_layout_id).findViewById(R.id.plus_id).setBackgroundResource(R.drawable.photo_holder_contract);

        birthday.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.beforeBirthdayChanged(s.length());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPersonalInfo();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterBirthdayChanged(s);
            }
        });
        contractDate.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterContractDateChanged(s);
                checkPersonalInfo();
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
        otherEditText.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.beforeOtherChanged();
            }
        });

        findViewById(R.id.contract_send_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.handleSendButtonClick(
                        nameEditText.getText().toString(),
                        addressEditText.getText().toString(),
                        birthday.getText().toString(),
                        occupationEditText.getText().toString(),
                        phoneNumberEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        bankAccount.getText().toString(),
                        ppsNumberEditText.getText().toString(),
                        contractDate.getText().toString(),
                        ((EditText) findViewById(R.id.number_kids_id)).getText().toString(),
                        otherEditText.getText().toString()
                );
            }
        });

        parentView = findViewById(R.id.signature_drawing_view);
        signatureDraw = new DrawingView(NewContractActivity.this, presenter);
        parentView.addView(signatureDraw);

        findViewById(R.id.delete_signature_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onClearSignatureClick();
            }
        });

        singleCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSingleClicked();
                checkPersonalInfo();
            }
        });

        findViewById(R.id.single_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSingleClicked();
                checkPersonalInfo();
            }
        });

        marriedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onMarriedClicked();
                checkPersonalInfo();
            }
        });

        findViewById(R.id.married_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onMarriedClicked();
                checkPersonalInfo();
            }
        });

        divorcedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDivorcedClick();
                checkPersonalInfo();
            }
        });

        findViewById(R.id.divorced_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDivorcedClick();
                checkPersonalInfo();
            }
        });

        cohabitingCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onCohabitingClicked();
                checkPersonalInfo();
            }
        });

        findViewById(R.id.cohabiting_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onCohabitingClicked();
                checkPersonalInfo();
            }
        });

        findViewById(R.id.self_employed_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSelfEmployeeClick();
                checkPersonalInfo();
            }
        });

        findViewById(R.id.self_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSelfEmployeeClick();
                checkPersonalInfo();
            }
        });

        findViewById(R.id.employee_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEmployeeClick();
                checkPersonalInfo();
            }
        });

        findViewById(R.id.employee_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEmployeeClick();
                checkPersonalInfo();
            }
        });

        setImageHolderColor(R.id.pps_front_image_holder_id);
        findViewById(R.id.pps_front_image_holder_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPpsFrontClicked();
            }
        });

        findViewById(R.id.pps_front_image_holder_id).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPpsFrontPreviewClicked();
            }
        });

        findViewById(R.id.pps_front_image_holder_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPpsFrontRemoveClicked();
                checkPersonalInfo();
            }
        });

        setImageHolderColor(R.id.pps_back_image_holder_id);
        findViewById(R.id.pps_back_image_holder_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPpsBackClicked();
            }
        });

        findViewById(R.id.pps_back_image_holder_id).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPpsBackPreviewClicked();
            }
        });

        findViewById(R.id.pps_back_image_holder_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPpsBackRemoveClicked();
            }
        });

        setImageHolderColor(R.id.personal_info_id);
        findViewById(R.id.personal_info_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onIdClicked();
            }
        });

        findViewById(R.id.personal_info_id).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onIdPreviewClicked();
            }
        });

        findViewById(R.id.personal_info_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onIdRemoveClicked();
                checkPersonalInfo();
            }
        });

        setImageHolderColor(R.id.marriage_layout_id);
        findViewById(R.id.marriage_layout_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onMarriageCertificateClicked();
            }
        });

        findViewById(R.id.marriage_layout_id).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onMarriagePreviewClicked();
            }
        });

        findViewById(R.id.marriage_layout_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onMarriageRemoveClicked();
                checkPersonalInfo();
            }
        });

        setupFilesLayout();
        setupCheckboxes();

        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), R.drawable.ic_icon_loader_contract);
        requestSent = new RequestSent(findViewById(android.R.id.content), R.drawable.request_sent_contract, getString(R.string.contract_sent_label), presenter);
    }

    private void setImageHolderColor(int viewId) {
        ((ImageView) findViewById(viewId).findViewById(R.id.contract_plus_id)).setImageResource(R.drawable.ic_btn_add_photo_contract);
        ((ImageView) findViewById(viewId).findViewById(R.id.preview_thumbnail_id)).setImageResource(R.drawable.ic_btn_view_photo_contract);
        ((ImageView) findViewById(viewId).findViewById(R.id.remove__thumbnail_photo_id)).setImageResource(R.drawable.ic_btn_remove_photo_contract);
        findViewById(viewId).findViewById(R.id.photo_holder_divider_id)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.contract_1));
    }

    private void setupCheckboxes() {
        findViewById(R.id.first_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFirstCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFirstCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.second_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSecondCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSecondCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.third_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onThirdCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.third).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onThirdCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.fourth_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFourthCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.fourth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFourthCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.fifth_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFifthCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.fifth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFifthCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.sixth_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSixthCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.sixth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSixthCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.seventh_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSeventhCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.seventh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSeventhCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.eight_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEightCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.eight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEightCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.ninth_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onNinthCheckboxClicked();
                checkPersonalInfo();
            }
        });
        findViewById(R.id.ninth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onNinthCheckboxClicked();
                checkPersonalInfo();
            }
        });
    }

    private void setupFilesLayout() {
        // get the bottom sheet view
        RelativeLayout llBottomSheet = findViewById(R.id.file_chooser_id);
        fileChooser = BottomSheetBehavior.from(llBottomSheet);
        fileChooser.setDraggable(false);
        findViewById(R.id.file_chooser_top_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFileChooserTopClicked();
            }
        });
    }

    private void collapseBottomSheet() {
        fileChooser.setHideable(true);
        fileChooser.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void showFileChooser(int selectPictureRequest, int storagePermissionId, int takePictureRequest, int picturePermissionId) {
        fileChooser.setState(BottomSheetBehavior.STATE_EXPANDED);
        fileChooser.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED)
                    presenter.onBottomSheetExpanded();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        findViewById(R.id.add_from_gallery_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAddFromGalleryClicked(selectPictureRequest, storagePermissionId);
            }
        });
        findViewById(R.id.take_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onTakePhotoClicked(nameEditText.getText().toString(), takePictureRequest, picturePermissionId);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        presenter.onRequestPermissionResult(nameEditText.getText().toString(), requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void openFilePreview(String fileName) {
        previewLayout.setVisibility(View.VISIBLE);
        ImageView previewImage = findViewById(R.id.image_preview_id);
        Glide.with(this).load("file://" + fileName).into(previewImage);

        findViewById(R.id.remove_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onRemovePhotoClicked(fileName);
            }
        });
    }

    @Override
    public void resetPpsFrontLayout() {
        findViewById(R.id.pps_front_image_holder_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetPpsBackLayout() {
        findViewById(R.id.pps_back_image_holder_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetIdLayout() {
        findViewById(R.id.personal_info_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
        checkPersonalInfo();
    }

    @Override
    public void resetMarriageCertificateLayout() {
        findViewById(R.id.marriage_layout_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
        checkPersonalInfo();
    }

    @Override
    public void closePreview() {
        previewLayout.setVisibility(View.GONE);
    }

    @Override
    public void showPersonalInfoLayout() {
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        findViewById(R.id.personal_info_layout_id).setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePersonalInfoLayout() {
        findViewById(R.id.personal_info_layout_id).setVisibility(View.GONE);
    }

    @Override
    public void showEmploymentInfoLayout() {
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        findViewById(R.id.employment_info_layout_id).setVisibility(View.VISIBLE);
        checkPersonalInfo();
    }

    @Override
    public void hideEmploymentInfoLayout() {
        findViewById(R.id.employment_info_layout_id).setVisibility(View.GONE);
    }

    @Override
    public void showSignatureLayout() {
        ((CustomScrollView) scrollView).setScrollingEnabled(false);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        findViewById(R.id.signature_layout_id).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSignatureLayout() {
        ((CustomScrollView) scrollView).setScrollingEnabled(true);
        findViewById(R.id.signature_layout_id).setVisibility(View.GONE);
    }

    @Override
    public void setFileChooserToVisible() {
        findViewById(R.id.file_chooser_top_id).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideBottomSheet() {
        collapseBottomSheet();
    }

    @Override
    public void showTaxesView() {
        View taxesView = findViewById(R.id.taxes_id);
        taxesView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideTaxesView() {
        View taxesView = findViewById(R.id.taxes_id);
        taxesView.setVisibility(View.GONE);
    }

    @Override
    public void changeSelfEmployedState(boolean state) {
        CheckBox selfEmployed = findViewById(R.id.self_employed_id);
        selfEmployed.setChecked(state);
    }

    @Override
    public void changeEmployeeState(boolean state) {
        CheckBox employee = findViewById(R.id.employee_id);
        employee.setChecked(state);
    }

    @Override
    public void hideMaritalLayout() {
        findViewById(R.id.married_id).setVisibility(View.GONE);
    }

    @Override
    public void showMaritalLayout() {
        View maritalLayout = findViewById(R.id.married_id);
        maritalLayout.setVisibility(View.VISIBLE);
        maritalLayout.findViewById(R.id.marriage_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onMarriageCertificateClicked();
            }
        });
    }

    private void checkPersonalInfo() {
        presenter.checkPersonalValidation(nameEditText.getText().toString(),
                addressEditText.getText().toString(),
                birthday.getText().toString(),
                occupationEditText.getText().toString(),
                phoneNumberEditText.getText().toString(),
                emailEditText.getText().toString(),
                bankAccount.getText().toString(),
                ppsNumberEditText.getText().toString(),
                contractDate.getText().toString());
    }


    @Override
    public void changeSingleState(boolean check) {
        singleCheck.setChecked(check);
    }

    @Override
    public void changeMarriedState(boolean check) {
        marriedCheck.setChecked(check);
    }

    @Override
    public void changeDivorcedState(boolean check) {
        divorcedCheck.setChecked(check);
    }

    @Override
    public void changeCohabitingState(boolean check) {
        cohabitingCheck.setChecked(check);
    }

    @Override
    public void clearSignature() {
        signatureDraw.clear();
    }

    @Override
    public void setBirthdayText(String string) {
        birthday.setText(string);
    }

    @Override
    public void moveBirthdayCursorToEnd() {
        birthday.setSelection(birthday.getText().length());
    }

    @Override
    public void moveContractCursorToEnd() {
        contractDate.setSelection(contractDate.getText().length());
    }

    @Override
    public void setContractDateText(String s) {
        contractDate.setText(s);
    }

    @Override
    public void showLoadingScreen() {
        loadingScreen.show();
    }

    @Override
    public void hideLoadingScreen() {
        loadingScreen.hide();
    }

    @Override
    public void showRequestSentScreen() {
        requestSent.show();
    }

    @Override
    public void hideKeyboard() {
        UtilsGeneral.hideSoftKeyboard(this);
    }

    @Override
    public void closeActivity() {
        finish();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(NewContractActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setTitle(String title) {
        ((TextView) findViewById(R.id.contract_title_id)).setText(title);
    }

    @Override
    public void setSubtitle(String subtitle) {
        ((TextView) findViewById(R.id.contract_subtitle_id)).setText(subtitle);
    }

    @Override
    public void setConfirmationButtonText(String string) {
        ((TextView) findViewById(R.id.contract_send_id)).setText(string);
    }

    @Override
    public void showClearButton() {
        findViewById(R.id.delete_signature_id).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideClearButton() {
        findViewById(R.id.delete_signature_id).setVisibility(View.GONE);
    }

    @Override
    public void setSendDisabled() {
        findViewById(R.id.contract_send_id).setEnabled(false);
    }

    @Override
    public void setSendEnabled() {
        findViewById(R.id.contract_send_id).setEnabled(true);
    }

    @Override
    public void setFirstCheckboxState(boolean state) {
        ((CheckBox) findViewById(R.id.first)).setChecked(state);
    }

    @Override
    public void setSecondCheckboxState(boolean state) {
        ((CheckBox) findViewById(R.id.second)).setChecked(state);
    }

    @Override
    public void setThirdCheckboxState(boolean state) {
        ((CheckBox) findViewById(R.id.third)).setChecked(state);
    }

    @Override
    public void setFourthCheckboxState(boolean state) {
        ((CheckBox) findViewById(R.id.fourth)).setChecked(state);
    }

    @Override
    public void setFifthCheckboxState(boolean state) {
        ((CheckBox) findViewById(R.id.fifth)).setChecked(state);
    }

    @Override
    public void setSixthCheckboxState(boolean state) {
        ((CheckBox) findViewById(R.id.sixth)).setChecked(state);
    }

    @Override
    public void setSeventhCheckboxState(boolean state) {
        ((CheckBox) findViewById(R.id.seventh)).setChecked(state);
    }

    @Override
    public void setEightCheckboxState(boolean state) {
        ((CheckBox) findViewById(R.id.eight)).setChecked(state);
    }

    @Override
    public void setNinthCheckboxState(boolean state) {
        ((CheckBox) findViewById(R.id.ninth)).setChecked(state);
    }

    @Override
    public void setFocusOnOther() {
        EditText other = findViewById(R.id.other_edit_id);
        other.setVisibility(View.VISIBLE);
        UtilsGeneral.setFocusOnInput(other);
    }

    @Override
    public void hideOther() {
        EditText other = findViewById(R.id.other_edit_id);
        UtilsGeneral.removeFocusFromInput(this, other);
        other.setVisibility(View.GONE);
    }

    @Override
    public void openFilePicker(int requestId) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
        checkPersonalInfo();
    }

    @Override
    public void setPpsFrontImage(String ppsFileFront) {
        findViewById(R.id.pps_front_image_holder_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load("file://" + ppsFileFront)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.pps_front_image_holder_id).findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setPpsBackImage(String ppsFileBack) {
        findViewById(R.id.pps_back_image_holder_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load("file://" + ppsFileBack)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.pps_back_image_holder_id).findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setIdImage(String idFile) {
        findViewById(R.id.personal_info_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load("file://" + idFile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.personal_info_id).findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setMarriageCertificateImage(String marriageCertificateFile) {
        findViewById(R.id.marriage_layout_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load("file://" + marriageCertificateFile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.marriage_layout_id).findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestOnePermission(int requestId) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestId);
    }

    @Override
    public void takePicture(int requestId, File file, String pictureImagePath) {
        Uri outputFileUri = FileProvider.getUriForFile(this,
                "com.app.tributum.fragment.invoices.provider", file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, requestId);
    }

    @Override
    public int hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    @Override
    public void requestListOfPermissions(String[] permissions, int requestCode) {
        requestPermissions(permissions, requestCode);
    }

    @Override
    public void setDrawingCacheEnabled() {
        parentView.setDrawingCacheEnabled(true);
    }

    @Override
    public Bitmap getSignatureFile() {
        return parentView.getDrawingCache();
    }

    @Override
    public void selectSingle() {
        UiUtils.setFontFamily(R.font.manrope_bold, findViewById(R.id.single_text_id));
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.married_text_id));
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.divorced_text_id));
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.cohabiting_text_id));
    }

    @Override
    public void selectMarriage() {
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.single_text_id));
        UiUtils.setFontFamily(R.font.manrope_bold, findViewById(R.id.married_text_id));
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.divorced_text_id));
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.cohabiting_text_id));
    }

    @Override
    public void selectDivorced() {
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.single_text_id));
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.married_text_id));
        UiUtils.setFontFamily(R.font.manrope_bold, findViewById(R.id.divorced_text_id));
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.cohabiting_text_id));
    }

    @Override
    public void selectCohabiting() {
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.single_text_id));
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.married_text_id));
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(R.id.divorced_text_id));
        UiUtils.setFontFamily(R.font.manrope_bold, findViewById(R.id.cohabiting_text_id));
    }

    @Override
    public void selectText(int textId) {
        UiUtils.setFontFamily(R.font.manrope_bold, findViewById(textId));
    }

    @Override
    public void deselectText(int textId) {
        UiUtils.setFontFamily(R.font.manrope_medium, findViewById(textId));
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }
}