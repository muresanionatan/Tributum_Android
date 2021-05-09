package com.example.tributum.utils;

import android.os.AsyncTask;

import com.example.tributum.fragment.invoices.listener.AsyncListener;

import java.io.FileNotFoundException;
import java.util.List;

public class UploadAsyncTask extends AsyncTask {

    private String userName;

    private List<String> uploadList;

    private AsyncListener asyncListener;

    public UploadAsyncTask(String userName, List<String> uploadList, AsyncListener asyncListener) {
        this.userName = userName;
        this.uploadList = uploadList;
        this.asyncListener = asyncListener;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            DropboxUtils.uploadPpsAndId(userName, uploadList);
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
}