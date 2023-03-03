package com.app.tributum.utils;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.app.tributum.application.TributumApplication;

import java.io.File;

public class ImageUtils {

    private ImageUtils() {
    }

    public static String getImagePath(String prefix) {
        String imageFileName = prefix + ".jpg";
        File storageDir = TributumApplication.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return storageDir.getAbsolutePath() + "/" + imageFileName;
    }

    public static String getFilePathFromGallery(Intent data) {
        Uri uri = data.getData();
        String[] projection = {MediaStore.Images.Media.DATA};

        if (uri == null)
            return "";
        Cursor cursor = TributumApplication.getInstance().getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null)
            return "";
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(projection[0]);
        String filepath = cursor.getString(columnIndex);
        cursor.close();

        return filepath;
    }

    public static Uri getUriFromFile(File file) {
        return FileProvider.getUriForFile(
                TributumApplication.getInstance(),
                "com.app.tributum.activity.vat.provider",
                file);
    }

    public static Intent getImageChooserIntent() {
        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    public static Intent getTakePhotoIntent(File file) {
        Uri outputFileUri = FileProvider.getUriForFile(TributumApplication.getInstance(),
                "com.app.tributum.activity.vat.provider", file);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        return cameraIntent;
    }
}