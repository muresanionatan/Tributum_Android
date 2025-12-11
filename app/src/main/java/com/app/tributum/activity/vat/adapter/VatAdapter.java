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

    private final InvoiceItemClickListener invoiceItemClickListener;

    private final InvoicesDeleteListener invoicesDeleteListener;

    private Resources resources;

    private final int mode;

    public VatAdapter(Activity activity, List<VatModel> list, InvoiceItemClickListener invoiceItemClickListener, InvoicesDeleteListener invoicesDeleteListener,
                      int mode) {
        this.activity = activity;
        this.list = list;
        this.invoiceItemClickListener = invoiceItemClickListener;
        this.invoicesDeleteListener = invoicesDeleteListener;
        this.mode = mode;
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
    public void onBindViewHolder(@NonNull VatItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        VatModel model = list.get(position);
        View plusImage = holder.plusImage;
        if (model.isPdf())
            plusImage.setVisibility(View.GONE);
        View photoUploadedView = holder.photoUploadedView;
        if (position == list.size() - 1 || plusImage == null) {
            if (list.size() - 1 < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT && plusImage != null) {
                photoUploadedView.setVisibility(View.GONE);
                plusImage.setVisibility(View.VISIBLE);
                plusImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (invoiceItemClickListener != null && !model.isPdf())
                            invoiceItemClickListener.onPlusCLick(mode);
                    }
                });
            }
        } else if (position < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT) {
            photoUploadedView.setVisibility(View.VISIBLE);
            if (model.isPdf()) {
                photoUploadedView.findViewById(R.id.preview_thumbnail_id).setVisibility(View.GONE);
                photoUploadedView.findViewById(R.id.photo_holder_divider_id).setVisibility(View.GONE);
                ((ImageView) photoUploadedView.findViewById(R.id.vat_preview_image_id)).setImageResource(R.drawable.pdf_final);
            } else {
                photoUploadedView.findViewById(R.id.preview_thumbnail_id).setVisibility(View.VISIBLE);
                photoUploadedView.findViewById(R.id.photo_holder_divider_id).setVisibility(View.VISIBLE);
                Glide.with(activity).load("file://" + model.getFilePath()).thumbnail(0.5f)
                        .transform(new CenterCrop(), new RoundedCorners(resources.getDimensionPixelOffset(R.dimen.global_radius)))
                        .into((ImageView) photoUploadedView.findViewById(R.id.vat_preview_image_id));
            }
            holder.previewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (invoiceItemClickListener != null)
                        invoiceItemClickListener.onPreviewPhotoClick(model.getFilePath(), position, mode);
                }
            });
            holder.deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (invoiceItemClickListener != null)
                        invoiceItemClickListener.onDeleteClick(model.getFilePath(), position, mode);
                }
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItemToList(VatModel model) {
        list.add(getItemCount() - 1, model);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
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