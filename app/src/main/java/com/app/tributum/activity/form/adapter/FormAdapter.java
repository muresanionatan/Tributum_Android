package com.app.tributum.activity.form.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tributum.R;
import com.app.tributum.activity.vat.model.VatModel;
import com.app.tributum.listener.FormItemClickListener;
import com.app.tributum.listener.InvoicesDeleteListener;
import com.app.tributum.utils.ConstantsUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

public class FormAdapter extends RecyclerView.Adapter<FormItemViewHolder> {

    private Activity activity;

    private List<VatModel> list;

    private FormItemClickListener formItemClickListener;

    private InvoicesDeleteListener invoicesDeleteListener;

    private Resources resources;

    private @FormAdapterState int state;

    public FormAdapter(Activity activity, List<VatModel> list, FormItemClickListener formItemClickListener, InvoicesDeleteListener invoicesDeleteListener,
                       int state) {
        this.activity = activity;
        this.list = list;
        this.formItemClickListener = formItemClickListener;
        this.invoicesDeleteListener = invoicesDeleteListener;
        this.state = state;
    }

    @NonNull
    @Override
    public FormItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        resources = parent.getResources();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoices_item, parent, false);
        return new FormItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FormItemViewHolder holder, int position) {
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
                        if (formItemClickListener != null)
                            formItemClickListener.onPlusCLick(state);
                    }
                });
            }
        } else if (position < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT) {
            photoUploadedView.setVisibility(View.VISIBLE);
            photoUploadedView.findViewById(R.id.preview_thumbnail_id).setVisibility(GONE);
            photoUploadedView.findViewById(R.id.photo_holder_divider_id).setVisibility(GONE);
            if (model.isPdf()) {
                ((ImageView) photoUploadedView.findViewById(R.id.vat_preview_image_id)).setImageResource(R.drawable.pdf_final);
            } else {
                Glide.with(activity).load("file://" + model.getFilePath()).thumbnail(0.5f)
                        .transform(new CenterCrop(), new RoundedCorners(resources.getDimensionPixelOffset(R.dimen.global_radius)))
                        .into((ImageView) photoUploadedView.findViewById(R.id.vat_preview_image_id));
            }
            holder.deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (formItemClickListener != null)
                        formItemClickListener.onDeleteClick(model.getFilePath(), position, state);
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