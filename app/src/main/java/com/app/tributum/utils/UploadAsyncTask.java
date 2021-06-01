package com.app.tributum.utils;

import android.os.AsyncTask;

import androidx.annotation.IntDef;

import com.app.tributum.fragment.invoices.listener.AsyncListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

public class UploadAsyncTask extends AsyncTask {

    private String userName;

    private Map<String, String> uploadList;

    private AsyncListener asyncListener;

    private File file;

    @UploadType
    private int type;

    public UploadAsyncTask(String userName, Map<String, String> uploadList, AsyncListener asyncListener, @UploadType int type) {
        this.userName = userName;
        this.uploadList = uploadList;
        this.asyncListener = asyncListener;
        this.type = type;
    }

    public UploadAsyncTask(String userName, File message, AsyncListener asyncListener, @UploadType int type) {
        this.userName = userName;
        this.file = message;
        this.asyncListener = asyncListener;
        this.type = type;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            if (type == UploadType.MULTIPLE) {
                DropboxUtils.uploadPpsAndId(userName, uploadList);
            } else {
                DropboxUtils.addUserInfoFile(file, userName);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (asyncListener != null)
            asyncListener.onTaskCompleted();
        super.onPostExecute(o);
    }

    @IntDef
    public @interface UploadType {
        int MULTIPLE = 0,
                ONE = 1;
    }
}