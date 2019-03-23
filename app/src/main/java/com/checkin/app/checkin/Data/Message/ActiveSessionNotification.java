package com.checkin.app.checkin.Data.Message;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionActivity;
import com.checkin.app.checkin.Utility.Utils;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment.KEY_SESSION_STATUS;
import static com.checkin.app.checkin.Menu.SessionMenuActivity.KEY_RESTAURANT_PK;
import static com.checkin.app.checkin.Menu.SessionMenuActivity.KEY_SESSION_PK;
import static com.checkin.app.checkin.Menu.SessionMenuActivity.SESSION_ARG;


public class ActiveSessionNotification extends Service {
    private NotificationManager mNotificationManager;
    Notification notification;
    long restaurant_pk;
    long session_pk;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Constants.STARTFOREGROUND_ACTION)) {
//            if(notification == null)
            showNotification(intent);

        } else if (intent.getAction().equals(Constants.STOPFOREGROUND_ACTION)) {
            stopForeground(true);
            stopSelf();
        } else if (intent.getAction().equals(Constants.MENU_ACTION)) {
            Intent menuIntent = new Intent(this, SessionMenuActivity.class);
            Bundle args = new Bundle();
            args.putSerializable(KEY_SESSION_STATUS, MenuGroupsFragment.SESSION_STATUS.ACTIVE);
            args.putLong(KEY_RESTAURANT_PK, restaurant_pk);
            args.putLong(KEY_SESSION_PK, session_pk);
            menuIntent.putExtra(SESSION_ARG, args);
            menuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(menuIntent);
        }
        return START_STICKY;
    }


    private void showNotification(Intent intent) {

        restaurant_pk = Long.parseLong(intent.getStringExtra("restaurant_pk"));
        session_pk = intent.getLongExtra("session_pk", 0);

        RemoteViews bigViews = new RemoteViews(getPackageName(),
                R.layout.active_session_persistent_notification);

        String restaurant_name = "At " + intent.getStringExtra("restaurant_name");
        String restaurant_logo = intent.getStringExtra("restaurant_pic");

        bigViews.setTextViewText(R.id.tv_as_noti_restaurant_name, restaurant_name);

        try {
            Bitmap bitmap = Glide.with(this)
                    .asBitmap()
                    .load(restaurant_logo)
                    .submit(512, 512)
                    .get();

            bigViews.setImageViewBitmap(R.id.im_as_noti_restaurant_logo, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Intent activeSessionIntent = new Intent(this, ActiveSessionActivity.class);
        activeSessionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activeSessionIntent, 0);

        Intent menuIntent = new Intent(this, ActiveSessionNotification.class);
        menuIntent.setAction(Constants.MENU_ACTION);
        PendingIntent pMenuIntent = PendingIntent.getService(this, 0, menuIntent, 0);

        bigViews.setOnClickPendingIntent(R.id.ll_as_noti_live_container, pendingIntent);
        bigViews.setOnClickPendingIntent(R.id.ll_menu, pMenuIntent);

        String NOTIFICATION_CHANNEL_NAME = getString(R.string.app_name);
        String NOTIFICATION_CHANNEL_ID = "Your human readable notification channel name";
        String NOTIFICATION_CHANNEL_D = "channel desc";

        notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setContentTitle("Live")
                .setContentText(restaurant_name)
                .setCustomBigContentView(bigViews)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(R.id.ll_menu, "Menu", pMenuIntent)
                .build();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NOTIFICATION_CHANNEL_D);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
