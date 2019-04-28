package com.checkin.app.checkin.Utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.checkin.app.checkin.Home.HomeActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Waiter.WaiterWorkActivity;
import com.golovin.fluentstackbar.FluentSnackbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

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

    /* ============================================================
     * Display
     * ============================================================ */
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
            res = String.valueOf(count / 1000) + "k";
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
        return formatCurrencyAmount(context, String.valueOf(amount));
    }

    public static String formatDateTo24HoursTime(Date dateTime) {
        return new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(dateTime);
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

    public static String formatDate(String date, String initDateFormat, String endDateFormat) throws ParseException {
        Date initDate = new SimpleDateFormat(initDateFormat, Locale.getDefault()).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat, Locale.getDefault());
        return formatter.format(initDate);
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

    public static String formatElapsedTime(Date eventTime) {
        return formatElapsedTime(eventTime, Calendar.getInstance().getTime());
    }

    public static String formatElapsedTime(Date eventTime, Date currentTime) {
        long diffTime = currentTime.getTime() - eventTime.getTime();
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
        if (elapsedDays > 0)
            return String.format(Locale.ENGLISH, "%d days ago", elapsedDays);
        if (elapsedHours > 0)
            return String.format(Locale.ENGLISH, "%d hours ago", elapsedHours);
        if (elapsedMinutes > 0)
            return String.format(Locale.ENGLISH, "%d minutes ago", elapsedMinutes);
        return "Now";
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
    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
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
     * File
     * ============================================================ */
    public static List<MediaImage> getImagesList(Context context) {
        List<MediaImage> imageList = null;
        final String[] projection = new String[]{
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

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (isKitKatOrLater) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }


        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public interface MatchResultFunction {
        String apply(MatchResult match);
    }
}
