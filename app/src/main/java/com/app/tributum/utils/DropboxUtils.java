package com.app.tributum.utils;

import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class DropboxUtils {

    private static final String ACCESS_TOKEN = "sl.BZ1Tok0Zzzh3KMZvhQGeSyGd29RncAJzKvsU4z45OKuYiCthIKB81F7c1b2hPWzZ8DosxS-lNN0XO8RE7pPcQ5qlwtls3DwLH6OIi2tJjTYLjFWeFeC-4SnsC1sAGh-9dInoxhA";

    private DropboxUtils() {
    }

    public static void uploadVatOnDropbox(String username, String months, File uploadFile) throws FileNotFoundException {
        try {
            InputStream inputStream = new FileInputStream(uploadFile);
            getDropBoxClient().files().uploadBuilder("/VATS/" + username.toUpperCase() + "/" + months + ".pdf")
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(inputStream);
            Log.d("Upload Status", "Success");
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void uploadInquiryOnDropbox(String username, String uploadFile, String inquiryPhotoName) throws FileNotFoundException {
        try {
            InputStream inputStream = new FileInputStream(uploadFile);
            getDropBoxClient().files().uploadBuilder("/INQUIRIES/" + username.toUpperCase() + "/" + inquiryPhotoName + ".png")
                    .withMode(WriteMode.ADD)
                    .uploadAndFinish(inputStream);
            Log.d("Upload Status", "Success");
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void uploadPpsAndId(String username, Map<String, String> uploadList, String path) throws FileNotFoundException {
        try {
            InputStream inputStream;
            for (String key : uploadList.keySet()) {
                String file = uploadList.get(key);
                if (file == null)
                    continue;
                inputStream = new FileInputStream(file);
                getDropBoxClient().files().uploadBuilder("/" + path + "/" + username.toUpperCase() + "/" + key + ".png")
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
            }
            Log.d("Upload Status", "Success");
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void addUserInfoFile(File uploadFile, String username, String path) {
        try {
            InputStream inputStream = new FileInputStream(uploadFile);
            getDropBoxClient().files().uploadBuilder("/" + path + "/" + username.toUpperCase() + "/" + "user_info.txt")
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(inputStream);
            Log.d("Upload Status", "Success");
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }

    private static DbxClientV2 getDropBoxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("tributum").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        return client;
    }
}