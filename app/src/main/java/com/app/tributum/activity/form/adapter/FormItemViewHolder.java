package com.app.tributum.activity.form.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tributum.R;
import com.app.tributum.utils.ui.CustomButton;

public class FormItemViewHolder extends RecyclerView.ViewHolder {

    public View plusImage;

    public CustomButton deleteImage;

    public View photoUploadedView;

    public FormItemViewHolder(@NonNull View itemView) {
        super(itemView);

        plusImage = itemView.findViewById(R.id.plus_id);
        deleteImage = itemView.findViewById(R.id.remove__thumbnail_photo_id);
        photoUploadedView = itemView.findViewById(R.id.photo_uploaded_id);
    }
}