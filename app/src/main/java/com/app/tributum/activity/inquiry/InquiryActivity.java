package com.app.tributum.activity.inquiry;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.tributum.R;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.utils.ConstantsUtils;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;

public class InquiryActivity extends AppCompatActivity implements InquiryView {

    private EditText name;

    private EditText email;

    private LoadingScreen loadingScreen;

    private EditText inquiryText;

    private InquiryPresenterImpl presenter;

    private BottomSheetBehavior<View> fileChooser;

    private RequestSent requestSent;

    private ScrollView scrollView;

    private View previewLayout;

    private ImageView previewImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsGeneral.changeLocaleForContext(this, TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        setContentView(R.layout.activity_inquiry);
        StatusBarUtils.makeStatusBarTransparent(this);

        presenter = new InquiryPresenterImpl(this);
        setupViews();
    }

    @SuppressLint("CutPasteId")
    private void setupViews() {
        RelativeLayout llBottomSheet = findViewById(R.id.file_chooser_id);
        fileChooser = BottomSheetBehavior.from(llBottomSheet);
        fileChooser.setDraggable(false);

        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), R.drawable.ic_icon_loader_inquiry, R.color.inquiry_1);
        requestSent = new RequestSent(findViewById(android.R.id.content), R.drawable.request_sent_inquiry, getString(R.string.inquiry_sent_label), presenter);

        ((ImageView) findViewById(R.id.inquiry_add_file_id).findViewById(R.id.contract_plus_id)).setImageResource(R.drawable.ic_btn_add_photo_inquiry);
        ((ImageView) findViewById(R.id.inquiry_add_file_id).findViewById(R.id.preview_image_id)).setImageResource(R.drawable.ic_btn_view_photo_inquiry);
        ((ImageView) findViewById(R.id.inquiry_add_file_id).findViewById(R.id.delete_image_id)).setImageResource(R.drawable.ic_btn_remove_photo_inquiry);
        findViewById(R.id.inquiry_add_file_id).findViewById(R.id.photo_holder_divider_id)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.inquiry_1));
        findViewById(R.id.inquiry_add_file_id).findViewById(R.id.plus_id).setBackgroundResource(R.drawable.photo_holder_inquiry);
        findViewById(R.id.inquiry_add_file_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPlusClick();
            }
        });
        findViewById(R.id.inquiry_add_file_id).findViewById(R.id.preview_thumbnail_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPreviewPhotoClick();
            }
        });

        findViewById(R.id.inquiry_add_file_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDeleteClick();
            }
        });

        scrollView = findViewById(R.id.inquiry_scroll_view_id);
        previewLayout = findViewById(R.id.preview_layout_id);
        name = findViewById(R.id.sender_edit_text_inquiry);
        email = findViewById(R.id.sender_email_edit_text_inquiry);
        inquiryText = findViewById(R.id.inquiry_edit_text_id);

        name.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        email.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        findViewById(R.id.inquiry_send_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSendClick(name.getText().toString().trim(), email.getText().toString().trim(),
                        inquiryText.getText().toString().trim());
            }
        });

        findViewById(R.id.inquiry_back_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackPressed();
            }
        });
    }

    private void scrollToEditText(View view) {
        if (view == null)
            return;

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, view.getTop());
            }
        });
    }

    @Override
    public void setFocusOnName() {
        UtilsGeneral.setFocusOnInput(name);
        scrollToEditText(name);
    }

    @Override
    public void setFocusOnEmail() {
        UtilsGeneral.setFocusOnInput(email);
        scrollToEditText(email);
    }

    @Override
    public void setFocusOnDescription() {
        UtilsGeneral.setFocusOnInput(inquiryText);
        scrollToEditText(inquiryText);
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
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRequestSent() {
        requestSent.show();
    }

    @Override
    public void showTopViewBottomSheet() {
        findViewById(R.id.file_chooser_top_id).setVisibility(View.VISIBLE);
        AnimUtils.getFadeInAnimator(findViewById(R.id.file_chooser_top_id),
                AnimUtils.DURATION_200,
                AnimUtils.NO_DELAY,
                null,
                null).start();
    }

    @Override
    public void showImagePreview(String filePath) {
        scrollView.setVisibility(View.GONE);
        previewLayout.setVisibility(View.VISIBLE);
        previewImage = findViewById(R.id.image_preview_id);
        Glide.with(this)
                .load(filePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(previewImage);

        findViewById(R.id.remove_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onRemovePhotoClick();
            }
        });
    }

    @Override
    public void hidePreview() {
        previewLayout.setVisibility(View.GONE);
        previewImage.setImageResource(0);
        scrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void openBottomSheet() {
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
                presenter.onAddFromGalleryClick();
            }
        });
        findViewById(R.id.take_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onTakePhotoClick(name.getText().toString().trim());
            }
        });
        findViewById(R.id.file_chooser_top_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onTopViewClick();
            }
        });
    }

    @Override
    public void openPhotoChooserIntent() {
        startActivityForResult(ImageUtils.getImageChooserIntent(), ConstantsUtils.SELECT_REQUEST_INQUIRY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void collapseBottomSheet() {
        fileChooser.setHideable(true);
        fileChooser.setState(BottomSheetBehavior.STATE_HIDDEN);
        AnimUtils.getFadeOutAnimator(findViewById(R.id.file_chooser_top_id),
                AnimUtils.DURATION_200,
                AnimUtils.NO_DELAY,
                null,
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.file_chooser_top_id).setVisibility(View.GONE);
                    }
                }).start();
    }

    @Override
    public void setImageInHolder(String fileName) {
        findViewById(R.id.inquiry_add_file_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(fileName)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(R.id.inquiry_add_file_id).findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void takePhoto(int requestId, File file, String picturePath) {
        presenter.setFilePath(picturePath);
        startActivityForResult(ImageUtils.getTakePhotoIntent(file), requestId);
    }

    @Override
    public void resetThumbnailLayout() {
        findViewById(R.id.inquiry_add_file_id).findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public void closeActivity() {
        finish();
    }
}