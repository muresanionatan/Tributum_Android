package com.app.tributum.activity.vat.model;

public class VatModel {

    private String filePath;

    public VatModel(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}