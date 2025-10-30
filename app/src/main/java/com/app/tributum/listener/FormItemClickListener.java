package com.app.tributum.listener;

public interface FormItemClickListener {

    void onPreviewPhotoClick(String filePath, int photoIndex, boolean arePrivates);

    void onPlusCLick(int state);

    void onDeleteClick(String filePath, int photoIndex, int state);
}