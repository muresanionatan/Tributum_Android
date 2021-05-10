package com.example.tributum.utils.ui;

import android.app.Activity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    private FileUtils() {
    }

    public static File createFile(Activity activity, String message, String sFileName) {
        File file = null;
        try {
            File root = new File(activity.getExternalFilesDir(null), "Files");
            if (!root.exists()) {
                root.mkdirs();
            }
            file = new File(root, sFileName);
            FileWriter writer = new FileWriter(file);
            writer.append(message);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}