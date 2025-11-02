package com.app.tributum.activity.vat.model;

public class VatModel {

    private boolean pdf;

    private String filePath;

    public VatModel(String filePath, boolean pdf) {
        this.filePath = filePath;
        this.pdf = pdf;
    }

    public VatModel(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isPdf() {
        return pdf;
    }

    public void setPdf(boolean pdf) {
        this.pdf = pdf;
    }
}