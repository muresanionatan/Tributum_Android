package com.app.tributum.utils.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.tributum.R;
import com.app.tributum.application.TributumApplication;
import com.app.tributum.utils.animation.AnimUtils;

/**
 * Custom layout which handles the click and animates the view
 * <p>
 * When the {@link MotionEvent#ACTION_DOWN} is received then {@link #handleDownAction()} method is called and the view is scaled to 97%.
 * <br>
 * When {@link MotionEvent#ACTION_UP} is received then {@link #handleUpAction(MotionEvent, float, float)} method is called and the view
 * is scaled back to 100%.
 * </br>
 * </p>
 * <p>
 * Also, the class contains all the button styles that can be set
 * <br>
 * The following buttons can be set:
 * <li>{@link #setPrimaryLightButton(String)}</li>
 * </br>
 * </p>
 * <p>
 * Created by Ionatan Muresan on 9/4/18.
 * </p>
 */
public class CustomButton extends RelativeLayout {

    private static final short CLICK_THRESHOLD = 500;

    private final LayoutInflater layoutInflater;

    /**
     * The entire view which contains {@link #parentView} and {@link #textView}
     */
    private View view;

    /**
     * The view which contains the background color/style (if there is one) and the {@link #textView}
     */
    private View parentView;

    /**
     * The text view which will display the {@link String} contained in the 'button'
     */
    private TextView textView;

    /**
     * Message that will be set to {@link #textView}
     * <br>
     * It is declared as a field because in case we use th tertiary button styles then when the user
     * clicks the button, we need to remove the underline from the text and when he releases the button
     * we need to set it back. So we need it for persistence.
     * </br>
     */
    private String buttonText;

    /**
     * {@code True} if the button is disabled, {@code False} otherwise.
     */
    private boolean disabled;

    public CustomButton(Context context) {
        super(context);
        layoutInflater = LayoutInflater.from(context);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        layoutInflater = LayoutInflater.from(context);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        layoutInflater = LayoutInflater.from(context);
        init();
    }

    @SuppressWarnings("unused")
    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        layoutInflater = LayoutInflater.from(context);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float initialXPosition = 0;
        float initialYPosition = 0;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return handleDownAction();
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL && view.isPressed()) {
            scaleBackToNormalView();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            return handleUpAction(event, initialXPosition, initialYPosition);
        } else {
            return false;
        }
    }

    /**
     * Disables/enables the view, depending on the given parameter
     *
     * @param disabled {@code True} if the view will be enabled, {@code False} otherwise
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        parentView.setEnabled(!disabled);
        setText(buttonText);
    }

    public void setPrimaryLightButton(String message) {
        textView.setTextColor(ContextCompat.getColor(TributumApplication.getInstance(), R.color.white));
        setText(message);
    }

    /**
     * Sets the {@link #buttonText} that will be displayed in {@link #textView}
     *
     * @param buttonText the message which will be displayed in the {@link #textView}
     */
    public void setText(String buttonText) {
        this.buttonText = buttonText;
        textView.setText(buttonText);
    }

    private void scaleBackToNormalView() {
        if (!disabled) {
            AnimUtils.getScaleAnimatorSet(view, AnimUtils.DURATION_50, AnimUtils.NO_DELAY, new DecelerateInterpolator(), null, false, 1f)
                    .start();
        }
        view.setPressed(false);
    }

    private void init() {
        view = layoutInflater.inflate(R.layout.custom_button, this, true);
//        textView = view.findViewById(R.id.text_custom_relative_layout);
        parentView = view.findViewById(R.id.parent_custom_relative_layout);
    }

    /**
     * Handles the {@link MotionEvent#ACTION_DOWN} action on {@link #view}
     * <br>
     * If the {@link #view} is not {@link #disabled} then the view will be scaled down to 97%
     * and if the button is style tertiary then the underline from {@link #textView} will be hidden
     * </br>
     *
     * @return {@code True} if the event will be handled by the touch event, {@code False} otherwise
     */
    private boolean handleDownAction() {
        if (!disabled) {
            AnimUtils.getScaleAnimatorSet(view, AnimUtils.DURATION_50, AnimUtils.NO_DELAY, new DecelerateInterpolator(), null, false, 0.97f)
                    .start();
        }
        view.setPressed(true);
        return true;
    }

    /**
     * Handles the {@link MotionEvent#ACTION_UP} action on {@link #view}
     * <br>
     * If the {@link #view} is not {@link #disabled} then the view will be scaled up to 100%
     * and if the button is style tertiary then the underline from {@link #textView} will be displayed
     * </br>
     * <br>
     * This method also handles the click on the {@link #view}
     * </br>
     *
     * @param event            the event which is received
     * @param initialXPosition initial X position of the touch used in order to determine if the user clicked the button or dragged it
     * @param initialYPosition initial Y position of the touch used in order to determine if the user clicked the button or dragged it
     * @return {@code True} if the event will be handled by the touch event, {@code False} otherwise
     */
    private boolean handleUpAction(MotionEvent event, float initialXPosition, float initialYPosition) {
        scaleBackToNormalView();
        if (Math.abs(event.getY() - initialYPosition) < CLICK_THRESHOLD || Math.abs(event.getX() - initialXPosition) < CLICK_THRESHOLD) {
            view.performClick();
            return false;
        } else {
            return true;
        }
    }
}