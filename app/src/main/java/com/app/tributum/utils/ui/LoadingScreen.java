package com.app.tributum.utils.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.app.tributum.R;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.utils.animation.AnimUtils;
import com.app.tributum.utils.animation.CustomAnimatorListener;

public class LoadingScreen {

    private Activity activity;

    private ViewGroup viewGroup;

    private View inflatedView;

    private ImageView imageView;

    private int colorId;

    public LoadingScreen(Activity activity, ViewGroup viewGroup, int colorId) {
        this.activity = activity;
        this.viewGroup = viewGroup;
        this.colorId = colorId;
    }

    @SuppressLint("ResourceType")
    public void show() {
        inflatedView = LayoutInflater.from(activity).inflate(R.layout.loading_screen, null);
        viewGroup.addView(inflatedView);
        imageView = inflatedView.findViewById(R.id.loading_animation_id);
        imageView.setColorFilter(ContextCompat.getColor(TributumApplication.getInstance(), colorId));
        animate();
    }

    @SuppressLint("ResourceType")
    public void hide() {
        viewGroup.removeView(inflatedView);
    }

    private void animate() {
        View imageView = inflatedView.findViewById(R.id.loading_animation_id);
        ObjectAnimator animator = AnimUtils.getRotationAnimator(imageView, AnimUtils.DURATION_500 * 3, AnimUtils.NO_DELAY, new DecelerateInterpolator(), null, 0, 360);
        animator.addListener(new CustomAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animator.start();
            }
        });
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.start();
    }
}