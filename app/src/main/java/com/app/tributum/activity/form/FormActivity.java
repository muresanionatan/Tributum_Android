package com.app.tributum.activity.form;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tributum.R;
import com.app.tributum.activity.form.adapter.FormAdapter;
import com.app.tributum.activity.form.adapter.FormAdapterState;
import com.app.tributum.activity.vat.model.VatModel;
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

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class FormActivity extends AppCompatActivity implements FormView {
    private BottomSheetBehavior<View> fileChooser;
    private FormPresenterImpl presenter;
    private FormAdapter bsAdapter;
    private FormAdapter kidsAdapter;
    private FormAdapter expensesAdapter;
    private FormAdapter medicalAdapter;
    private LoadingScreen loadingScreen;
    private RequestSent requestSent;
    private final ActivityResultLauncher<PickVisualMediaRequest> pickBank =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(20), uris -> {
                if (!uris.isEmpty()) {
                    presenter.onBankSelected(uris);
                }
            });
    private final ActivityResultLauncher<PickVisualMediaRequest> pickKids =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(20), uris -> {
                if (!uris.isEmpty()) {
                    presenter.onKidsSelected(uris);
                }
            });
    private final ActivityResultLauncher<PickVisualMediaRequest> pickExpenses =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(20), uris -> {
                if (!uris.isEmpty()) {
                    presenter.onExpensesSelected(uris);
                }
            });
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedical =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(20), uris -> {
                if (!uris.isEmpty()) {
                    presenter.onMedicalSelected(uris);
                }
            });
    private final ActivityResultLauncher<PickVisualMediaRequest> pickRent =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onRentSelected(uri);
            });
    private final ActivityResultLauncher<PickVisualMediaRequest> pickRtb =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onRtbSelected(uri);
            });
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMarriage =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onMarriageSelected(uri);
            });
    private final ActivityResultLauncher<PickVisualMediaRequest> pickFisc1 =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onFisc1Selected(uri);
            });
    private final ActivityResultLauncher<PickVisualMediaRequest> pickFisc2 =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null)
                    presenter.onFisc2Selected(uri);
            });

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            presenter.handleCropping(result.getUriFilePath(getApplicationContext(), true));
        }
    });

    private final ActivityResultLauncher<Intent> pdfBankPickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                presenter.handlePdfSelected(result);
            });

    @SuppressLint({"ClickableViewAccessibility", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_activity);

        RelativeLayout llBottomSheet = findViewById(R.id.file_chooser_id);
        fileChooser = BottomSheetBehavior.from(llBottomSheet);
        fileChooser.setDraggable(false);

        presenter = new FormPresenterImpl(this);
        presenter.onCreate();

        loadingScreen = new LoadingScreen(findViewById(android.R.id.content), R.drawable.ic_icon_loader_vat, R.color.inquiry_1);
        loadingScreen.setText(getString(R.string.might_take_pictures));
        requestSent = new RequestSent(findViewById(android.R.id.content), R.drawable.request_sent_inquiry, getString(R.string.form_sent), presenter);

        RecyclerView bsRecyclerView = findViewById(R.id.form_bank_recycler_id);
        bsRecyclerView.setHasFixedSize(true);
        bsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bsAdapter = new FormAdapter(this, presenter.getBsList(), presenter, presenter, FormAdapterState.BANK);
        bsRecyclerView.setAdapter(bsAdapter);
        bsRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return presenter.onRecyclerViewTouch(event);
            }
        });

        RecyclerView childRecyclerView = findViewById(R.id.form_child_recycler_id);
        childRecyclerView.setHasFixedSize(true);
        childRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        kidsAdapter = new FormAdapter(this, presenter.getChildList(), presenter, presenter, FormAdapterState.KIDS);
        childRecyclerView.setAdapter(kidsAdapter);
        childRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return presenter.onRecyclerViewTouch(event);
            }
        });

        RecyclerView expensesRecyclerView = findViewById(R.id.form_expenses_recycler_id);
        expensesRecyclerView.setHasFixedSize(true);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        expensesAdapter = new FormAdapter(this, presenter.getExpensesList(), presenter, presenter, FormAdapterState.EXPENSES);
        expensesRecyclerView.setAdapter(expensesAdapter);
        expensesRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return presenter.onRecyclerViewTouch(event);
            }
        });

        RecyclerView medicalRecyclerView = findViewById(R.id.form_medical_recycler_id);
        medicalRecyclerView.setHasFixedSize(true);
        medicalRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        medicalAdapter = new FormAdapter(this, presenter.getMedicalList(), presenter, presenter, FormAdapterState.MEDICAL);
        medicalRecyclerView.setAdapter(medicalAdapter);
        medicalRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return presenter.onRecyclerViewTouch(event);
            }
        });
        setClicks();
    }

    private void setClicks() {
        Spinner dropdown = findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(FormActivity.this, android.R.layout.simple_spinner_dropdown_item, presenter.getFormYears());
        dropdown.setAdapter(adapter);
        dropdown.setSelection(adapter.getPosition(String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1)));

        findViewById(R.id.form_back_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackPressed();
            }
        });
        findViewById(R.id.yes_no_rent_id).findViewById(R.id.yes_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onBankYesClick();
            }
        });
        findViewById(R.id.yes_no_rent_id).findViewById(R.id.yes_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onBankYesClick();
            }
        });
        findViewById(R.id.yes_no_rent_id).findViewById(R.id.no_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onBankNoClick();
            }
        });
        findViewById(R.id.yes_no_rent_id).findViewById(R.id.no_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onBankNoClick();
            }
        });

        findViewById(R.id.married_yes_no_id).findViewById(R.id.yes_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMarriageYesClick();
            }
        });
        findViewById(R.id.married_yes_no_id).findViewById(R.id.yes_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMarriageYesClick();
            }
        });
        findViewById(R.id.married_yes_no_id).findViewById(R.id.no_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMarriageNoClick();
            }
        });
        findViewById(R.id.married_yes_no_id).findViewById(R.id.no_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMarriageNoClick();
            }
        });

        findViewById(R.id.expenses_yes_no_id).findViewById(R.id.yes_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onExpensesYesClick();
            }
        });
        findViewById(R.id.expenses_yes_no_id).findViewById(R.id.yes_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onExpensesYesClick();
            }
        });
        findViewById(R.id.expenses_yes_no_id).findViewById(R.id.no_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onExpensesNoClick();
            }
        });
        findViewById(R.id.expenses_yes_no_id).findViewById(R.id.no_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onExpensesNoClick();
            }
        });

        findViewById(R.id.medical_yes_no_id).findViewById(R.id.yes_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMedicalYesClick();
            }
        });
        findViewById(R.id.medical_yes_no_id).findViewById(R.id.yes_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMedicalYesClick();
            }
        });
        findViewById(R.id.medical_yes_no_id).findViewById(R.id.no_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMedicalNoClick();
            }
        });
        findViewById(R.id.medical_yes_no_id).findViewById(R.id.no_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMedicalNoClick();
            }
        });
        findViewById(R.id.file_chooser_top_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onTopViewClick();
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
                presenter.onTakePhotoClick();
            }
        });
        findViewById(R.id.add_pdf_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAddPdfClick();
            }
        });
        findViewById(R.id.rent_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPlusCLick(FormState.RENT);
            }
        });
        findViewById(R.id.rtb_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPlusCLick(FormState.RTB);
            }
        });
        findViewById(R.id.marriage_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPlusCLick(FormState.MARRIAGE);
            }
        });
        findViewById(R.id.fisc_1_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPlusCLick(FormState.FISC_1);
            }
        });
        findViewById(R.id.fisc_2_id).findViewById(R.id.plus_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPlusCLick(FormState.FISC_2);
            }
        });
        findViewById(R.id.rent_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDeleteClickForSingleFile(FormState.RENT);
            }
        });
        findViewById(R.id.rtb_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDeleteClickForSingleFile(FormState.RTB);
            }
        });
        findViewById(R.id.marriage_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDeleteClickForSingleFile(FormState.MARRIAGE);
            }
        });
        findViewById(R.id.fisc_1_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDeleteClickForSingleFile(FormState.FISC_1);
            }
        });
        findViewById(R.id.fisc_2_id).findViewById(R.id.remove__thumbnail_photo_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onDeleteClickForSingleFile(FormState.FISC_2);
            }
        });
        findViewById(R.id.form_send_layout_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onSendClick(
                        ((EditText) findViewById(R.id.full_name_edit_text)).getText().toString(),
                        ((EditText) findViewById(R.id.email_edit_text)).getText().toString(),
                        dropdown.getSelectedItem().toString(),
                        ((EditText) findViewById(R.id.rent_edit_text)).getText().toString()
                );
            }
        });
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public void openPdfIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        pdfBankPickerLauncher.launch(intent);
    }

    @Override
    public void showRentLayout() {
        findViewById(R.id.rent_layout_id).setVisibility(VISIBLE);
        ((CheckBox) findViewById(R.id.yes_no_rent_id).findViewById(R.id.yes_checkbox)).setChecked(true);
        ((CheckBox) findViewById(R.id.yes_no_rent_id).findViewById(R.id.no_checkbox)).setChecked(false);
    }

    @Override
    public void hideRentLayout() {
        findViewById(R.id.rent_layout_id).setVisibility(GONE);
        ((CheckBox) findViewById(R.id.yes_no_rent_id).findViewById(R.id.yes_checkbox)).setChecked(false);
        ((CheckBox) findViewById(R.id.yes_no_rent_id).findViewById(R.id.no_checkbox)).setChecked(true);
    }

    @Override
    public void showMarriageLayout() {
        findViewById(R.id.marriage_layout_id).setVisibility(VISIBLE);
        ((CheckBox) findViewById(R.id.married_yes_no_id).findViewById(R.id.yes_checkbox)).setChecked(true);
        ((CheckBox) findViewById(R.id.married_yes_no_id).findViewById(R.id.no_checkbox)).setChecked(false);
    }

    @Override
    public void hideMarriageLayout() {
        findViewById(R.id.marriage_layout_id).setVisibility(GONE);
        ((CheckBox) findViewById(R.id.married_yes_no_id).findViewById(R.id.yes_checkbox)).setChecked(false);
        ((CheckBox) findViewById(R.id.married_yes_no_id).findViewById(R.id.no_checkbox)).setChecked(true);
    }

    @Override
    public void showExpensesLayout() {
        findViewById(R.id.form_expenses_recycler_id).setVisibility(VISIBLE);
        ((CheckBox) findViewById(R.id.expenses_yes_no_id).findViewById(R.id.yes_checkbox)).setChecked(true);
        ((CheckBox) findViewById(R.id.expenses_yes_no_id).findViewById(R.id.no_checkbox)).setChecked(false);
    }

    @Override
    public void hideExpensesLayout() {
        findViewById(R.id.form_expenses_recycler_id).setVisibility(GONE);
        ((CheckBox) findViewById(R.id.expenses_yes_no_id).findViewById(R.id.yes_checkbox)).setChecked(false);
        ((CheckBox) findViewById(R.id.expenses_yes_no_id).findViewById(R.id.no_checkbox)).setChecked(true);
    }

    @Override
    public void showMedicalLayout() {
        findViewById(R.id.form_medical_recycler_id).setVisibility(VISIBLE);
        ((CheckBox) findViewById(R.id.medical_yes_no_id).findViewById(R.id.yes_checkbox)).setChecked(true);
        ((CheckBox) findViewById(R.id.medical_yes_no_id).findViewById(R.id.no_checkbox)).setChecked(false);
    }

    @Override
    public void hideMedicalLayout() {
        findViewById(R.id.form_medical_recycler_id).setVisibility(GONE);
        ((CheckBox) findViewById(R.id.medical_yes_no_id).findViewById(R.id.yes_checkbox)).setChecked(false);
        ((CheckBox) findViewById(R.id.medical_yes_no_id).findViewById(R.id.no_checkbox)).setChecked(true);
    }

    @Override
    public void hideBottomSheet() {
        collapseBottomSheet();
    }

    @Override
    public void pickBank() {
        pickBank.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    @Override
    public void pickKids() {
        pickKids.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    @Override
    public void pickExpenses() {
        pickExpenses.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    @Override
    public void pickMedical() {
        pickMedical.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    @Override
    public void pickRent() {
        pickRent.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    @Override
    public void pickRtb() {
        pickRtb.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    @Override
    public void pickMarriage() {
        pickMarriage.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    @Override
    public void pickFisc1() {
        pickFisc1.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    @Override
    public void pickFisc2() {
        pickFisc2.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                .build());
    }

    @Override
    public void removeItemFromBank(int photoClicked) {
        bsAdapter.remove(photoClicked);
    }

    @Override
    public void removeItemFromKids(int photoClicked) {
        kidsAdapter.remove(photoClicked);
    }

    @Override
    public void removeItemFromExpenses(int photoClicked) {
        expensesAdapter.remove(photoClicked);
    }

    @Override
    public void removeItemFromMedical(int photoClicked) {
        medicalAdapter.remove(photoClicked);
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
                        findViewById(R.id.file_chooser_top_id).setVisibility(GONE);
                    }
                }).start();
    }

    @Override
    public void showBottomSheet() {
        findViewById(R.id.add_pdf_id).setVisibility(VISIBLE);
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
        findViewById(R.id.file_chooser_top_id).setVisibility(VISIBLE);
        AnimUtils.getFadeInAnimator(findViewById(R.id.file_chooser_top_id),
                AnimUtils.DURATION_200,
                AnimUtils.NO_DELAY,
                null,
                null).start();
    }

    @Override
    public void showToast(int message) {
        Toast.makeText(FormActivity.this, message, Toast.LENGTH_SHORT).show();
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
    public void closeActivity() {
        finish();
    }

    @Override
    public void showFileChooser(int selectPictureRequest, int takePictureRequest) {

    }

    @Override
    public void setFileChooserToVisible() {

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
    public void addItemToBankList(VatModel vatModel) {
        bsAdapter.addItemToList(vatModel);
    }

    @Override
    public void addItemToKidsList(VatModel vatModel) {
        kidsAdapter.addItemToList(vatModel);
    }

    @Override
    public void addItemToExpensesList(VatModel vatModel) {
        expensesAdapter.addItemToList(vatModel);
    }

    @Override
    public void addItemToMedicalList(VatModel vatModel) {
        medicalAdapter.addItemToList(vatModel);
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
    public void setImage(String file, int resourceId) {
        findViewById(resourceId).findViewById(R.id.preview_thumbnail_id).setVisibility(GONE);
        findViewById(resourceId).findViewById(R.id.photo_holder_divider_id).setVisibility(GONE);
        findViewById(resourceId).findViewById(R.id.photo_uploaded_id).setVisibility(VISIBLE);
        Glide.with(this)
                .load(file)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.5f)
                .transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelOffset(R.dimen.global_radius)))
                .into((ImageView) findViewById(resourceId).findViewById(R.id.vat_preview_image_id));
    }

    @Override
    public void setPdfDefaultImage(int resourceId) {
        findViewById(resourceId).findViewById(R.id.photo_uploaded_id).setVisibility(VISIBLE);
        findViewById(resourceId).findViewById(R.id.preview_thumbnail_id).setVisibility(GONE);
        findViewById(resourceId).findViewById(R.id.photo_holder_divider_id).setVisibility(GONE);
        ((ImageView) findViewById(resourceId).findViewById(R.id.vat_preview_image_id)).setImageResource(R.drawable.pdf_final);
    }

    @Override
    public void clearHolder(int resourceId) {
        findViewById(resourceId).findViewById(R.id.photo_uploaded_id).setVisibility(View.GONE);
    }
}