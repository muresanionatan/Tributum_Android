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

    private static final String ACCESS_TOKEN = "GH2DbopZybMAAAAAAAAAAQACWCPsCEpxctSWk2ZaU1hRQ4FmylxO8kVjat9SkQd4";

    private DropboxUtils() {
    }

    public static void uploadPdfOnDropbox(String username, String months, File uploadFile) throws FileNotFoundException {
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

    public static void uploadPpsAndId(String username, Map<String, String> uploadList) throws FileNotFoundException {
        try {
            InputStream inputStream;
            for (String key : uploadList.keySet()) {
                String file = uploadList.get(key);
                if (file == null)
                    continue;
                inputStream = new FileInputStream(file);
                getDropBoxClient().files().uploadBuilder("/DATABASE/" + username.toUpperCase() + "/" + key + ".png")
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
            }
            Log.d("Upload Status", "Success");
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void addUserInfoFile(File uploadFile, String username) {
        try {
            InputStream inputStream = new FileInputStream(uploadFile);
            getDropBoxClient().files().uploadBuilder("/DATABASE/" + username.toUpperCase() + "/" + "user_info.txt")
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