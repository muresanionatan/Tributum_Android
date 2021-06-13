package com.app.tributum.utils.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.tributum.R;
import com.app.tributum.listener.RequestSentListener;

public class RequestSent {

    private Activity activity;

    private ViewGroup viewGroup;

    private View inflatedView;

    private ImageView imageView;

    private int drawableResourceId;

    private String title;

    private RequestSentListener listener;

    public RequestSent(Activity activity, ViewGroup viewGroup, int drawableResourceId, String title, RequestSentListener listener) {
        this.activity = activity;
        this.viewGroup = viewGroup;
        this.drawableResourceId = drawableResourceId;
        this.title = title;
        this.listener =listener;
    }

    @SuppressLint("ResourceType")
    public void show() {
        inflatedView = LayoutInflater.from(activity).inflate(R.layout.request_sent_screen, null);
        viewGroup.addView(inflatedView);
//        imageView = inflatedView.findViewById(R.id.loading_animation_id);
//        imageView.setColorFilter(ContextCompat.getColor(TributumApplication.getInstance(), colorId));
        inflatedView.findViewById(R.id.loading_screen_id).setBackgroundResource(drawableResourceId);
        ((TextView) inflatedView.findViewById(R.id.request_sent_text_id)).setText(title);
        inflatedView.findViewById(R.id.ok_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                if (listener != null)
                    listener.onOkClicked();
            }
        });
        animate();
    }

    @SuppressLint("ResourceType")
    public void hide() {
        viewGroup.removeView(inflatedView);
    }

    private void animate() {
//        View imageView = inflatedView.findViewById(R.id.loading_animation_id);
//        ObjectAnimator animator = AnimUtils.getRotationAnimator(imageView, AnimUtils.DURATION_500 * 3, AnimUtils.NO_DELAY, new DecelerateInterpolator(), null, 0, 360);
//        animator.addListener(new CustomAnimatorListener() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                animator.start();
//            }
//        });
//        animator.setRepeatMode(ObjectAnimator.RESTART);
//        animator.start();
    }
}