package com.example.tributum.fragment.invoices.model;

public class InvoiceModel {

    private String filePath;

    public InvoiceModel(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}