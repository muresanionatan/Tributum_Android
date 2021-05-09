package com.example.tributum.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;

import com.example.tributum.listener.CustomAnimatorListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Class that contains all the default animations
 * Created by Ionatan Muresan on 9/12/16.
 */
public class AnimationUtils {

    public static final short DURATION_ONE_SECOND = 1000;

    public static final short DURATION_750 = 750;

    public static final short DURATION_500 = 500;

    public static final short DURATION_350 = 350;

    public static final short DURATION_300 = 300;

    public static final short DURATION_250 = 250;

    public static final short DURATION_200 = 200;

    public static final short DURATION_150 = 150;

    public static final byte DURATION_100 = 100;

    public static final byte DURATION_50 = 50;

    public static final byte NONE = 0;

    public static final byte NO_DELAY = 0;

    /**
     * default duration for the zoom animation
     */
    public static final short DEFAULT_ZOOM_DURATION = 300;

    /**
     * The initial position from which the animation will start
     */
    public static final byte INITIAL = 0;

    /**
     * private constructor just to make sure this class cannot be instantiated
     */
    private AnimationUtils() {
    }

    public static ObjectAnimator getTranslationXAnimator(View view, int duration, int delay, Interpolator interpolator, CustomAnimatorListener listener, float... toValues) {
        ObjectAnimator translateXAnimator = new ObjectAnimator();
        translateXAnimator.setTarget(view);
        translateXAnimator.setProperty(View.TRANSLATION_X);
        translateXAnimator.setFloatValues(toValues);
        if (interpolator != null) {
            translateXAnimator.setInterpolator(interpolator);
        }
        translateXAnimator.setDuration(duration).setStartDelay(delay);
        if (listener != null) {
            translateXAnimator.addListener(listener);
        }

        return translateXAnimator;
    }

    public static ObjectAnimator getTranslationXAnimator(View view, float... toValues) {
        return getTranslationXAnimator(view, NONE, NO_DELAY, null, null, toValues);
    }

    public static ObjectAnimator getTranslationYAnimator(View view, int duration, int delay, Interpolator interpolator, CustomAnimatorListener listener, float... toValues) {
        ObjectAnimator translateYAnimator = new ObjectAnimator();
        translateYAnimator.setTarget(view);
        translateYAnimator.setProperty(View.TRANSLATION_Y);
        translateYAnimator.setFloatValues(toValues);
        if (interpolator != null) {
            translateYAnimator.setInterpolator(interpolator);
        }
        translateYAnimator.setDuration(duration).setStartDelay(delay);
        if (listener != null) {
            translateYAnimator.addListener(listener);
        }

        return translateYAnimator;
    }

    public static ObjectAnimator getTranslationYAnimator(View view, float... toValues) {
        return getTranslationYAnimator(view, NONE, NO_DELAY, null, null, toValues);
    }

    public static ObjectAnimator getRotationAnimator(View view, int duration, int delay, Interpolator interpolator, CustomAnimatorListener listener, float... toValues) {
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(view, View.ROTATION, toValues);
        if (interpolator != null) {
            rotationAnimator.setInterpolator(interpolator);
        }
        rotationAnimator.setDuration(duration).setStartDelay(delay);
        if (listener != null) {
            rotationAnimator.addListener(listener);
        }

        return rotationAnimator;
    }

    @SuppressWarnings("unused")
    public static ObjectAnimator getRotationAnimator(View view, float... toValues) {
        return getRotationAnimator(view, NONE, NO_DELAY, null, null, toValues);
    }

    public static ObjectAnimator getFadeInAnimator(View view, int duration, int delay, Interpolator interpolator, CustomAnimatorListener listener) {
        return getAlphaAnimator(view, duration, delay, interpolator, listener, 0f, 1f);
    }

    public static ObjectAnimator getFadeInAnimator(View view) {
        return getFadeInAnimator(view, NONE, NO_DELAY, null, null);
    }

    public static ObjectAnimator getFadeOutAnimator(View view, int duration, int delay, Interpolator interpolator, CustomAnimatorListener listener) {
        return getAlphaAnimator(view, duration, delay, interpolator, listener, 1f, 0f);
    }

