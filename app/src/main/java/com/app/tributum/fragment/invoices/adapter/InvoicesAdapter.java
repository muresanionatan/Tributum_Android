package com.app.tributum.fragment.invoices.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.app.tributum.R;
import com.app.tributum.fragment.invoices.model.InvoiceModel;
import com.app.tributum.fragment.invoices.listener.InvoiceItemClickListener;
import com.app.tributum.listener.InvoicesDeleteListener;
import com.app.tributum.utils.ConstantsUtils;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

public class InvoicesAdapter extends RecyclerView.Adapter<InvoicesItemViewHolder> {

    private Activity activity;

    private List<InvoiceModel> list;

    private InvoiceItemClickListener invoiceItemClickListener;

    private InvoicesDeleteListener invoicesDeleteListener;

    private Resources resources;

    public InvoicesAdapter(Activity activity, List<InvoiceModel> list, InvoiceItemClickListener invoiceItemClickListener, InvoicesDeleteListener invoicesDeleteListener) {
        this.activity = activity;
        this.list = list;
        this.invoiceItemClickListener = invoiceItemClickListener;
        this.invoicesDeleteListener = invoicesDeleteListener;
    }

    @NonNull
    @Override
    public InvoicesItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        resources = parent.getResources();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoices_item, parent, false);
        return new InvoicesItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InvoicesItemViewHolder holder, int position) {
        InvoiceModel model = list.get(position);
        View plusImage = holder.plusImage;
        View photoUploadedView = holder.photoUploadedView;
        if (position == list.size() - 1 || plusImage == null) {
            if (list.size() - 1 < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT && plusImage != null) {
                photoUploadedView.setVisibility(View.GONE);
                plusImage.setVisibility(View.VISIBLE);
                plusImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (invoiceItemClickListener != null)
                            invoiceItemClickListener.onPlusCLick();
                    }
                });
            }
        } else if (position < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT) {
            photoUploadedView.setVisibility(View.VISIBLE);
            Glide.with(activity).load("file://" + model.getFilePath()).thumbnail(0.5f)
                    .centerCrop()
                    .transform(new CenterCrop(), new RoundedCorners(resources.getDimensionPixelOffset(R.dimen.global_radius)))
                    .into((ImageView) photoUploadedView.findViewById(R.id.vat_preview_image_id));
            holder.previewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (invoiceItemClickListener != null)
                        invoiceItemClickListener.onPreviewPhotoClick(model.getFilePath(), position);
                }
            });
            holder.deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (invoiceItemClickListener != null)
                        invoiceItemClickListener.onDeleteClick(model.getFilePath(), position);
                }
            });
        }
    }

    public void addItemToList(InvoiceModel model) {
        list.add(getItemCount() - 1, model);
        notifyDataSetChanged();
    }

    public void remove(int photoIndex) {
        list.remove(photoIndex);
        notifyDataSetChanged();

        if (getItemCount() == 1 && invoicesDeleteListener != null)
            invoicesDeleteListener.clearList();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}