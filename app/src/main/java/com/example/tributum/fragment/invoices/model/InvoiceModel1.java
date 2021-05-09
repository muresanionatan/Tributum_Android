package com.example.tributum.fragment.invoices.model;

import android.graphics.Bitmap;

public class InvoiceModel1 {

    private Bitmap bitmap;

    private String filePath;

    public InvoiceModel1(Bitmap bitmap, String filePath) {
        this.bitmap = bitmap;
        this.filePath = filePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}