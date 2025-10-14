package com.app.tributum.activity.form;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.tributum.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;

public class FormActivity extends AppCompatActivity implements FormView {

    private BottomSheetBehavior<View> fileChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_activity);

        RelativeLayout llBottomSheet = findViewById(R.id.file_chooser_id);
        fileChooser = BottomSheetBehavior.from(llBottomSheet);
        fileChooser.setDraggable(false);

        setClicks();
    }

    private void setClicks() {
        findViewById(R.id.yes_no_bank_id).findViewById(R.id.yes_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void showToast(int stringId) {

    }

    @Override
    public void showLoadingScreen() {

    }

    @Override
    public void hideLoadingScreen() {

    }

    @Override
    public void closeActivity() {

    }

    @Override
    public void hideMaritalLayout() {

    }

    @Override
    public void showMaritalLayout() {

    }

    @Override
    public void hideBottomSheet() {

    }

    @Override
    public void showFileChooser(int selectPictureRequest, int takePictureRequest) {

    }

    @Override
    public void openFilePreview(String fileName) {

    }

    @Override
    public void setFileChooserToVisible() {

    }

    @Override
    public void closePreview() {

    }

    @Override
    public void showClearButton() {

    }

    @Override
    public void hideClearButton() {

    }

    @Override
    public void takePicture(int requestId, File file, String pictureImagePath) {

    }

    @Override
    public void resetPpsFrontLayout() {

    }

    @Override
    public void resetPpsBackLayout() {

    }

    @Override
    public void resetIdLayout() {

    }

    @Override
    public void resetMarriageCertificateLayout() {

    }

    @Override
    public void setPpsFrontImage(String ppsFileFront) {

    }

    @Override
    public void setPpsBackImage(String ppsFileBack) {

    }

    @Override
    public void setIdImage(String idFile) {

    }

    @Override
    public void setMarriageCertificateImage(String marriageCertificateFile) {

    }

    @Override
    public void showRequestSentScreen() {

    }

    @Override
    public void removeFocus() {

    }

    @Override
    public void startCrop(Uri imageUri) {

    }
}