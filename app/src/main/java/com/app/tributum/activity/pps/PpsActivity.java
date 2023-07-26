package com.app.tributum.activity.pps;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.app.tributum.R;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.utils.CustomTextWatcher;
import com.app.tributum.utils.ImageUtils;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.animation.AnimUtils;
import com.app.tributum.utils.animation.CustomAnimatorListener;
import com.app.tributum.utils.ui.LoadingScreen;
import com.app.tributum.utils.ui.RequestSent;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
//import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

public class PpsActivity extends AppCompatActivity implements PpsView {

    private EditText firstNameEditText;

    private EditText lastNameEditText;

    private EditText momNameEditText;

    private EditText addressEditText;

    private EditText eircode;

    private EditText emailEditText;

    private BottomSheetBehavior<View> fileChooser;

    private View previewLayout;

    private EditText phoneNumberEditText;

    private EditText ownerPhoneNumberEditText;

    private NestedScrollView scrollView;

    private LoadingScreen loadingScreen;

    private RequestSent requestSent;

    private PpsPresenterImpl presenter;

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            presenter.handleCropping(result.getUriFilePath(getApplicationContext(), true));
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsGeneral.changeLocaleForContext(this, TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        setContentView(R.layout.pps_activity);
        StatusBarUtils.makeStatusBarTransparent(this);

        presenter = new PpsPresenterImpl(this);
        setupViews();
    }

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
    private void setupViews() {
        scrollView = findViewById(R.id.pps_scrollView_id);
        findViewById(R.id.pps_back_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackPressed();
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null && event.getAction() == MotionEvent.ACTION_MOVE) {
                    UtilsGeneral.hideSoftKeyboard(PpsActivity.this);
                }
                return false;
            }
        });

        firstNameEditText = findViewById(R.id.first_name_edit_text);
        lastNameEditText = findViewById(R.id.last_name_edit_text);
        momNameEditText = findViewById(R.id.mom_name_edit_text);
        addressEditText = findViewById(R.id.address_edit_text);
        eircode = findViewById(R.id.eircode_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        phoneNumberEditText = findViewById(R.id.phone_edit_text);
        ownerPhoneNumberEditText = findViewById(R.id.owner_number_edit_text);
        previewLayout = findViewById(R.id.preview_layout_id);

        firstNameEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        lastNameEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        momNameEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        addressEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        eircode.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        ownerPhoneNumberEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        emailEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        findViewById(R.id.cnp_id).findViewById(R.id.plus_id).setBackgroundResource(R.drawable.photo_holder_pps);
        findViewById(R.id.bill_id).findViewById(R.id.plus_id).setBackgroundResource(R.drawable.photo_holder_pps);
        findViewById(R.id.letter_id).findViewById(R.id.plus_id).setBackgroundResource(R.drawable.photo_holder_pps);

        eircode.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                presenter.beforeEircodeChanged(s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.afterEircodeChanged(s);
            }
        });

        findViewById(R.id.pps_send_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.handleSendButtonClick(
                        firstNameEditText.getText().toString().trim(),
                        lastNameEditText.getText().toString().trim(),
                        momNameEditText.getText().toString().trim(),
                        addressEditText.getText().toString(),
                        eircode.getText().toString(),
                        emailEditText.getText().toString(),
                        phoneNumberEditText.getText().toString(),
                        ownerPhoneNumberEditText.getText().toString()
                );
            }
        });

        setImageHolderColor(R.id.bill_id);
        findViewById(R.id.bill_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBillClicked();
            }
        });

        findViewById(R.id.bill_id).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBillPreviewClicked();
            }
        });

        findViewById(R.id.bill_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBillRemoveClicked();
            }
        });

        setImageHolderColor(R.id.letter_id);
        findViewById(R.id.letter_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onLetterClicked();
            }
        });

        findViewById(R.id.letter_id).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onLetterPreviewClicked();
            }
        });

        findViewById(R.id.letter_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onLetterRemoveClicked();
            }
        });

        setImageHolderColor(R.id.cnp_id);
        findViewById(R.id.cnp_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onIdClicked();
            }
        });

        findViewById(R.id.cnp_id).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onIdPreviewClicked();
            }
        });

        findViewById(R.id.cnp_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onIdRemoveClicked();
            }
        });

        setupFilesLayout();

        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), R.drawable.ic_icon_loader_pps, R.color.pps);
        loadingScreen.setText(getString(R.string.might_take));
        requestSent = new RequestSent(findViewById(android.R.id.content), R.drawable.request_sent_pps, getString(R.string.request_sent), presenter);
    }

    private void setImageHolderColor(int viewId) {
        ((ImageView) findViewById(viewId).findViewById(R.id.contract_plus_id)).setImageResource(R.drawable.ic_btn_add_photo_pps);
        ((ImageView) findViewById(viewId).findViewById(R.id.preview_image_id)).setImageResource(R.drawable.ic_btn_view_photo_pps);
        ((ImageView) findViewById(viewId).findViewById(R.id.delete_image_id)).setImageResource(R.drawable.ic_btn_remove_photo_pps);
        findViewById(viewId).findViewById(R.id.photo_holder_divider_id)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.pps));
    }

    private void scrollToView(View view) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, view.getTop());
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
                presenter.onTakePhotoClicked(firstNameEditText.getText().toString().trim(), takePictureRequest);
            }
        });
    }

    @Override
    public void openFilePreview(String fileName) {
        previewLayout.setVisibility(View.VISIBLE);
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
    public void resetBillLayout() {
        findViewById(R.id.bill_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetLetterLayout() {
        findViewById(R.id.letter_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void resetIdLayout() {
        findViewById(R.id.cnp_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void closePreview() {
        previewLayout.setVisibility(View.GONE);
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
    public void setEircodeText(String string) {
        eircode.setText(string);
    }

    @Override
    public void moveEircodeCursorToEnd(int length) {
        eircode.setSelection(length);
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
        requestSent.show(false);
    }

    @Override
    public void closeActivity() {
        finish();
    }

    @Override
    public void showToast(int stringId) {
        Toast.makeText(PpsActivity.this, getResources().getString(stringId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void openFilePicker(int requestId) {
        startActivityForResult(ImageUtils.getImageChooserIntent(), requestId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void startCrop(Uri imageUri) {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = true;
        cropImageOptions.imageSourceIncludeCamera = true;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(imageUri, cropImageOptions);
        cropImage.launch(cropImageContractOptions);
    }

    @Override
    public void setBillImage(String bill) {
        findViewById(R.id.bill_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(bill)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.bill_id).findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setLetterImage(String letter) {
        findViewById(R.id.letter_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(letter)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.letter_id).findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setIdImage(String idFile) {
        findViewById(R.id.cnp_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(idFile)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.cnp_id).findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void takePicture(int requestId, File file, String pictureImagePath) {
        presenter.setFilePath(pictureImagePath);
        startActivityForResult(ImageUtils.getTakePhotoIntent(file), requestId);
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
    public void focusOnMomName() {
        UtilsGeneral.setFocusOnInput(momNameEditText);
        scrollToView(momNameEditText);
    }

    @Override
    public void focusOnAddress() {
        UtilsGeneral.setFocusOnInput(addressEditText);
        scrollToView(addressEditText);
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
    public void scrollToId() {
        scrollToView(findViewById(R.id.cnp_id));
    }

    @Override
    public void scrollToBill() {
        scrollToView(findViewById(R.id.bill_id));
    }

    @Override
    public void scrollToLetter() {
        scrollToView(findViewById(R.id.letter_id));
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