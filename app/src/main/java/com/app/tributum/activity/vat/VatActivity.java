package com.app.tributum.activity.vat;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tributum.R;
import com.app.tributum.activity.vat.adapter.VatAdapter;
import com.app.tributum.activity.vat.model.VatModel;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.listener.AsyncListener;
import com.app.tributum.thread.PdfAsyncTask;
import com.app.tributum.utils.ConstantsUtils;
import com.app.tributum.utils.CustomTextWatcher;
import com.app.tributum.utils.DialogUtils;
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.animation.AnimUtils;
import com.app.tributum.utils.animation.CustomAnimatorListener;
import com.app.tributum.utils.ui.FileUtils;
import com.app.tributum.utils.ui.LoadingScreen;
import com.app.tributum.utils.ui.RequestSent;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.Date;

public class VatActivity extends AppCompatActivity implements VatView, AsyncListener {

    private VatAdapter adapter;

    private ImageView previewImage;

    private View previewLayout;

    private EditText name;

    private EditText payerEmail;

    private LoadingScreen loadingScreen;

    private EditText startingMonth;

    private EditText endingMonth;

    private VatPresenterImpl presenter;

    private BottomSheetBehavior fileChooser;

    private RequestSent requestSent;

    private RecyclerView recyclerView;

    private ScrollView scrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilsGeneral.changeLocaleForContext(this, TributumAppHelper.getStringSetting(AppKeysValues.APP_LANGUAGE));
        setContentView(R.layout.activity_vat);
        StatusBarUtils.makeStatusBarTransparent(this);

        presenter = new VatPresenterImpl(this);
        presenter.onCreate();
        setupViews();
    }

    @SuppressLint("CutPasteId")
    private void setupViews() {
        previewImage = findViewById(R.id.image_preview_id);
        previewLayout = findViewById(R.id.preview_layout_id);
        name = findViewById(R.id.payer_edit_text);
        payerEmail = findViewById(R.id.payer_email_edit_text);
        startingMonth = findViewById(R.id.start_month_edit_text);
        endingMonth = findViewById(R.id.end_month_edit_text);
        scrollView = findViewById(R.id.vat_scroll_view_id);

        name.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        payerEmail.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        startingMonth.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        endingMonth.setImeOptions(EditorInfo.IME_ACTION_DONE);

        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), R.drawable.ic_icon_loader_vat);
        requestSent = new RequestSent(findViewById(android.R.id.content), R.drawable.request_sent_vat, getString(R.string.vat_receipts_sent), presenter);

        recyclerView = findViewById(R.id.invoices_recycler_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VatAdapter(this, presenter.getList(), presenter, presenter);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.remove_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onRemovePhotoClick();
            }
        });

        findViewById(R.id.vat_send_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSendClick(name.getText().toString(),
                        payerEmail.getText().toString(),
                        startingMonth.getText().toString(),
                        endingMonth.getText().toString());
            }
        });

        findViewById(R.id.invoices_recycler_id).setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return presenter.onRecyclerViewTouch(event);
            }
        });

        findViewById(R.id.vat_back_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackPressed();
            }
        });

        name.setText(TributumAppHelper.getStringSetting(AppKeysValues.INVOICE_NAME));
        payerEmail.setText(TributumAppHelper.getStringSetting(AppKeysValues.INVOICE_EMAIL));

        RelativeLayout llBottomSheet = findViewById(R.id.file_chooser_id);
        fileChooser = BottomSheetBehavior.from(llBottomSheet);
        fileChooser.setDraggable(false);
        findViewById(R.id.add_from_gallery_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAddFromGalleryClick();
            }
        });
        findViewById(R.id.take_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onTakePhotoClick();
            }
        });
        findViewById(R.id.file_chooser_top_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onTopViewClick();
            }
        });

        name.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateAddedInformation();
            }
        });
        payerEmail.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateAddedInformation();
            }
        });
        startingMonth.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateAddedInformation();
            }
        });
        endingMonth.addTextChangedListener(new CustomTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateAddedInformation();
            }
        });
    }

    private void validateAddedInformation() {
        presenter.onTextChanged(name.getText().toString(),
                payerEmail.getText().toString(),
                startingMonth.getText().toString(),
                endingMonth.getText().toString());
    }

    private void scrollListToBottom() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                scrollView.scrollTo(0, recyclerView.getBottom());
            }
        }, 100);
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
    public void addItemToList(VatModel model) {
        adapter.addItemToList(model);
        scrollListToBottom();
        validateAddedInformation();
    }

    @Override
    public void removeItemFromList(int photoClicked) {
        adapter.remove(photoClicked);
        validateAddedInformation();
    }

    @Override
    public void getFilesFromGallery(Uri imageUri) {
        adapter.addItemToList(new VatModel(FileUtils.getPath(imageUri)));
        scrollListToBottom();
        validateAddedInformation();
    }

    @Override
    public void startPdfCreation() {
        PdfAsyncTask asyncTask = new PdfAsyncTask(VatActivity.this, presenter.getList(), name.getText().toString(),
                startingMonth.getText().toString()
                        + "_" + endingMonth.getText().toString());
        asyncTask.execute();
    }

    @Override
    public void hideKeyboard() {
        UtilsGeneral.hideSoftKeyboard(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void takePhoto(String pictureImagePath) {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(pictureImagePath);
        Uri outputFileUri = FileProvider.getUriForFile(this,
                "com.app.tributum.activity.vat.provider", file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        presenter.setFilePath(pictureImagePath);
        startActivityForResult(cameraIntent, ConstantsUtils.CAMERA_REQUEST_INVOICES_ID);
    }

    @Override
    public void openPhotoChooserIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), ConstantsUtils.SELECT_PICTURES);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        presenter.onRequestPermissionsResult(requestCode, grantResults);
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void showImagePreview(String filePath) {
        previewLayout.setVisibility(View.VISIBLE);
        Glide.with(this).load("file://" + filePath).into(previewImage);
        scrollView.setVisibility(View.GONE);
    }

    @Override
    public void hidePreview() {
        previewLayout.setVisibility(View.GONE);
        previewImage.setImageResource(0);
        scrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(VatActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskCompleted() {
        presenter.onTaskCompleted(name.getText().toString(),
                payerEmail.getText().toString(),
                startingMonth.getText().toString(),
                endingMonth.getText().toString());
    }

    @Override
    public int checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    @Override
    public void takeUserToApPSettings() {
        DialogUtils.showPermissionDeniedDialog(this);
    }

    @Override
    public boolean shouldShowStorageRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public boolean shouldShowCameraRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
    }

    @Override
    public void disableSendButton() {
        findViewById(R.id.vat_send_text_id).setEnabled(false);
    }

    @Override
    public void enableSendButton() {
        findViewById(R.id.vat_send_text_id).setEnabled(true);
    }

    @Override
    public void setFocusOnName() {
        UtilsGeneral.setFocusOnInput(name);
        scrollToEditText(name);
    }

    @Override
    public void setFocusOnEmail() {
        UtilsGeneral.setFocusOnInput(payerEmail);
        scrollToEditText(payerEmail);
    }

    @Override
    public void setFocusOnStartingMonth() {
        UtilsGeneral.setFocusOnInput(startingMonth);
        scrollToEditText(startingMonth);
    }

    @Override
    public void setFocusOnEndingMonth() {
        UtilsGeneral.setFocusOnInput(endingMonth);
        scrollToEditText(endingMonth);
    }

    @Override
    public void closeActivity() {
        finish();
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    private void scrollToEditText(View view) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, view.getTop());
            }
        });
    }
}