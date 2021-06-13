package com.app.tributum.activity.faq.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.tributum.R;
import com.app.tributum.activity.faq.model.FaqItem;
import com.app.tributum.activity.faq.model.FaqItemState;
import com.app.tributum.listener.FaqClickListener;
import com.app.tributum.utils.animation.AnimUtils;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqItemViewHolder> {

    private List<FaqItem> faqItems;

    private FaqClickListener listener;

    public FaqAdapter(List<FaqItem> faqItems, FaqClickListener listener) {
        this.faqItems = faqItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FaqItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_item, parent, false);
        return new FaqItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqItemViewHolder holder, int position) {
        holder.title.setText(faqItems.get(position).getTitle());
        holder.subtitle.setText(faqItems.get(position).getSubtitle());

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClicked(position);
            }
        });

        if (faqItems.get(position).getState() == FaqItemState.EXPAND) {
            expandItem(holder);
        } else if (faqItems.get(position).getState() == FaqItemState.COLLAPSE) {
            collapseItem(holder);
        }
    }

    @Override
    public int getItemCount() {
        return faqItems.size();
    }

    public void setFaqItems(List<FaqItem> faqItems) {
        this.faqItems = faqItems;
    }

    public void expandItem(FaqItemViewHolder holder) {
        holder.subtitle.setVisibility(View.VISIBLE);
        AnimUtils.getRotationAnimator(holder.expandIcon,
                AnimUtils.DURATION_200,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                null,
                90).start();
    }

    public void collapseItem(FaqItemViewHolder holder) {
        holder.subtitle.setVisibility(View.GONE);
        AnimUtils.getRotationAnimator(holder.expandIcon,
                AnimUtils.DURATION_200,
                AnimUtils.NO_DELAY,
                new DecelerateInterpolator(),
                null,
                270).start();
    }

    class FaqItemViewHolder extends RecyclerView.ViewHolder {

        private View itemLayout;

        private ImageView expandIcon;

        private TextView title;

        private TextView subtitle;

        public FaqItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.faq_item_layout_id);
            expandIcon = itemView.findViewById(R.id.expand_arrow_id);
            title = itemView.findViewById(R.id.title_id);
            subtitle = itemView.findViewById(R.id.subtitle_id);
        }
    }
}