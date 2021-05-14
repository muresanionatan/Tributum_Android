package com.app.tributum.fragment.invoices.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tributum.R;

public class InvoicesItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public TextView textView;

    public InvoicesItemViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.item_image_id);
        textView = itemView.findViewById(R.id.item_text_id);
    }
}