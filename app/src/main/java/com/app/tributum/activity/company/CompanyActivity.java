package com.app.tributum.activity.company;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.app.tributum.R;
import com.app.tributum.activity.company.model.Company;
import com.app.tributum.activity.company.model.Secretary;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.FintrexAppHelper;
import com.app.tributum.utils.CustomTextWatcher;
import com.app.tributum.utils.ImageUtils;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.animation.AnimUtils;
import com.app.tributum.utils.animation.CustomAnimatorListener;
import com.app.tributum.utils.ui.LoadingScreen;
import com.app.tributum.utils.ui.RequestSent;
import com.app.tributum.utils.ui.UiUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;

public class CompanyActivity extends AppCompatActivity implements CompanyView {

    private CompanyPresenterImpl presenter;

    private NestedScrollView scrollView;

    private LoadingScreen loadingScreen;

    private RequestSent requestSent;
    private BottomSheetBehavior<View> fileChooser;

    private View previewLayout;

    private final ActivityResultLauncher<PickVisualMediaRequest> director1PpsFrontPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onDirector1PpsFrontPicker(uri);
            });

    private final ActivityResultLauncher<PickVisualMediaRequest> director1PpsBackPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onDirector1PpsBackPicker(uri);
            });

    private final ActivityResultLauncher<PickVisualMediaRequest> director1IdPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onDirector1IdPicker(uri);
            });

    private final ActivityResultLauncher<PickVisualMediaRequest> director1PassPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onDirector1PassPicker(uri);
            });

    private final ActivityResultLauncher<PickVisualMediaRequest> director2PpsFrontPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onDirector2PpsFrontPicker(uri);
            });

    private final ActivityResultLauncher<PickVisualMediaRequest> director2PpsBackPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onDirector2PpsBackPicker(uri);
            });

    private final ActivityResultLauncher<PickVisualMediaRequest> director2IdPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onDirector2IdPicker(uri);
            });

    private final ActivityResultLauncher<PickVisualMediaRequest> director2PassPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onDirector2PassPicker(uri);
            });

    private final ActivityResultLauncher<PickVisualMediaRequest> director3PpsFrontPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onDirector3PpsFrontPicker(uri);
            });

    private final ActivityResultLauncher<PickVisualMediaRequest> director3PpsBackPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onDirector3PpsBackPicker(uri);
            });

    private final ActivityResultLauncher<PickVisualMediaRequest> director3IdPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onDirector3IdPicker(uri);
            });

    private final ActivityResultLauncher<PickVisualMediaRequest> director3PassPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onDirector3PassPicker(uri);
            });

    private ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            presenter.handleCropping(result.getUriFilePath(getApplicationContext(), true));
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsGeneral.changeLocaleForContext(this, FintrexAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        setContentView(R.layout.activity_company);
        StatusBarUtils.makeStatusBarTransparent(this);

        presenter = new CompanyPresenterImpl(this);

        setupViewsAndClicks();
    }

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
    private void setupViewsAndClicks() {
        scrollView = findViewById(R.id.scrollView);
        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), R.drawable.ic_icon_loader_company, R.color.company_1);
        requestSent = new RequestSent(findViewById(android.R.id.content), R.drawable.request_sent_company, getString(R.string.request_sent), presenter);

        previewLayout = findViewById(R.id.preview_layout_id);

        UtilsGeneral.setMaxLengthEditText(findViewById(R.id.director_1_layout_id).findViewById(R.id.director_birthday_id), 10);
        UtilsGeneral.setMaxLengthEditText(findViewById(R.id.director_2_layout_id).findViewById(R.id.director_birthday_id), 10);
        UtilsGeneral.setMaxLengthEditText(findViewById(R.id.director_3_layout_id).findViewById(R.id.director_birthday_id), 10);
        UtilsGeneral.setMaxLengthEditText(findViewById(R.id.secretary_birthday_id), 10);
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_edit_text), 9, true);
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_edit_text), 9, true);
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_edit_text), 9, true);
        UtilsGeneral.setMaxLengthAndAllCapsToEditText(findViewById(R.id.secretary_pps_edit_text), 9, true);

        ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_birthday_id)).addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.beforeBirthdayChanged(s.length(), R.id.director_1_layout_id);
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterBirthdayChanged(s, R.id.director_1_layout_id);
            }
        });

        ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_birthday_id)).addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.beforeBirthdayChanged(s.length(), R.id.director_2_layout_id);
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterBirthdayChanged(s, R.id.director_2_layout_id);
            }
        });

        ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_birthday_id)).addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.beforeBirthdayChanged(s.length(), R.id.director_3_layout_id);
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterBirthdayChanged(s, R.id.director_3_layout_id);
            }
        });

        ((EditText) findViewById(R.id.secretary_birthday_id)).addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.beforeBirthdayChanged(s.length(), R.id.secretary_birthday_id);
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterBirthdayChanged(s, R.id.secretary_birthday_id);
            }
        });
        findViewById(R.id.company_back_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackPressed();
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null && event.getAction() == MotionEvent.ACTION_MOVE) {
                    UtilsGeneral.hideSoftKeyboard(CompanyActivity.this);
                }
                return false;
            }
        });

        findViewById(R.id.director_2_layout_checkbox_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onDirector2Click();
            }
        });
        findViewById(R.id.director_2_checkbox_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onDirector2Click();
            }
        });
        findViewById(R.id.director_3_layout_checkbox_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onDirector3Click();
            }
        });
        findViewById(R.id.director_3_checkbox_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onDirector3Click();
            }
        });
        findViewById(R.id.agree_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onAgreeTermsClick(false);
            }
        });
        findViewById(R.id.agree_checkbox_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onAgreeTermsClick(true);
            }
        });
        findViewById(R.id.company_send_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMainButtonClick();
            }
        });
        setupFilesLayout();
        setupClicksForHolders();
    }

    private void setupClicksForHolders() {
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_front).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector1PpsFrontClick();
            }
        });
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_back).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector1PpsBackClick();
            }
        });
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector1IdClick();
            }
        });
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pass).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector1PassClick();
            }
        });
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_front).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector2PpsFrontClick();
            }
        });
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_back).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector2PpsBackClick();
            }
        });
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector2IdClick();
            }
        });
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pass).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector2PassClick();
            }
        });
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_front).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector3PpsFrontClick();
            }
        });
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_back).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector3PpsBackClick();
            }
        });
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector3IdClick();
            }
        });
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pass).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector3PassClick();
            }
        });
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_front).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector1PpsFrontPreview();
            }
        });
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_back).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector1PpsBackPreview();
            }
        });
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_id).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector1IdPreview();
            }
        });
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pass).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector1PassPreview();
            }
        });
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_front).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector1PpsFrontDelete();
            }
        });
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_back).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector1PpsBackDelete();
            }
        });
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector1IdDelete();
            }
        });
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pass).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector1PassDelete();
            }
        });
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_front).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector2PpsFrontPreview();
            }
        });
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_back).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector2PpsBackPreview();
            }
        });
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_id).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector2IdPreview();
            }
        });
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pass).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector2PassPreview();
            }
        });
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_front).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector2PpsFrontDelete();
            }
        });
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_back).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector2PpsBackDelete();
            }
        });
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector2IdDelete();
            }
        });
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pass).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector2PassDelete();
            }
        });
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_front).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector3PpsFrontPreview();
            }
        });
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_back).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector3PpsBackPreview();
            }
        });
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_id).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector3IdPreview();
            }
        });
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pass).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector3PassPreview();
            }
        });
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_front).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector3PpsFrontDelete();
            }
        });
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_back).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector3PpsBackDelete();
            }
        });
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector3IdDelete();
            }
        });
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pass).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDirector3PassDelete();
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
    public void showFileChooser(int selectPictureRequest, int takePictureRequest) {
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
                presenter.onTakePhotoClicked(
                        ((EditText) findViewById(R.id.director_first_name_edit_text)).getText().toString().trim(), takePictureRequest);
            }
        });
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
    public void takePicture(int requestId, File file, String pictureImagePath) {
        presenter.setFilePath(pictureImagePath);
        startActivityForResult(ImageUtils.getTakePhotoIntent(file), requestId);
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
    public void closePreview() {
        previewLayout.setVisibility(View.GONE);
        findViewById(R.id.progress_layout_id).setVisibility(View.VISIBLE);
    }

    @Override
    public void pickDirector1PpsFront() {
        director1PpsFrontPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void pickDirector1PpsBack() {
        director1PpsBackPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void pickDirector1Id() {
        director1IdPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void pickDirector1Pass() {
        director1PassPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void pickDirector2PpsFront() {
        director2PpsFrontPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void pickDirector2PpsBack() {
        director2PpsBackPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void pickDirector2Id() {
        director2IdPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void pickDirector2Pass() {
        director2PassPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void pickDirector3PpsFront() {
        director3PpsFrontPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void pickDirector3PpsBack() {
        director3PpsBackPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void pickDirector3Id() {
        director3IdPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void pickDirector3Pass() {
        director3PassPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void resetDirector1PpsFrontLayout() {
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_front)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetDirector1PpsBackLayout() {
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_back)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetDirector1IdLayout() {
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_id)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetDirector1PassLayout() {
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pass)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetDirector2PpsFrontLayout() {
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_front)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetDirector2PpsBackLayout() {
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_back)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetDirector2IdLayout() {
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_id)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetDirector2PassLayout() {
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pass)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetDirector3PpsFrontLayout() {
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_front)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetDirector3PpsBackLayout() {
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_back)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetDirector3IdLayout() {
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_id)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetDirector3PassLayout() {
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pass)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void startCrop(Uri uri) {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = true;
        cropImageOptions.imageSourceIncludeCamera = true;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(uri, cropImageOptions);
        cropImage.launch(cropImageContractOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setDirector1PpsFrontImage(String result) {
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_front)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(result)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_front)
                        .findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setDirector1PpsBackImage(String result) {
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_back)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(result)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_back)
                        .findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setDirector1IdImage(String result) {
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_id)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(result)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_id)
                        .findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setDirector1PassImage(String result) {
        findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pass)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(result)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pass)
                        .findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setDirector2PpsFrontImage(String result) {
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_front)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(result)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_front)
                        .findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setDirector2PpsBackImage(String result) {
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_back)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(result)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_back)
                        .findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setDirector2IdImage(String result) {
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_id)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(result)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_id)
                        .findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setDirector2PassImage(String result) {
        findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pass)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(result)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pass)
                        .findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setDirector3PpsFrontImage(String result) {
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_front)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(result)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_front)
                        .findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setDirector3PpsBackImage(String result) {
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_back)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(result)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_back)
                        .findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setDirector3IdImage(String result) {
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_id)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(result)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_id)
                        .findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setDirector3PassImage(String result) {
        findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pass)
                .findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(result)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pass)
                        .findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void showSecondDirector() {
        ((CheckBox) findViewById(R.id.director_2_checkbox_id)).setChecked(true);
        findViewById(R.id.director_2_layout_id).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSecondDirector() {
        ((CheckBox) findViewById(R.id.director_2_checkbox_id)).setChecked(false);
        findViewById(R.id.director_2_layout_id).setVisibility(View.GONE);
    }

    @Override
    public void showThirdDirector() {
        ((CheckBox) findViewById(R.id.director_3_checkbox_id)).setChecked(true);
        findViewById(R.id.director_3_layout_id).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideCompanyView() {
        AnimUtils.getTranslationXAnimator(findViewById(R.id.company_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.company_layout_id).setVisibility(View.GONE);
                    }
                },
                0, -UiUtils.getScreenWidth()).start();
    }

    @Override
    public void showCompanyView() {
        AnimUtils.getTranslationXAnimator(findViewById(R.id.company_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        findViewById(R.id.company_layout_id).setVisibility(View.VISIBLE);
                    }
                },
                0).start();
    }

    @Override
    public void hideDirectorViewToLeft() {
        AnimUtils.getTranslationXAnimator(findViewById(R.id.directors_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.directors_layout_id).setVisibility(View.GONE);
                    }
                },
                -UiUtils.getScreenWidth()).start();
    }

    @Override
    public void hideDirectorViewToRight() {
        setCompletionProgress(R.id.second_progress_id, false);
        AnimUtils.getTranslationXAnimator(findViewById(R.id.directors_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.directors_layout_id).setVisibility(View.GONE);
                    }
                },
                UiUtils.getScreenWidth()).start();
    }

    @Override
    public void showDirectorViewFromRight() {
        setCompletionProgress(R.id.second_progress_id, true);
        AnimUtils.getTranslationXAnimator(findViewById(R.id.directors_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        scrollView.scrollTo(0, 0);
                        findViewById(R.id.directors_layout_id).setVisibility(View.VISIBLE);
                    }
                },
                UiUtils.getScreenWidth(), 0).start();
    }

    @Override
    public void showDirectorViewFromLeft() {
        AnimUtils.getTranslationXAnimator(findViewById(R.id.directors_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        findViewById(R.id.directors_layout_id).setVisibility(View.VISIBLE);
                    }
                },
                0).start();
    }

    @Override
    public void hideSecretaryView() {
        setCompletionProgress(R.id.third_progress_id, false);
        AnimUtils.getTranslationXAnimator(findViewById(R.id.secretary_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.secretary_layout_id).setVisibility(View.GONE);
                    }
                },
                UiUtils.getScreenWidth()).start();
    }

    @Override
    public void showSecretaryView() {
        setCompletionProgress(R.id.third_progress_id, true);
        AnimUtils.getTranslationXAnimator(findViewById(R.id.secretary_layout_id),
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        scrollView.scrollTo(0, 0);
                        findViewById(R.id.secretary_layout_id).setVisibility(View.VISIBLE);
                    }
                },
                UiUtils.getScreenWidth(), 0).start();
    }

    @Override
    public void setConfirmationButtonText(int stringRes) {
        ((TextView) findViewById(R.id.company_send_text_id)).setText(stringRes);
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
    public void hideThirdDirector() {
        ((CheckBox) findViewById(R.id.director_3_checkbox_id)).setChecked(false);
        findViewById(R.id.director_3_layout_id).setVisibility(View.GONE);
    }

    @Override
    public void showToast(int stringResource) {
        Toast.makeText(this, getString(stringResource), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void closeActivity() {
        finish();

    }

    @Override
    public void hideKeyboard() {
        UtilsGeneral.hideSoftKeyboard(this);
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
    public void showRequestSent() {
        requestSent.show();
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public Company getCompanyDetails() {
        return new Company(((EditText) findViewById(R.id.full_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.sur_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.email_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.phone_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.company_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.proposed_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.street_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.town_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.country_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.company_activities_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.registered_edit_text)).getText().toString().trim());
    }

    @Override
    public void getDirector1Details() {
        presenter.setDirector1Details(
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_first_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_sur_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_birthday_id)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_pps_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_nationality_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_occupation_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_home_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_other_directorships_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_1_layout_id).findViewById(R.id.director_other_company_edit_text)).getText().toString().trim());
    }

    @Override
    public void getDirector2Details() {
        presenter.setDirector2Details(
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_first_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_sur_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_birthday_id)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_pps_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_nationality_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_occupation_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_home_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_other_directorships_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_2_layout_id).findViewById(R.id.director_other_company_edit_text)).getText().toString().trim());
    }

    @Override
    public void getDirector3Details() {
        presenter.setDirector3Details(
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_first_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_sur_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_birthday_id)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_pps_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_nationality_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_occupation_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_home_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_other_directorships_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.director_3_layout_id).findViewById(R.id.director_other_company_edit_text)).getText().toString().trim());
    }

    @Override
    public Secretary getSecretaryDetails() {
        return new Secretary(((EditText) findViewById(R.id.secretary_first_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.secretary_sur_name_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.secretary_birthday_id)).getText().toString().trim(),
                ((EditText) findViewById(R.id.secretary_email_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.secretary_pps_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.secretary_nationality_edit_text)).getText().toString().trim(),
                ((EditText) findViewById(R.id.secretary_home_edit_text)).getText().toString().trim());
    }

    @Override
    public void setBirthdayText(String text, int id) {
        if (id != R.id.secretary_birthday_id)
            ((EditText) findViewById(id).findViewById(R.id.director_birthday_id)).setText(text);
        else
            ((EditText) findViewById(R.id.secretary_birthday_id)).setText(text);
    }

    @Override
    public void moveBirthdayCursorToEnd(int id) {
        if (id != R.id.secretary_birthday_id)
            ((EditText) findViewById(id).findViewById(R.id.director_birthday_id))
                    .setSelection(((EditText) findViewById(id).findViewById(R.id.director_birthday_id)).getText().length());
        else
            ((EditText) findViewById(R.id.secretary_birthday_id))
                    .setSelection(((EditText) findViewById(R.id.secretary_birthday_id)).getText().length());
    }

    @Override
    public void checkTheAgreeBox(boolean acceptTerms) {
        ((CheckBox) findViewById(R.id.agree_checkbox_id)).setChecked(acceptTerms);
    }
}