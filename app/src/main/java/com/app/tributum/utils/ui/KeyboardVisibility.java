package com.app.tributum.utils.ui;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import com.app.tributum.listener.KeyboardListener;

/**
 * Class that detects when keyboard is hidden or visible and notifies the interested parties
 * by a listener
 */
public class KeyboardVisibility {

    private boolean isKeyboardShowing;

    private KeyboardListener keyboardListener;

    public KeyboardVisibility(KeyboardListener keyboardListener) {
        this.keyboardListener = keyboardListener;
    }

    public void handleKeyboardVisibility(View contentView) {
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        contentView.getWindowVisibleDisplayFrame(r);
                        int screenHeight = contentView.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            if (!isKeyboardShowing) {
                                isKeyboardShowing = true;
                                if (keyboardListener != null)
                                    keyboardListener.onKeyboardStateChanged(true);
                            }
                        } else {
                            // keyboard is closed
                            if (isKeyboardShowing) {
                                isKeyboardShowing = false;
                                if (keyboardListener != null)
                                    keyboardListener.onKeyboardStateChanged(false);
                            }
                        }
                    }
                });
    }
}