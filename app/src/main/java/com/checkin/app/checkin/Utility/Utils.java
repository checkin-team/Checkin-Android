package com.checkin.app.checkin.Utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.checkin.app.checkin.Auth.AuthPreferences;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.data.notifications.ActiveSessionNotificationService;
import com.checkin.app.checkin.home.activities.HomeActivity;
import com.checkin.app.checkin.home.activities.SplashActivity;
import com.golovin.fluentstackbar.FluentSnackbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import kotlin.Pair;

/**
 * Created by shivanshs9 on 12/5/18.
 */

public final class Utils {
    /* ============================================================
     * Android
     * ============================================================ */
    public static final boolean isPOrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    public static final boolean isOOrLater = isPOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    public static final boolean isNougatMR1OrLater = isOOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1;
    public static final boolean isNougatOrLater = isNougatMR1OrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    public static final boolean isMarshMallowOrLater = isNougatOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    public static final boolean isLolliPopOrLater = isMarshMallowOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    public static final boolean isKitKatOrLater = isLolliPopOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    public static final boolean isJellyBeanMR2OrLater = isKitKatOrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    public static final boolean isJellyBeanMR1OrLater = isJellyBeanMR2OrLater || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    private static final Handler sHandler = new Handler(Looper.getMainLooper());
    public static final String DOCUMENTS_DIR = "documents";
    public static final String TAG = Utils.class.getSimpleName();

