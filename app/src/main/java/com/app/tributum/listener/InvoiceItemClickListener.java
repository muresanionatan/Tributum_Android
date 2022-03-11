package com.app.tributum.listener;

public interface InvoiceItemClickListener {

    void onPreviewPhotoClick(String filePath, int photoIndex, boolean arePrivates);

    void onPlusCLick(boolean privates);

    void onDeleteClick(String filePath, int photoIndex, boolean arePrivates);
}