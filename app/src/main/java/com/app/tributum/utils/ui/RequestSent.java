package com.app.tributum.utils.ui;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.tributum.R;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.listener.RequestSentListener;

public class RequestSent {

    private ViewGroup viewGroup;

    private View inflatedView;

    private int drawableResourceId;

    private String title;

    private RequestSentListener listener;

    public RequestSent(ViewGroup viewGroup, int drawableResourceId, String title, RequestSentListener listener) {
        this.viewGroup = viewGroup;
        this.drawableResourceId = drawableResourceId;
        this.title = title;
        this.listener = listener;
    }

    @SuppressLint("ResourceType")
    public void show(boolean shouldShowSubtitle) {
        inflatedView = LayoutInflater.from(TributumApplication.getInstance()).inflate(R.layout.request_sent_screen, null);
        viewGroup.addView(inflatedView);
        inflatedView.findViewById(R.id.loading_screen_id).setBackgroundResource(drawableResourceId);
        ((TextView) inflatedView.findViewById(R.id.request_sent_text_id)).setText(title);
        showSubtitle(shouldShowSubtitle);
        inflatedView.findViewById(R.id.ok_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                if (listener != null)
                    listener.onOkClicked();
            }
        });
    }

    public void showSubtitle(boolean shouldShowSubtitle) {
        if (shouldShowSubtitle)
            inflatedView.findViewById(R.id.request_sent__subtitle_text_id).setVisibility(View.VISIBLE);
    }

    @SuppressLint("ResourceType")
    public void hide() {
        viewGroup.removeView(inflatedView);
    }

}