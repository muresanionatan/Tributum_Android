package com.app.tributum.fragment.invoices.listener;

public interface InvoiceItemClickListener {

    void onPreviewPhotoClick(String filePath, int photoIndex);

    void onPlusCLick();

    void onDeleteClick(String filePath, int photoIndex);
}