    public static ObjectAnimator getFadeOutAnimator(View view) {
        return getFadeOutAnimator(view, NONE, NO_DELAY, null, null);
    }

    public static ObjectAnimator getAlphaAnimator(View view, int duration, int delay, Interpolator interpolator, CustomAnimatorListener listener, float... toValues) {
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, toValues);
        if (interpolator != null) {
            fadeInAnimator.setInterpolator(interpolator);
        }
        fadeInAnimator.setDuration(duration).setStartDelay(delay);
        if (listener != null) {
            fadeInAnimator.addListener(listener);
        }

        return fadeInAnimator;
    }

    public static ObjectAnimator getAlphaAnimator(View view, float... toValues) {
        return getAlphaAnimator(view, NONE, NO_DELAY, null, null, toValues);
    }

    public static ObjectAnimator getProgressAnimator(View view, int duration, int delay, Interpolator interpolator, CustomAnimatorListener listener, int... toValues) {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(view, "progress", toValues);
        if (interpolator != null) {
            progressAnimator.setInterpolator(interpolator);
        } else {
            progressAnimator.setInterpolator(new LinearInterpolator());
        }
        progressAnimator.setDuration(duration).setStartDelay(delay);
        if (listener != null) {
            progressAnimator.addListener(listener);
        }

        return progressAnimator;
    }

    public static ObjectAnimator getScaleXAnimator(View view, int duration, int delay, Interpolator interpolator, CustomAnimatorListener listener, float... toValues) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, toValues);
        if (interpolator != null) {
            scaleXAnimator.setInterpolator(interpolator);
        }
        scaleXAnimator.setDuration(duration).setStartDelay(delay);
        if (listener != null) {
            scaleXAnimator.addListener(listener);
        }

        return scaleXAnimator;
    }

    public static ObjectAnimator getScaleYAnimator(View view, int duration, int delay, Interpolator interpolator, CustomAnimatorListener listener, float... toValues) {
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, toValues);
        if (interpolator != null) {
            scaleYAnimator.setInterpolator(interpolator);
        }
        scaleYAnimator.setDuration(duration).setStartDelay(delay);
        if (listener != null) {
            scaleYAnimator.addListener(listener);
        }

        return scaleYAnimator;
    }

    /**
     * returns a scale {@link AnimatorSet} that scales the {@link View#SCALE_X} and {@link View#SCALE_Y} properties of the {@link View}
     * @param view view that will be animated
     * @param duration animation duration in milliseconds
     * @param delay animation delay in milliseconds
     * @param interpolator animation interpolator
     * @param listener animation listener
     * @param toValues view will be animated through these values
     * @return a scale {@link AnimatorSet}
     */
    public static AnimatorSet getScaleAnimatorSet(View view, int duration, int delay, Interpolator interpolator,
                                                  CustomAnimatorListener listener, boolean useAnchor, float... toValues) {
        if (useAnchor) {
            view.setPivotX(0);
        }
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, toValues);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, toValues);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        if (interpolator != null) {
            animatorSet.setInterpolator(interpolator);
        }
        animatorSet.setDuration(duration).setStartDelay(delay);
        if (listener != null) {
            animatorSet.addListener(listener);
        }

        return animatorSet;
    }

    @SuppressWarnings("unused")
    public static AnimatorSet getScaleAnimatorSet(View view, float... toValues) {
        return getScaleAnimatorSet(view, NONE, NO_DELAY, null, null, false, toValues);
    }

    @SuppressWarnings("unused")
    public static ValueAnimator getWidthAnimator(final View view, int fromValue, int toValue, int duration, int delay) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(fromValue, toValue);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = val;
                view.setLayoutParams(layoutParams);
            }
        });
        valueAnimator.setStartDelay(delay);
        valueAnimator.setDuration(duration);

        return valueAnimator;
    }

    public static ValueAnimator getMarginAnimator(final View view, int fromValue, int toValue, int duration, @MarginSide final int marginSide) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(fromValue, toValue);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                if (marginSide == MarginSide.RIGHT) {
                    layoutParams.rightMargin = val;
                } else if (marginSide == MarginSide.LEFT) {
                    layoutParams.leftMargin = val;
                } else if (marginSide == MarginSide.BOTTOM) {
                    layoutParams.bottomMargin = val;
                } else {
                    layoutParams.topMargin = val;
                }
                view.setLayoutParams(layoutParams);
            }
        });
        valueAnimator.setDuration(duration);

        return valueAnimator;
    }

    /**
     * animates the <b>background </b>transition between two colors
     * @param view {@link View} that will be animated
     * @param duration animation duration
     * @param fromColor color from which the view will be animated
     * @param toColor color to which the view will be animated
     * @return a color transition animator
     */
    public static ValueAnimator getColorTransformationAnimator(final View view, int duration, int fromColor, int toColor) {
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        colorAnimator.setDuration(duration);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setBackgroundColor((int) animation.getAnimatedValue());
            }
        });

        return colorAnimator;
    }

    /**
     * animates the <b>text color</b> transition between two colors
     * @param view {@link TextView} that will be animated
     * @param duration animation duration
     * @param fromColor color from which the view will be animated
     * @param toColor color to which the view will be animated
     * @return a color transition animator
     */
    public static ValueAnimator getColorTransformationTextAnimator(final TextView view, int duration, int fromColor, int toColor) {
        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        colorAnimator.setDuration(duration);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTextColor((int) animation.getAnimatedValue());
            }
        });

        return colorAnimator;
    }

    /**
     * Animates the <b>drawable color</b> transition between the given colors
     * @param drawable drawable that needs it's background color animated
     * @param duration animation duration
     * @param fromColor color from which the drawable will be animated
     * @param toColor color to which the drawable will be animated
     * @return a color transition animator
     */
    public static ValueAnimator getColorTransformationDrawableAnimator(final Drawable drawable, int duration, int fromColor, int toColor) {
        ValueAnimator colorAnimator = ValueAnimator.ofArgb(fromColor, toColor);
        colorAnimator.setDuration(duration);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawable.setColorFilter((int) animation.getAnimatedValue(), PorterDuff.Mode.SRC_IN);
            }
        });

        return colorAnimator;
    }

    public static ObjectAnimator getImageAnimation(final ImageView view, final Drawable drawable, final int duration) {
        return getFadeOutAnimator(view, duration, NO_DELAY, null, new CustomAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setImageDrawable(drawable);
                getFadeInAnimator(view, duration, NO_DELAY, null, null).start();
            }
        });
    }

    public static void stopAnimator(ObjectAnimator objectAnimator) {
        if (objectAnimator != null) {
            objectAnimator.removeAllListeners();
            objectAnimator.cancel();
        }
    }

    public static void stopValueAnimator(ValueAnimator valueAnimator) {
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            valueAnimator.cancel();
        }
    }

    public static void stopAnimatorSet(AnimatorSet animatorSet) {
        if (animatorSet != null) {
            animatorSet.cancel();
            animatorSet.removeAllListeners();
        }
    }

    /**
     * Sets the hardware layer type for the given views
     * <p>The reason for this is that on slow devices running Android 5.x animations lag. See fadeInDialog() method for example</p>
     * @param views the views for which the hardware layer will be set
     */
    public static void addHardwareLayer(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
        }
    }

    /**
     * Removes the hardware layer type from the given views
     * @param views the views from which the hardware layer will be removed
     */
    public static void removeHardwareLayer(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        }
    }

    /**
     * Defines the animation mode for updating certain UI elements.
     */
    @IntDef({AnimationMode.INSTANT, AnimationMode.ANIMATED})
    @Retention(RetentionPolicy.CLASS)
    public @interface AnimationMode {
        int INSTANT = 0,
                ANIMATED = 1;
    }


    /**
     * Defines side of the margin which will be animated using {@link #getMarginAnimator(View, int, int, int, int)}
     */
    @IntDef({MarginSide.LEFT, MarginSide.TOP, MarginSide.RIGHT, MarginSide.BOTTOM})
    @Retention(RetentionPolicy.CLASS)
    public @interface MarginSide {
        int LEFT = 0,
                TOP = 1,
                RIGHT = 2,
                BOTTOM = 3;
    }
}