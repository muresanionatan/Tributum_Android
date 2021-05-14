package com.app.tributum.utils.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.tributum.R;

public class LoadingScreen {

    private Activity activity;

    private ViewGroup viewGroup;

    private View inflatedView;

    public LoadingScreen(Activity activity, ViewGroup viewGroup) {
        this.activity = activity;
        this.viewGroup = viewGroup;
    }

    @SuppressLint("ResourceType")
    public void show() {
        inflatedView = LayoutInflater.from(activity).inflate(R.layout.loading_screen, null);
        viewGroup.addView(inflatedView);
    }

    @SuppressLint("ResourceType")
    public void hide() {
        viewGroup.removeView(inflatedView);
    }
}