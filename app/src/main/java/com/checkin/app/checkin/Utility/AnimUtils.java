package com.checkin.app.checkin.Utility;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.app.checkin.R;
import com.miguelcatalan.materialsearchview.utils.AnimationUtil;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public final class AnimUtils {
    public static final int NO_CHANGE = -1;
    public static final long DEFAULT_DURATION = 300L;

    /**
     * Helper method to animate the change of view dimensions.
     *
     * @param view      View to animate.
     * @param newWidth  New width, the view should have. If width shouldn't change, pass Utils.NO_CHANGE.
     * @param newHeight New height, the view should have. If height shouldn't change, pass Utils.NO_CHANGE.
     */
    public static Animator changeViewSize(@NonNull View view, int newWidth, int newHeight) {
        if (newWidth == NO_CHANGE && newHeight == NO_CHANGE)
            return null;
        return changeViewSize(view, newWidth, newHeight, DEFAULT_DURATION);
    }

    public static Animator changeViewSize(final View view, int newWidth, int newHeight, long duration) {
        ValueAnimator heightTick, widthTick;
        heightTick = ValueAnimator.ofInt(view.getHeight(), newHeight).setDuration(duration);
        widthTick = ValueAnimator.ofInt(view.getWidth(), newWidth).setDuration(duration);

        heightTick.addUpdateListener(valueAnimator -> {
            view.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();

            // Request force layout validation.
            view.requestLayout();
        });
        widthTick.addUpdateListener(valueAnimator -> {
            view.getLayoutParams().width = (int) valueAnimator.getAnimatedValue();
            view.requestLayout();
        });

        AnimatorSet animatorSet = new AnimatorSet();
        if (newWidth != NO_CHANGE) {
            if (newHeight != NO_CHANGE)
                animatorSet.playTogether(heightTick, widthTick);
            else
                animatorSet.play(widthTick);
        } else animatorSet.play(heightTick);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        return animatorSet;
    }

    public static Animator hideView(@NonNull View view) {
        return animateAlpha(view, 0.0f, DEFAULT_DURATION);
    }

    public static Animator showView(@NonNull View view) {
        return animateAlpha(view, 1.0f, DEFAULT_DURATION);
    }

    public static Animator animateAlpha(final View view, float finalAlpha, long duration) {
        final ValueAnimator animator = ValueAnimator.ofFloat(view.getAlpha(), finalAlpha).setDuration(duration);
        animator.addUpdateListener(valueAnimator -> {
            float value = (float) animator.getAnimatedValue();
            view.setAlpha(value);
        });
        return animator;
    }

    public static Animator createRotationAnimator(final View view, float targetDegrees) {
        float initialDegrees = view.getRotation();
        final Animator rotate = ObjectAnimator.ofFloat(view, "rotation", initialDegrees, targetDegrees);
        rotate.setDuration(DEFAULT_DURATION);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());
        return rotate;
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static Animator createTintAnimator(final ImageView view, int initialColor, int finalColor) {
        ColorStateList tintList = view.getImageTintList();
        initialColor = (tintList != null) ? tintList.getDefaultColor() : initialColor;
        final ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), initialColor, finalColor);
        animator.setDuration(DEFAULT_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            view.setImageTintList(ColorStateList.valueOf(animatedValue));
        });
        return animator;
    }

    public static Animator createImageColorSourceAnimator(final ImageView view, int initialColor, int finalColor) {
        Drawable drawable = view.getDrawable();
        initialColor = drawable != null ? ((ColorDrawable) drawable).getColor() : initialColor;
        final ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), initialColor, finalColor);
        animator.setDuration(DEFAULT_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                view.setImageDrawable(new ColorDrawable(animatedValue));
            }
        });
        return animator;
    }

    public static Animator createTextColorAnimator(final TextView view, int initialColor, int finalColor) {
        initialColor = initialColor != 0 ? initialColor : view.getCurrentTextColor();
        final ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), initialColor, finalColor);
        animator.setDuration(DEFAULT_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> view.setTextColor((int) animation.getAnimatedValue()));
        return animator;
    }

    public static Animator createCircularRevealAnimator(final ClipRevealFrame view, int x, int y, float startRadius, float endRadius) {

        final Animator reveal;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            reveal = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius);
        } else {
            view.setClipOutLines(true);
            view.setClipCenter(x, y);
            reveal = ObjectAnimator.ofFloat(view, "ClipRadius", startRadius, endRadius);
            reveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setClipOutLines(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        reveal.setDuration(DEFAULT_DURATION);
        reveal.setInterpolator(new AccelerateDecelerateInterpolator());
        return reveal;
    }

    public static void expand(ViewGroup viewPager, Context context) {
//        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewPager.getWidth(), View.MeasureSpec.EXACTLY);
//        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(viewPager.getHeight(), View.MeasureSpec.AT_MOST);
//
//        viewPager.measure(matchParentMeasureSpec, wrapContentMeasureSpec);

//        viewPager.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        int height = viewPager.getMeasuredHeight();

       /* ViewTreeObserver vto = viewPager.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if(viewPager.getMeasuredHeight()> 0){


                    int width  = viewPager.getMeasuredWidth();
                    int height = viewPager.getMeasuredHeight();

                    final int targetHeight = height;

//                    viewPager.getLayoutParams().height = 1;
                    Animation a = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            viewPager.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT
                                    : (int) (targetHeight * interpolatedTime);
                            viewPager.requestLayout();

                        }

                        @Override
                        public boolean willChangeBounds() {
                            return true;
                        }
                    };
                    a.setDuration((int) (targetHeight / viewPager.getContext().getResources().getDisplayMetrics().density));
                    viewPager.setVisibility(View.VISIBLE);
                    viewPager.startAnimation(a);
                }
            }
        });*/

        viewPager.post(new Runnable() {

            @Override
            public void run() {
                int width = viewPager.getWidth();
                int height = viewPager.getMeasuredHeight();

                final int targetHeight = height;

                viewPager.getLayoutParams().height = 1;
                Animation a = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        viewPager.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT
                                : (int) (targetHeight * interpolatedTime);
                        viewPager.requestLayout();

                    }

                    @Override
                    public boolean willChangeBounds() {
                        return true;
                    }
                };
                a.setDuration((int) (targetHeight / viewPager.getContext().getResources().getDisplayMetrics().density));
                viewPager.setVisibility(View.VISIBLE);
                viewPager.startAnimation(a);
            }

        });

       /* int widthSpec = View.MeasureSpec.makeMeasureSpec(viewPager.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        viewPager.measure(widthSpec, heightSpec);
        final int targetHeight = viewPager.getMeasuredHeight();

        viewPager.getLayoutParams().height = 1;
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                viewPager.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                viewPager.requestLayout();

            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration((int) (targetHeight / viewPager.getContext().getResources().getDisplayMetrics().density));
        viewPager.setVisibility(View.VISIBLE);
        viewPager.startAnimation(a);*/



    }

    public static void collapse(ViewGroup vSubGroupWrapper, CustomViewPager viewPager) {
        final int initialHeight = vSubGroupWrapper.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    vSubGroupWrapper.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
                } else {
                    vSubGroupWrapper.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    vSubGroupWrapper.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (initialHeight / vSubGroupWrapper.getContext().getResources().getDisplayMetrics().density));
        vSubGroupWrapper.startAnimation(a);
    }


}
