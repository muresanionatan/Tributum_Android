package com.app.tributum.utils.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.tributum.R;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.utils.animation.AnimUtils;
import com.app.tributum.utils.animation.CustomAnimatorListener;

public class LoadingScreen {

    private ViewGroup viewGroup;

    private View inflatedView;

    private ImageView imageView;

    private int imageId;

    private AnimatorSet scaleAnimator;

    private String loadingText;

    private int colorId;

    public LoadingScreen(ViewGroup viewGroup, int imageId, int colorId) {
        this.viewGroup = viewGroup;
        this.imageId = imageId;
        this.colorId = colorId;
    }

    @SuppressLint("ResourceType")
    public void show() {
        inflatedView = LayoutInflater.from(TributumApplication.getInstance()).inflate(R.layout.loading_screen, null);
        viewGroup.addView(inflatedView);
        imageView = inflatedView.findViewById(R.id.loading_animation_id);
        imageView.setImageResource(imageId);
        if (loadingText != null)
            ((TextView) inflatedView.findViewById(R.id.loading_screen_text_id)).setText(loadingText);
        ((TextView) inflatedView.findViewById(R.id.loading_screen_text_id)).setTextColor(ContextCompat.getColor(TributumApplication.getInstance(), colorId));
        animate();
    }

    @SuppressLint("ResourceType")
    public void hide() {
        viewGroup.removeView(inflatedView);
    }

    public void setText(String text) {
        loadingText = text;
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

        scaleAnimator = AnimUtils.getScaleAnimatorSet(
                imageView,
                AnimUtils.DURATION_500,
                AnimUtils.NO_DELAY,
                null,
                new CustomAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        AnimUtils.getScaleAnimatorSet(imageView,
                                AnimUtils.DURATION_500,
                                AnimUtils.NO_DELAY,
                                null,
                                new CustomAnimatorListener() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        scaleAnimator.start();
                                    }
                                },
                                false,
                                1.2f, 1).start();
                    }
                },
                false,
                1, 1.2f);
        scaleAnimator.start();
    }
}