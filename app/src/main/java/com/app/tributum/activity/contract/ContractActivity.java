package com.app.tributum.activity.contract;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import com.app.tributum.R;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.helper.DrawingView;
import com.app.tributum.utils.CustomTextWatcher;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.animation.AnimUtils;
import com.app.tributum.utils.animation.CustomAnimatorListener;
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

public class ContractActivity extends AppCompatActivity implements ContractView {

    private EditText firstNameEditText;

    private EditText lastNameEditText;

    private EditText address1EditText;

    private EditText address2EditText;

    private EditText address3EditText;

    private EditText eircode;

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
        UtilsGeneral.changeLocaleForContext(this, TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        setContentView(R.layout.contract_activity);
        StatusBarUtils.makeStatusBarTransparent(this);

        presenter = new ContractPresenterImpl(this);
        setupViews();
        presenter.onCreate();
    }

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
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
                    UtilsGeneral.hideSoftKeyboard(ContractActivity.this);
                }
                return false;
            }
        });

        firstNameEditText = findViewById(R.id.first_name_edit_text);
        lastNameEditText = findViewById(R.id.last_name_edit_text);
        address1EditText = findViewById(R.id.address_1_edit_text);
        address2EditText = findViewById(R.id.address_2_edit_text);
        address3EditText = findViewById(R.id.address_3_edit_text);
        eircode = findViewById(R.id.eircode_edit_text);
        ppsNumberEditText = findViewById(R.id.pps_number_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        occupationEditText = findViewById(R.id.occupation_edit_text);
        previewLayout = findViewById(R.id.preview_layout_id);
        phoneNumberEditText = findViewById(R.id.phone_edit_text);
        bankAccount = findViewById(R.id.bank_edit_text);
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(bankAccount, 34, true);

        firstNameEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        lastNameEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        address1EditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        address2EditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        address3EditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        eircode.setImeOptions(EditorInfo.IME_ACTION_NEXT);
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
        UtilsGeneral.setMaxLengthEditText(contractDate, 10);

        findViewById(R.id.birthday_image_id).setOnClickListener(new View.OnClickListener() {
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
                                presenter.onBirthdayDateSet(year, monthOfYear, dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        findViewById(R.id.contract_date_image_id).setOnClickListener(new View.OnClickListener() {
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
                                presenter.onContractDateSet(year, monthOfYear, dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

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
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterBirthdayChanged(s);
            }
        });
        contractDate.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.beforeContractDateChanged(s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterContractDateChanged(s);
            }
        });

        EditText otherEditText = findViewById(R.id.other_edit_id);
        UtilsGeneral.setMaxLengthEditText(otherEditText, 60);
        CheckBox otherCheck = findViewById(R.id.ninth);
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

        findViewById(R.id.contract_send_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.handleSendButtonClick(
                        firstNameEditText.getText().toString().trim(),
                        lastNameEditText.getText().toString().trim(),
                        address1EditText.getText().toString(),
                        address2EditText.getText().toString(),
                        address3EditText.getText().toString(),
                        eircode.getText().toString(),
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
        signatureDraw = new DrawingView(ContractActivity.this, presenter);
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
            }
        });

        findViewById(R.id.single_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSingleClicked();
            }
        });

        marriedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onMarriedClicked();
            }
        });

        findViewById(R.id.married_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onMarriedClicked();
            }
        });

        divorcedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDivorcedClick();
            }
        });

        findViewById(R.id.divorced_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDivorcedClick();
            }
        });

        cohabitingCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onCohabitingClicked();
            }
        });

        findViewById(R.id.cohabiting_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onCohabitingClicked();
            }
        });

        findViewById(R.id.self_employed_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSelfEmployeeClick();
            }
        });

        findViewById(R.id.self_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSelfEmployeeClick();
            }
        });

        findViewById(R.id.employee_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEmployeeClick();
            }
        });

        findViewById(R.id.employee_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEmployeeClick();
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
            }
        });

        setupFilesLayout();
        setupCheckboxes();

        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), R.drawable.ic_icon_loader_contract, R.color.contract_1);
        loadingScreen.setText(getString(R.string.might_take));
        requestSent = new RequestSent(findViewById(android.R.id.content), R.drawable.request_sent_contract, getString(R.string.contract_sent_label), presenter);
    }

    private void setImageHolderColor(int viewId) {
        ((ImageView) findViewById(viewId).findViewById(R.id.contract_plus_id)).setImageResource(R.drawable.ic_btn_add_photo_contract);
        ((ImageView) findViewById(viewId).findViewById(R.id.preview_image_id)).setImageResource(R.drawable.ic_btn_view_photo_contract);
        ((ImageView) findViewById(viewId).findViewById(R.id.delete_image_id)).setImageResource(R.drawable.ic_btn_remove_photo_contract);
        findViewById(viewId).findViewById(R.id.photo_holder_divider_id)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.contract_1));
    }

    private void scrollToView(View view) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, view.getTop());
            }
        });
    }

    private void setupCheckboxes() {
        findViewById(R.id.first_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFirstCheckboxClicked();
            }
        });
        findViewById(R.id.first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFirstCheckboxClicked();
            }
        });
        findViewById(R.id.second_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSecondCheckboxClicked();
            }
        });
        findViewById(R.id.second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSecondCheckboxClicked();
            }
        });
        findViewById(R.id.third_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onThirdCheckboxClicked();
            }
        });
        findViewById(R.id.third).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onThirdCheckboxClicked();
            }
        });
        findViewById(R.id.fourth_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFourthCheckboxClicked();
            }
        });
        findViewById(R.id.fourth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFourthCheckboxClicked();
            }
        });
        findViewById(R.id.fifth_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFifthCheckboxClicked();
            }
        });
        findViewById(R.id.fifth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFifthCheckboxClicked();
            }
        });
        findViewById(R.id.sixth_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSixthCheckboxClicked();
            }
        });
        findViewById(R.id.sixth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSixthCheckboxClicked();
            }
        });
        findViewById(R.id.seventh_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSeventhCheckboxClicked();
            }
        });
        findViewById(R.id.seventh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSeventhCheckboxClicked();
            }
        });
        findViewById(R.id.eight_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEightCheckboxClicked();
            }
        });
        findViewById(R.id.eight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onEightCheckboxClicked();
            }
        });
        findViewById(R.id.ninth_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onNinthCheckboxClicked();
            }
        });
        findViewById(R.id.ninth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onNinthCheckboxClicked();
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
        AnimUtils.getFadeOutAnimator(findViewById(R.id.file_chooser_top_id), AnimUtils.DURATION_200, AnimUtils.NO_DELAY, null,
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.file_chooser_top_id).setVisibility(View.GONE);
                    }
                }).start();
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
                presenter.onAddFromGalleryClicked(selectPictureRequest);
            }
        });
        findViewById(R.id.take_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onTakePhotoClicked(firstNameEditText.getText().toString().trim(), takePictureRequest);
            }
        });
    }

    @Override
    public void openFilePreview(String fileName) {
        previewLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.progress_layout_id).setVisibility(View.GONE);
        ImageView previewImage = findViewById(R.id.image_preview_id);
        Glide.with(this)
                .load("file://" + fileName)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(previewImage);

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
    }

    @Override
    public void resetMarriageCertificateLayout() {
        findViewById(R.id.marriage_layout_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void closePreview() {
        previewLayout.setVisibility(View.GONE);
        findViewById(R.id.progress_layout_id).setVisibility(View.VISIBLE);
    }

    @Override
    public void showPersonalInfoLayout() {
        AnimUtils.getTranslationXAnimator(findViewById(R.id.personal_info_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        findViewById(R.id.personal_info_layout_id).setVisibility(View.VISIBLE);
                    }
                },
                0).start();
    }

    @Override
    public void hidePersonalInfoLayout() {
        AnimUtils.getTranslationXAnimator(findViewById(R.id.personal_info_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.personal_info_layout_id).setVisibility(View.GONE);
                    }
                },
                0, -UiUtils.getScreenWidth()).start();
    }

    @Override
    public void showEmploymentInfoLayoutFromRight() {
        setCompletionProgress(R.id.second_progress_id, true);
        AnimUtils.getTranslationXAnimator(findViewById(R.id.employment_info_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        scrollView.scrollTo(0, 0);
                        findViewById(R.id.employment_info_layout_id).setVisibility(View.VISIBLE);
                    }
                },
                UiUtils.getScreenWidth(), 0).start();
    }

    @Override
    public void showEmploymentInfoLayoutFromLeft() {
        AnimUtils.getTranslationXAnimator(findViewById(R.id.employment_info_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        findViewById(R.id.employment_info_layout_id).setVisibility(View.VISIBLE);
                    }
                },
                0).start();
    }

    @Override
    public void hideEmploymentInfoLayoutToRight() {
        setCompletionProgress(R.id.second_progress_id, false);
        AnimUtils.getTranslationXAnimator(findViewById(R.id.employment_info_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.employment_info_layout_id).setVisibility(View.GONE);
                    }
                },
                UiUtils.getScreenWidth()).start();
    }

    @Override
    public void hideEmploymentInfoLayoutToLeft() {
        AnimUtils.getTranslationXAnimator(findViewById(R.id.employment_info_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.employment_info_layout_id).setVisibility(View.GONE);
                    }
                },
                -UiUtils.getScreenWidth()).start();
    }

    @Override
    public void showSignatureLayout() {
        setCompletionProgress(R.id.third_progress_id, true);
        ((CustomScrollView) scrollView).setScrollingEnabled(false);
        AnimUtils.getTranslationXAnimator(findViewById(R.id.signature_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        scrollView.scrollTo(0, 0);
                        findViewById(R.id.signature_layout_id).setVisibility(View.VISIBLE);
                    }
                },
                UiUtils.getScreenWidth(), 0).start();
    }

    @Override
    public void hideSignatureLayout() {
        setCompletionProgress(R.id.third_progress_id, false);
        ((CustomScrollView) scrollView).setScrollingEnabled(true);
        AnimUtils.getTranslationXAnimator(findViewById(R.id.signature_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.signature_layout_id).setVisibility(View.GONE);
                    }
                },
                UiUtils.getScreenWidth()).start();
    }

    @Override
    public void setFileChooserToVisible() {
        findViewById(R.id.file_chooser_top_id).setVisibility(View.VISIBLE);
        AnimUtils.getFadeInAnimator(findViewById(R.id.file_chooser_top_id), AnimUtils.DURATION_200, AnimUtils.NO_DELAY, null, null).start();
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

    private void setCompletionProgress(int progressId, boolean forward) {
        int progress = forward ? 100 : 0;
        AnimUtils.getProgressAnimator(findViewById(progressId),
                AnimUtils.DURATION_300,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                null,
                progress).start();
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
    public void closeActivity() {
        finish();
    }

    @Override
    public void showToast(int stringId) {
        Toast.makeText(ContractActivity.this, getResources().getString(stringId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setTitle(int stringId) {
        ((TextView) findViewById(R.id.contract_title_id)).setText(getResources().getString(stringId));
    }

    @Override
    public void setSubtitle(int stringId) {
        ((TextView) findViewById(R.id.contract_subtitle_id)).setText(getResources().getString(stringId));
    }

    @Override
    public void setConfirmationButtonText(int stringId) {
        ((TextView) findViewById(R.id.contract_send_id)).setText(getResources().getString(stringId));
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
    public void takePicture(int requestId, File file, String pictureImagePath) {
        Uri outputFileUri = FileProvider.getUriForFile(this,
                "com.app.tributum.activity.vat.provider", file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        presenter.setFilePath(pictureImagePath);
        startActivityForResult(cameraIntent, requestId);
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
    public void showAsteriskView() {
        findViewById(R.id.asterisk_text_view_id).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAsteriskView() {
        findViewById(R.id.asterisk_text_view_id).setVisibility(View.GONE);
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
    public void removeFocus() {
        UtilsGeneral.hideSoftKeyboard(this);
    }

    @Override
    public void focusOnFirstName() {
        UtilsGeneral.setFocusOnInput(firstNameEditText);
        scrollToView(firstNameEditText);
    }

    @Override
    public void focusOnLastName() {
        UtilsGeneral.setFocusOnInput(lastNameEditText);
        scrollToView(lastNameEditText);
    }

    @Override
    public void focusOnAddress1() {
        UtilsGeneral.setFocusOnInput(address1EditText);
        scrollToView(address1EditText);
    }

    @Override
    public void focusOnAddress2() {
        UtilsGeneral.setFocusOnInput(address2EditText);
        scrollToView(address2EditText);
    }

    @Override
    public void focusOnBirthday() {
        UtilsGeneral.setFocusOnInput(birthday);
        scrollToView(birthday);
    }

    @Override
    public void focusOnOccupation() {
        UtilsGeneral.setFocusOnInput(occupationEditText);
        scrollToView(occupationEditText);
    }

    @Override
    public void focusOnPhone() {
        UtilsGeneral.setFocusOnInput(phoneNumberEditText);
        scrollToView(phoneNumberEditText);
    }

    @Override
    public void focusOnEmail() {
        UtilsGeneral.setFocusOnInput(emailEditText);
        scrollToView(emailEditText);
    }

    @Override
    public void focusOnEircode() {
        UtilsGeneral.setFocusOnInput(eircode);
        scrollToView(eircode);
    }

    @Override
    public void focusOnPps() {
        UtilsGeneral.setFocusOnInput(ppsNumberEditText);
        scrollToView(ppsNumberEditText);
    }

    @Override
    public void focusOnContractDate() {
        UtilsGeneral.setFocusOnInput(contractDate);
        scrollToView(contractDate);
    }

    @Override
    public void focusOnOther() {
        UtilsGeneral.setFocusOnInput(findViewById(R.id.other_edit_id));
        scrollToView(findViewById(R.id.other_edit_id));
    }

    @Override
    public void scrollToPpsFront() {
        scrollToView(findViewById(R.id.pps_front_image_holder_id));
    }

    @Override
    public void scrollToId() {
        scrollToView(findViewById(R.id.personal_info_id));
    }

    @Override
    public void scrollToTaxes() {
        scrollToView(findViewById(R.id.taxes_id));
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}