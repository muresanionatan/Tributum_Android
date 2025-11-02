package com.app.tributum.utils;

import android.os.AsyncTask;

import androidx.annotation.IntDef;

import com.app.tributum.listener.AsyncListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

public class UploadAsyncTask extends AsyncTask {

    private String userName;

    private Map<String, String> uploadList;

    private Map<String, File> pdfList;

    private AsyncListener asyncListener;

    private File file;

    private String filePath;

    private String inquiryPhotoName;

    private String path;

    private String process;

    @UploadType
    private int type;

    public UploadAsyncTask(String userName, Map<String, String> uploadList, AsyncListener asyncListener, @UploadType int type, String path) {
        this.userName = userName;
        this.uploadList = uploadList;
        this.asyncListener = asyncListener;
        this.type = type;
        this.path = path;
    }

    public UploadAsyncTask(String userName, Map<String, File> uploadList, AsyncListener asyncListener, @UploadType int type) {
        this.userName = userName;
        this.pdfList = uploadList;
        this.asyncListener = asyncListener;
        this.type = type;
    }

    public UploadAsyncTask(String userName, File message, AsyncListener asyncListener, @UploadType int type, String path) {
        this.userName = userName;
        this.file = message;
        this.asyncListener = asyncListener;
        this.type = type;
        this.path = path;
    }

    public UploadAsyncTask(String userName, String filePath, String inquiryPhotoName, AsyncListener asyncListener, @UploadType int type) {
        this.userName = userName;
        this.filePath = filePath;
        this.inquiryPhotoName = inquiryPhotoName;
        this.asyncListener = asyncListener;
        this.type = type;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            if (type == UploadType.MULTIPLE) {
                DropboxUtils.uploadPpsAndId(userName, uploadList, path);
            } else if (type == UploadType.USER_INFO) {
                DropboxUtils.addUserInfoFile(file, userName, path);
            } else if (type == UploadType.INQUIRY) {
                DropboxUtils.uploadInquiryOnDropbox(userName, filePath, inquiryPhotoName);
            } else if (type == UploadType.PDFS) {
                DropboxUtils.uploadMultipleFiles(userName, pdfList);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (asyncListener != null)
            asyncListener.onTaskCompleted(process);
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getProcess() {
        return process;
    }

    @IntDef
    public @interface UploadType {
        int MULTIPLE = 0,
                USER_INFO = 1,
                INQUIRY = 2,
                PDFS = 3;
    }
}