    /* ============================================================
     * Display
     * ============================================================ */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float dpToPx(float dp) {
        return (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static float pxToDp(float px) {
        return (px / Resources.getSystem().getDisplayMetrics().density);
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

    public static void navigateBackToHome(Context context) {
        context.startActivity(Intent.makeRestartActivityTask(new ComponentName(context, HomeActivity.class)));
    }

    /* ============================================================
     * General
     * ============================================================ */
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

    public static <T> T getOrDefault(Map<?, T> map, Object key, T defaultValue) {
        if (map == null)
            return defaultValue;
        T v;
        return (((v = map.get(key)) != null) || map.containsKey(key))
                ? v
                : defaultValue;
    }

    public static void setKeyboardVisibility(final View v, final boolean show) {
        if (v == null) return;
        final InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        sHandler.post(() -> {
            if (show) imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
            else imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });
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
        for (T item : collection) {
            if (obj.equals(item)) {
                res = item;
            }
        }
        return res;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return (networkInfo != null && networkInfo.isConnected());
    }

    /* ============================================================
     * Text
     * ============================================================ */
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

    public static String formatCount(long count) {
        String res;
        if (count > 1000)
            res = count / 1000 + "k";
        else
            res = String.valueOf(count);
        return res;
    }

    public static String formatTime(long time) {
        long hour = time / 60;
        long min = time % 60;

        return String.format(Locale.getDefault(), "%02d:%02d", hour, min);
    }

    public static String joinCollection(Collection<?> words, CharSequence delimiter) {
        StringBuilder wordList = new StringBuilder();
        for (Object word : words) {
            wordList.append(word.toString()).append(delimiter);
        }
        return new String(wordList.delete(wordList.length() - delimiter.length(), wordList.length()));
    }

    public static String formatTime(long min, long sec) {
        String res;
        if (min == 0)
            res = String.format(Locale.ENGLISH, "%02d seconds", sec);
        else
            res = String.format(Locale.ENGLISH, "%02d:%02d minutes", min, sec);
        return res;
    }

    public static String getCurrencyFormat(Context context) {
        return context.getResources().getString(R.string.format_currency_rupee);
    }

    public static String formatCurrencyAmount(Context context, String amount) {
        return String.format(Locale.ENGLISH, getCurrencyFormat(context), amount);
    }

    public static String formatCurrencyAmount(Context context, Double amount) {
        DecimalFormat format = new DecimalFormat("##,##,##,##0.00");
        return formatCurrencyAmount(context, format.format(amount));
    }

    public static String formatIntegralCurrencyAmount(Context context, Double amount) {
        DecimalFormat format = new DecimalFormat("##,##,##,##0");
        return formatCurrencyAmount(context, format.format(amount));
    }

    public static String formatDateTo24HoursTime(@NonNull Date dateTime) {
        return new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(dateTime);
    }

    public static String formatDateTo12HoursTime(@NonNull Date dateTime) {
        return new SimpleDateFormat("HH:mm a", Locale.ENGLISH).format(dateTime);
    }

    public static String getCurrentFormattedDateInvoice() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String getCurrentFormattedDate() {
        Date date = Calendar.getInstance().getTime();
        return getFormattedDate(date);
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String formatCompleteDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        return formatter.format(date);
    }

    public static String convertFormatDate(String date, String initDateFormat, String endDateFormat) throws ParseException {
        Date initDate = new SimpleDateFormat(initDateFormat, Locale.getDefault()).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat, Locale.getDefault());
        return formatter.format(initDate);
    }

    public static String formatDate(@NonNull Date date, String dateFormat) {
        return new SimpleDateFormat(dateFormat, Locale.getDefault()).format(date);
    }

    public static String formatTimeDuration(long milliSec) {
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        long elapsedHours = milliSec / hoursInMilli;
        milliSec = milliSec % hoursInMilli;
        long elapsedMinutes = milliSec / minutesInMilli;
        milliSec = milliSec % minutesInMilli;
        long elapsedSeconds = milliSec / secondsInMilli;
        milliSec = milliSec % secondsInMilli;

        if (elapsedHours > 0)
            return String.format(Locale.ENGLISH, "%d Hours", elapsedHours);
        if (elapsedMinutes > 0)
            return String.format(Locale.ENGLISH, "%d Mins", elapsedMinutes);
        if (elapsedSeconds > 0)
            return String.format(Locale.ENGLISH, "%d Sec", elapsedSeconds);
        return "0 Sec";
    }

    public static String formatElapsedTime(@NonNull Date eventTime) {
        return formatElapsedTime(eventTime, Calendar.getInstance().getTime());
    }

    public static String formatElapsedTime(@NonNull Date eventTime, @NonNull Date currentTime) {
        Pair<TimeUnit, Long> pair = getTimeDifference(eventTime, currentTime);
        long value = pair.getSecond();
        String suffix = value > 1 ? "s " : " ";
        switch (pair.getFirst()) {
            case DAYS:
                return String.format(Locale.getDefault(), "%d day%sago", value, suffix);
            case HOURS:
                return String.format(Locale.getDefault(), "%d hour%sago", value, suffix);
            case MINUTES:
                return String.format(Locale.getDefault(), "%d min%sago", value, suffix);
        }
        return "Now";
    }

    public static String formatDueTime(@NonNull Date startTime, @NonNull Date endTime) {
        Pair<TimeUnit, Long> pair = getTimeDifference(startTime, endTime);
        long value = pair.getSecond();
        String suffix = value > 1 ? "s " : " ";
        switch (pair.getFirst()) {
            case DAYS:
                return String.format(Locale.getDefault(), "%d day%s", value, suffix);
            case HOURS:
                return String.format(Locale.getDefault(), "%d hour%s", value, suffix);
            case MINUTES:
                return String.format(Locale.getDefault(), "%d min%s", value, suffix);
        }
        return "Under 1 min";
    }

    public static Pair<TimeUnit, Long> getTimeDifference(@NonNull Date start, @NonNull Date end) {
        long diffTime = end.getTime() - start.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = diffTime / daysInMilli;
        diffTime = diffTime % daysInMilli;
        long elapsedHours = diffTime / hoursInMilli;
        diffTime = diffTime % hoursInMilli;
        long elapsedMinutes = diffTime / minutesInMilli;
        diffTime = diffTime % minutesInMilli;
        if (elapsedDays > 0) return new Pair(TimeUnit.DAYS, elapsedDays);
        if (elapsedHours > 0) return new Pair(TimeUnit.HOURS, elapsedHours);
        if (elapsedMinutes > 0) return new Pair(TimeUnit.MINUTES, elapsedMinutes);
        else return new Pair(TimeUnit.SECONDS, diffTime / secondsInMilli);
    }

    public static CharSequence fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return Html.fromHtml(html, 0);
        else return Html.fromHtml(html);
    }

    /* ============================================================
     * UI
     * ============================================================ */
    public static void toast(@NonNull Context context, String msg) {
        if (!TextUtils.isEmpty(msg))
            Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void toast(@NonNull Context context, @StringRes int msgRes) {
        Toast.makeText(context.getApplicationContext(), msgRes, Toast.LENGTH_SHORT).show();
    }

    public static FluentSnackbar.Builder snack(View view, String msg) {
        return FluentSnackbar.create(view)
                .create(msg)
                .duration(Snackbar.LENGTH_SHORT);
    }

    public static FluentSnackbar.Builder snack(Activity activity, String msg) {
        return FluentSnackbar.create(activity)
                .create(msg)
                .duration(Snackbar.LENGTH_SHORT);
    }

    public static void errorSnack(View view, String msg) {
        snack(view, msg)
                .errorBackgroundColor()
                .textColorRes(R.color.white)
                .show();
    }

    public static void warningSnack(View view, String msg) {
        snack(view, msg)
                .warningBackgroundColor()
                .textColorRes(R.color.brownish_grey)
                .show();
    }

    public static void errorSnack(Activity activity, String msg) {
        snack(activity, msg)
                .errorBackgroundColor()
                .textColorRes(R.color.white)
                .show();
    }

    public static void warningSnack(Activity activity, String msg) {
        snack(activity, msg)
                .warningBackgroundColor()
                .textColorRes(R.color.brownish_grey)
                .show();
    }

    public static void loadImageOrDefault(ImageView imageView, String url, @DrawableRes int defaultDrawable) {
        if (url != null) {
            GlideApp.with(imageView.getContext())
                    .load(url)
                    .into(imageView);
        } else if (defaultDrawable != 0) {
            imageView.setImageResource(defaultDrawable);
        }
    }

    public static void loadImageOrDefault(ImageView imageView, String url, Drawable defaultDrawable) {
        if (url != null) {
            GlideApp.with(imageView.getContext())
                    .load(url)
                    .into(imageView);
        } else if (defaultDrawable != null) {
            imageView.setImageDrawable(defaultDrawable);
        }
    }

    public static Drawable getSingleDrawable(Context context, LayerDrawable layerDrawable) {
        int resourceBitmapHeight = 136, resourceBitmapWidth = 153;

        float widthInInches = 0.9f;

        int widthInPixels = (int) (widthInInches * context.getResources().getDisplayMetrics().densityDpi);
        int heightInPixels = widthInPixels * resourceBitmapHeight / resourceBitmapWidth;

        int insetLeft = 10, insetTop = 10, insetRight = 10, insetBottom = 10;

        layerDrawable.setLayerInset(1, insetLeft, insetTop, insetRight, insetBottom);

        Bitmap bitmap = Bitmap.createBitmap(widthInPixels, heightInPixels, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        layerDrawable.setBounds(0, 0, widthInPixels, heightInPixels);
        layerDrawable.draw(canvas);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);
        bitmapDrawable.setBounds(0, 0, widthInPixels, heightInPixels);

        return bitmapDrawable;
    }

    /* ============================================================
     * Bitmap
     * ============================================================ */
    public static Bitmap modifyOrientation(Bitmap bitmap, @NonNull String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /* ============================================================
     * General
     * ============================================================ */
    public static void resetStuff(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .clear()
                .apply();
        ActiveSessionNotificationService.clearNotification(context);
    }

    public static boolean logoutFromApp(Context context) {
        if (!AuthPreferences.removeCurrentAccount(context))
            return false;
        Utils.resetStuff(context);

        Intent splashIntent = Intent.makeRestartActivityTask(new ComponentName(context, SplashActivity.class));
        context.startActivity(splashIntent);
        return true;
    }

    public interface MatchResultFunction {
        String apply(MatchResult match);
    }

    /* ============================================================
     * View
     * ============================================================ */
    @SuppressLint("ResourceAsColor")
    public static Bitmap blurImage(ImageView imageView, Bitmap src) {
        // create new bitmap, which will be painted and becomes result image
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Log.e("width===", src.getWidth() + "    " + src.getHeight() + "===");
        // setup canvas for painting
        Canvas canvas = new Canvas(bmOut);
        // setup default color
//        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        // create a blur paint for capturing alpha
        Paint ptBlur = new Paint();
        ptBlur.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        ptBlur.setMaskFilter(new BlurMaskFilter(80, BlurMaskFilter.Blur.OUTER));
        int[] offsetXY = new int[2];
        // capture alpha into a bitmap
//        Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
        // create a color paint
        Paint ptAlphaColor = new Paint();
        ptAlphaColor.setColor(R.color.apple_green);
        canvas.drawBitmap(src, 100, 50, ptBlur);
        // paint color for captured alpha region (bitmap)
//        canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], ptAlphaColor);
        // free memory
//        bmAlpha.recycle();

        // paint the image source
//        canvas.drawBitmap(src, 40, 10, ptAlphaColor);
        imageView.draw(canvas);
        imageView.setImageBitmap(bmOut);
        // return out final image
        return bmOut;
    }

    public static Bitmap getBitmapFromView(View view) {
        //Create a Bitmap with the same dimensions as the View
        Bitmap image = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_4444); //reduce quality
        //Draw the view inside the Bitmap
        Canvas canvas = new Canvas(image);
        view.draw(canvas);

        //Make it frosty
        Paint paint = new Paint();
        paint.setXfermode(
                new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        ColorFilter filter =
           new LightingColorFilter(0xFF7F7F7F, 0x00000000); // darken
        paint.setColorFilter(filter);
        canvas.drawBitmap(image, 0, 0, paint);
        return image;
    }
}
