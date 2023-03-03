package com.app.tributum.activity.vat;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.app.tributum.utils.StatusBarUtils;
import com.app.tributum.utils.UtilsGeneral;
import com.app.tributum.utils.animation.AnimUtils;
import com.app.tributum.utils.animation.CustomAnimatorListener;
import com.app.tributum.utils.ui.FileUtils;
import com.app.tributum.utils.ui.LoadingScreen;
import com.app.tributum.utils.ui.RequestSent;
import com.app.tributum.utils.ui.UiUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.Date;
import java.util.List;

public class VatActivity extends AppCompatActivity implements VatView, AsyncListener {

    private VatAdapter invoicesAdapter;

    private ImageView previewImage;

    private View previewLayout;

    private EditText name;

    private EditText payerEmail;

    private LoadingScreen loadingScreen;

    private EditText startingMonth;

    private EditText endingMonth;

    private VatPresenterImpl presenter;

    private BottomSheetBehavior<View> fileChooser;

    private RequestSent requestSent;

    private RecyclerView invoicesRecyclerView;

    private ScrollView scrollView;

    private View privatesText;

    private RecyclerView privatesRecyclerView;

    private VatAdapter privatesAdapter;

    private String fileName;

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

    @SuppressLint({"CutPasteId", "ClickableViewAccessibility"})
    private void setupViews() {
        previewImage = findViewById(R.id.image_preview_id);
        previewLayout = findViewById(R.id.preview_layout_id);
        name = findViewById(R.id.payer_edit_text);
        payerEmail = findViewById(R.id.payer_email_edit_text);
        startingMonth = findViewById(R.id.start_month_edit_text);
        endingMonth = findViewById(R.id.end_month_edit_text);
        scrollView = findViewById(R.id.vat_scroll_view_id);
        privatesText = findViewById(R.id.privates_layout_id);

        name.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        payerEmail.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        startingMonth.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        endingMonth.setImeOptions(EditorInfo.IME_ACTION_DONE);

        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), R.drawable.ic_icon_loader_vat, R.color.vat_1);
        loadingScreen.setText(getString(R.string.might_take_pictures));
        requestSent = new RequestSent(findViewById(android.R.id.content), R.drawable.request_sent_vat, getString(R.string.vat_receipts_sent), presenter);

        invoicesRecyclerView = findViewById(R.id.invoices_recycler_id);
        invoicesRecyclerView.setHasFixedSize(true);
        invoicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        invoicesAdapter = new VatAdapter(this, presenter.getInvoicesList(), presenter, presenter, false);
        invoicesRecyclerView.setAdapter(invoicesAdapter);

        privatesRecyclerView = findViewById(R.id.privates_recycler_id);
        privatesRecyclerView.setHasFixedSize(true);
        privatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        privatesAdapter = new VatAdapter(this, presenter.getPrivatesList(), presenter, presenter, true);
        privatesRecyclerView.setAdapter(privatesAdapter);

        findViewById(R.id.remove_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onRemovePhotoClick();
            }
        });

        findViewById(R.id.vat_send_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSendClick(name.getText().toString().trim(),
                        payerEmail.getText().toString().trim(),
                        startingMonth.getText().toString().trim(),
                        endingMonth.getText().toString().trim());
            }
        });

        invoicesRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return presenter.onRecyclerViewTouch(event);
            }
        });

        privatesRecyclerView.setOnTouchListener(new View.OnTouchListener() {
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
        findViewById(R.id.privates_checkbox_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPrivatesClick();
            }
        });
        findViewById(R.id.privates_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPrivatesClick();
            }
        });
    }

    private void scrollInvoicesListToBottom() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                invoicesRecyclerView.scrollToPosition(invoicesAdapter.getItemCount() - 1);
                scrollView.scrollTo(0, invoicesRecyclerView.getBottom());
            }
        }, 100);
    }

    private void scrollPrivatesListToBottom() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                privatesRecyclerView.scrollToPosition(privatesAdapter.getItemCount() - 1);
                scrollView.scrollTo(0, privatesRecyclerView.getBottom());
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
    public void addItemToInvoicesList(VatModel model) {
        invoicesAdapter.addItemToList(model);
        scrollInvoicesListToBottom();
    }

    @Override
    public void addItemToPrivatesList(VatModel vatModel) {
        privatesAdapter.addItemToList(vatModel);
        scrollPrivatesListToBottom();
    }

    @Override
    public void removeItemFromInvoicesList(int photoClicked) {
        invoicesAdapter.remove(photoClicked);
    }

    @Override
    public void removeItemFromPrivatesList(int photoClicked) {
        privatesAdapter.remove(photoClicked);
    }

    @Override
    public void getFilesFromGalleryForInvoices(Uri imageUri) {
        invoicesAdapter.addItemToList(new VatModel(FileUtils.getPath(imageUri)));
        scrollInvoicesListToBottom();
    }

    @Override
    public void getFilesFromGalleryForPrivates(Uri imageUri) {
        privatesAdapter.addItemToList(new VatModel(FileUtils.getPath(imageUri)));
        scrollPrivatesListToBottom();
    }

    @Override
    public void startPdfCreation(List<VatModel> invoices, List<VatModel> privates) {
        fileName = startingMonth.getText().toString().replaceAll(" ", "_").trim()
                + "_" + endingMonth.getText().toString().replaceAll(" ", "_").trim() + "_" + System.currentTimeMillis();
        PdfAsyncTask asyncTask = new PdfAsyncTask(VatActivity.this, invoices, privates, name.getText().toString().trim(),
                fileName);
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
    public void takePhoto(String pictureImagePath, int state) {
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
        startActivityForResult(cameraIntent, state);
    }

    @Override
    public void openPhotoChooserIntent(int requestId) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestId);
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
        presenter.onTaskCompleted(name.getText().toString().trim(),
                payerEmail.getText().toString().trim(),
                startingMonth.getText().toString().trim(),
                endingMonth.getText().toString().trim(),
                fileName);
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
    public void setPrivatesStates(boolean state) {
        ((CheckBox) privatesText.findViewById(R.id.privates_checkbox_id)).setChecked(state);
    }

    @Override
    public void setPrivatesFont(int font) {
        UiUtils.setFontFamily(font, findViewById(R.id.add_privates_id));
    }

    @Override
    public void setRecyclerViewVisibility(int visibility) {
        privatesRecyclerView.setVisibility(visibility);
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