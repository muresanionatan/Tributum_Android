package com.example.tributum.fragment.invoices.listener;

public interface InvoiceItemClickListener {

    void onTakePhotoClick();

    void onPreviewPhotoClick(String filePath, int photoIndex);
}