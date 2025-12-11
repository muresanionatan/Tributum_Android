package com.app.tributum.listener;

public interface InvoiceItemClickListener {

    void onPreviewPhotoClick(String filePath, int photoIndex, int mode);

    void onPlusCLick(int mode);

    void onDeleteClick(String filePath, int photoIndex, int mode);
}