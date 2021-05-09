package com.example.tributum.fragment.invoices.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tributum.R;
import com.example.tributum.fragment.invoices.listener.InvoiceItemClickListener;
import com.example.tributum.fragment.invoices.model.InvoiceModel;
import com.example.tributum.listener.InvoicesDeleteListener;
import com.example.tributum.utils.BitmapUtils;
import com.example.tributum.utils.ConstantsUtils;

import java.util.ArrayList;
import java.util.List;

public class InvoicesAdapter1 extends RecyclerView.Adapter<InvoicesItemViewHolder> {

    private Resources resources;

    private Activity activity;

    private List<InvoiceModel> list;

    private InvoiceItemClickListener invoiceItemClickListener;

    private InvoicesDeleteListener invoicesDeleteListener;

    public InvoicesAdapter1(Activity activity, List<InvoiceModel> list, InvoiceItemClickListener invoiceItemClickListener, InvoicesDeleteListener invoicesDeleteListener) {
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
        ImageView imageView = holder.imageView;
        if (position == list.size() - 1 || imageView == null) {
            if (list.size() - 1 < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT) {
                imageView.setImageResource(R.drawable.camera_svg);
                imageView.setBackgroundResource(R.drawable.selector_white_stroke);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (invoiceItemClickListener != null)
                            invoiceItemClickListener.onTakePhotoClick();
                    }
                });
                holder.textView.setText(resources.getString(R.string.take_photo));
            }
        } else if (position < ConstantsUtils.MAXIMUM_PICTURES_IN_ATTACHMENT) {
            Bitmap bitmap = BitmapFactory.decodeFile(BitmapUtils.compressBitmap(activity, model.getFilePath(), false));
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (invoiceItemClickListener != null)
                        invoiceItemClickListener.onPreviewPhotoClick(model.getFilePath(), position);
                }
            });
            holder.textView.setText(resources.getString(R.string.photo_label) + " " + (position + 1));
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

    public List<InvoiceModel> getList() {
        List<InvoiceModel> newList = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            newList.add(list.get(i));
        }
        return newList;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}