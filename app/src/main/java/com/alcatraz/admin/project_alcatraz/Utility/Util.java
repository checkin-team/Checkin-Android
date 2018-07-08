package com.alcatraz.admin.project_alcatraz.Utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

/**
 * Created by shivanshs9 on 12/5/18.
 */

public class Util {
    public static final int NO_CHANGE = -1;
    private static final long DEFAULT_DURATION = 300L;

    public static void setTabsFont(TabLayout tabLayout, Typeface tf) {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(tf);
                }
            }
        }
    }

    public static void animateShow(View view, float y) {
        // TODO: Have to remove / change this stupid method! (while working on HomeActivity)
        view.animate()
            .translationY(y-y)
            .alpha(1.0f)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
    }

    public static void animateHide(final View view) {
        view.animate()
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                        super.onAnimationEnd(animation);
                    }
                });
    }

    public static void animateHide(final View view, float y) {
        view.animate()
            .translationY(y)
            .alpha(0.0f)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.clearAnimation();
                }
            });
    }

    /** Helper method to animate the change of view dimensions.
     *
     * @param view View to animate.
     * @param newWidth New width, the view should have. If width shouldn't change, pass Util.NO_CHANGE.
     * @param newHeight New height, the view should have. If height shouldn't change, pass Util.NO_CHANGE.
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

        heightTick.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();

                // Request force layout validation.
                view.requestLayout();
            }
        });

        widthTick.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.getLayoutParams().width = (int) valueAnimator.getAnimatedValue();
                view.requestLayout();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        if (newWidth != NO_CHANGE) {
            if (newHeight != NO_CHANGE)
                animatorSet.playTogether(heightTick, widthTick);
            else
                animatorSet.play(widthTick);
        }
        else animatorSet.play(heightTick);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
//        animatorSet.start();
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
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) animator.getAnimatedValue();
                view.setAlpha(value);
            }
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
        final ObjectAnimator animator = ObjectAnimator.ofInt(view, "imageTint", initialColor, finalColor);
        animator.setDuration(DEFAULT_DURATION);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ObjectAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                view.setImageTintList(ColorStateList.valueOf(animatedValue));
            }
        });
        return animator;
    }

    public static Animator createCircularRevealAnimator(final ClipRevealFrame view, int x, int y, float startRadius, float endRadius) {

        final Animator reveal;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
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


    public static int[] range(int start, int stop) {
        return range(start, stop, 1);
    }

    private static int[] range(int start, int stop, int step) {
        int[] ar = new int[(stop - start) / step];
        int j = 0;
        for (int i = start; i < stop; i += step)
            ar[j++] = i;
        return ar;
    }

    public static String replaceAll(String text, Matcher matcher, @NonNull MatchResultFunction replacer) {
        matcher.reset();
        boolean result = matcher.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                String replacement = replacer.apply(matcher);
                matcher.appendReplacement(sb, replacement);
                result = matcher.find();
            } while (result);
            matcher.appendTail(sb);
            return sb.toString();
        }
        return text;
    }

    public static Object getOrDefault(Map<String, ?> map, Object key, Object defaultValue) {
        Object v;
        return (((v = map.get(key)) != null) || map.containsKey(key))
                ? v
                : defaultValue;
    }

    public static String postApi(String partialUrl, String data) {
        String url = Util.getUrl(partialUrl);
        //TODO: Implement the networking part.
        String responseJson = "{'detail': 'dummy'}";
        try {
            JSONObject object = new JSONObject(responseJson);
            return object.getString("detail");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getUrl(String partialUrl) {
        return Constants.API_HOST + ":" + Constants.API_PORT + partialUrl;
    }

    public interface MatchResultFunction {
        String apply(MatchResult match);
    }
}
