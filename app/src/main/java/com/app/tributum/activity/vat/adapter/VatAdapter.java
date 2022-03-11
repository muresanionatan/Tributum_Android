package com.app.tributum.activity.vat.adapter;

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
import com.app.tributum.activity.vat.model.VatModel;
import com.app.tributum.listener.InvoiceItemClickListener;
import com.app.tributum.listener.InvoicesDeleteListener;
import com.app.tributum.utils.ConstantsUtils;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

public class VatAdapter extends RecyclerView.Adapter<VatItemViewHolder> {

    private Activity activity;

    private List<VatModel> list;

    private InvoiceItemClickListener invoiceItemClickListener;

    private InvoicesDeleteListener invoicesDeleteListener;

    private Resources resources;

    private boolean arePrivates;

    public VatAdapter(Activity activity, List<VatModel> list, InvoiceItemClickListener invoiceItemClickListener, InvoicesDeleteListener invoicesDeleteListener,
                      boolean arePrivates) {
        this.activity = activity;
        this.list = list;
        this.invoiceItemClickListener = invoiceItemClickListener;
        this.invoicesDeleteListener = invoicesDeleteListener;
        this.arePrivates = arePrivates;
    }

    @NonNull
    @Override
    public VatItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        resources = parent.getResources();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoices_item, parent, false);
        return new VatItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VatItemViewHolder holder, int position) {
        VatModel model = list.get(position);
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
                            invoiceItemClickListener.onPlusCLick(arePrivates);
                    }
                });
            }
        } else if (position < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT) {
            photoUploadedView.setVisibility(View.VISIBLE);
            Glide.with(activity).load("file://" + model.getFilePath()).thumbnail(0.5f)
                    .transform(new CenterCrop(), new RoundedCorners(resources.getDimensionPixelOffset(R.dimen.global_radius)))
                    .into((ImageView) photoUploadedView.findViewById(R.id.vat_preview_image_id));
            holder.previewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (invoiceItemClickListener != null)
                        invoiceItemClickListener.onPreviewPhotoClick(model.getFilePath(), position, arePrivates);
                }
            });
            holder.deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (invoiceItemClickListener != null)
                        invoiceItemClickListener.onDeleteClick(model.getFilePath(), position, arePrivates);
                }
            });
        }
    }

    public void addItemToList(VatModel model) {
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