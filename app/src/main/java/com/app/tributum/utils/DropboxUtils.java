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
import java.util.List;

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

    public static void uploadPpsAndId(String username, List<String> uploadList) throws FileNotFoundException {
        try {
            InputStream inputStream;
            for (int i = 0; i < uploadList.size(); i++) {
                inputStream = new FileInputStream(uploadList.get((i)));
                String prefix;
                if (i == 0)
                    prefix = "PPS_FRONT";
                else if (i == 1)
                    prefix = "PPS_BACK";
                else
                    prefix = "ID";
                getDropBoxClient().files().uploadBuilder("/DATABASE/" + username.toUpperCase() + "/" + prefix + ".png")
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