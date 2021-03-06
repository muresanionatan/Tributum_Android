package com.app.tributum.utils;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.app.tributum.application.TributumApplication;

import java.io.File;

public class ImageUtils {

    private ImageUtils() {}

    public static String getImagePath(String prefix) {
        String imageFileName = prefix + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

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
}