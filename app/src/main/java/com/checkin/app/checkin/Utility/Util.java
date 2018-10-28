package com.checkin.app.checkin.Utility;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

/**
 * Created by shivanshs9 on 12/5/18.
 */

public class Util {
    public static final int NO_CHANGE = -1;
    public static final long DEFAULT_DURATION = 300L;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

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
        final ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), initialColor, finalColor);
        animator.setDuration(DEFAULT_DURATION);
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
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTextColor((int) animation.getAnimatedValue());
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

    public static <T> T getOrDefault(Map<?, T> map, Object key, T defaultValue) {
        if (map == null)
            return defaultValue;
        T v;
        return (((v = map.get(key)) != null) || map.containsKey(key))
                ? v
                : defaultValue;
    }

    public static String formatCount(long count) {
        String res;
        if (count > 1000)
            res = String.valueOf(count / 1000) + "k";
        else
            res = String.valueOf(count);
        return res;
    }

    public interface MatchResultFunction {
        String apply(MatchResult match);
    }

    public static <T> List<T> sparseArrayAsList(SparseArray<T> sparseArray) {
        if (sparseArray == null) return null;
        List<T> arrayList = new ArrayList<T>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    public static String joinCollection(Collection<?> words, CharSequence delimiter) {
        StringBuilder wordList = new StringBuilder();
        for (Object word : words) {
            wordList.append(word.toString() + delimiter);
        }
        return new String(wordList.delete(wordList.length() - delimiter.length(), wordList.length()));
    }

    public static <T> boolean equalsObjectField(@NonNull Object obj, String field, @NonNull T value) {
        try {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return value.equals(f.get(obj));
        } catch (NoSuchFieldException e) {
            Log.e("Field '" + field + "' in " + obj.getClass().getSimpleName(), "Missing");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static <T> T getFromCollection(@NonNull Collection<T> collection, T obj) {
        T res = null;
        for (T item: collection) {
            if (obj.equals(item)) {
                res = item;
            }
        }
        return res;
    }

    public static String formatTime(long min, long sec) {
        String res;
        if (min == 0)
            res = String.format(Locale.ENGLISH, "%02d seconds", sec);
        else
            res = String.format(Locale.ENGLISH, "%02d:%02d minutes", min, sec);
        return res;
    }

    public static List<MediaImage> getImagesList(Context context) {
        List<MediaImage> imageList = null;
        final String[] projection = new String[] {
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_MODIFIED,
        };
        final String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        );
        assert cursor != null;
        int column_data = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        int column_title = cursor.getColumnIndex(MediaStore.Images.Media.TITLE);
        String imagePath, imageTitle;
        imageList = new ArrayList<>();
        while (cursor.moveToNext()) {
            imagePath = cursor.getString(column_data);
            imageTitle = cursor.getString(column_title);
            imageList.add(new MediaImage(imageTitle, imagePath));
        }
        cursor.close();
        return imageList;
    }

    public static String getActivityIntentFilter(Context context, String identifier) {
        return context.getPackageName() + "." + identifier;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }



    public static String timePassed(Date timeEvent){
        Date date;
        date = Calendar.getInstance().getTime();
        long different=date.getTime()-timeEvent.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

       return (elapsedDays+" days, "+elapsedHours+" hours, "+ elapsedMinutes+"minutes ago");

    }
}
