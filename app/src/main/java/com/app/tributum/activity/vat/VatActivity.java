package com.app.tributum.activity.vat;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tributum.R;
import com.app.tributum.application.AppKeysValues;
import com.app.tributum.application.TributumAppHelper;
import com.app.tributum.fragment.invoices.PdfAsyncTask;
import com.app.tributum.fragment.invoices.adapter.InvoicesAdapter;
import com.app.tributum.fragment.invoices.listener.AsyncListener;
import com.app.tributum.fragment.invoices.model.InvoiceModel;
import com.app.tributum.utils.ConstantsUtils;
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

    private InvoicesAdapter adapter;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vat);
        StatusBarUtils.makeStatusBarTransparent(this);

        presenter = new VatPresenterImpl(this);
        presenter.onCreate();
        setupViews();
    }

    private void setupViews() {
        previewImage = findViewById(R.id.image_preview_id);
        previewLayout = findViewById(R.id.preview_layout_id);
        name = findViewById(R.id.payer_edit_text);
        payerEmail = findViewById(R.id.payer_email_edit_text);
        TextView sendButton = findViewById(R.id.invoices_send_id);
        startingMonth = findViewById(R.id.start_month_edit_text);
        endingMonth = findViewById(R.id.end_month_edit_text);

        name.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        payerEmail.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        startingMonth.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        endingMonth.setImeOptions(EditorInfo.IME_ACTION_DONE);

        loadingScreen = new LoadingScreen(this, findViewById(android.R.id.content), R.color.vat_1);
        requestSent = new RequestSent(this, findViewById(android.R.id.content), R.drawable.request_sent_vat, getString(R.string.vat_receipts_sent));

        RecyclerView recyclerView = findViewById(R.id.invoices_recycler_id);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new InvoicesAdapter(this, presenter.getList(), presenter, presenter);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.remove_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onRemovePhotoClick();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSendClick(name.getText().toString(),
                        payerEmail.getText().toString(),
                        startingMonth.getText().toString(),
                        endingMonth.getText().toString());
            }
        });

        findViewById(R.id.invoices_recycler_id).setOnTouchListener(new View.OnTouchListener() {
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
    }

    @Override
    public void openBottomSheet() {
        fileChooser.setState(BottomSheetBehavior.STATE_EXPANDED);
        AnimUtils.getFadeInAnimator(findViewById(R.id.file_chooser_top_id),
                AnimUtils.DURATION_200,
                AnimUtils.NO_DELAY,
                null,
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        findViewById(R.id.file_chooser_top_id).setVisibility(View.VISIBLE);
                    }
                }).start();
    }

    @Override
    public void collapseBottomSheet() {
        fileChooser.setHideable(true);
        fileChooser.setState(BottomSheetBehavior.STATE_HIDDEN);
        AnimUtils.getFadeOutAnimator(findViewById(R.id.file_chooser_top_id),
                AnimUtils.DURATION_200,
                AnimUtils.NO_DELAY,
                null,
                null).start();
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
    public void addItemToList(InvoiceModel model) {
        adapter.addItemToList(model);
    }

    @Override
    public void removeItemFromList(int photoClicked) {
        adapter.remove(photoClicked);
    }

    @Override
    public void getFilesFromGallery(Uri imageUri) {
        adapter.addItemToList(new InvoiceModel(FileUtils.getPath(imageUri)));
    }

    @Override
    public void startPdfCreation() {
        PdfAsyncTask asyncTask = new PdfAsyncTask(VatActivity.this, VatActivity.this, presenter.getList(), name.getText().toString(),
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
                "com.app.tributum.fragment.invoices.provider", file);
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
        findViewById(R.id.vat_scroll_view_id).setVisibility(View.GONE);
    }

    @Override
    public void hidePreview() {
        previewLayout.setVisibility(View.GONE);
        previewImage.setImageResource(0);
        findViewById(R.id.vat_scroll_view_id).setVisibility(View.VISIBLE);
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
    public void closeActivity() {
        finish();
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }
}