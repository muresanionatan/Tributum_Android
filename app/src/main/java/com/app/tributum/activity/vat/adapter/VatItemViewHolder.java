package com.app.tributum.activity.vat.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tributum.R;

public class VatItemViewHolder extends RecyclerView.ViewHolder {

    public View plusImage;

    public ImageView previewImage;

    public ImageView deleteImage;

    public View photoUploadedView;

    public VatItemViewHolder(@NonNull View itemView) {
        super(itemView);

        plusImage = itemView.findViewById(R.id.plus_id);
        previewImage = itemView.findViewById(R.id.preview_thumbnail_id);
        deleteImage = itemView.findViewById(R.id.remove__thumbnail_photo_id);
        photoUploadedView = itemView.findViewById(R.id.photo_uploaded_id);
    }
}