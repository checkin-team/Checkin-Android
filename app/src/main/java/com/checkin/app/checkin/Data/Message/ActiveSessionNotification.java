package com.checkin.app.checkin.Data.Message;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionActivity;
import com.squareup.picasso.Picasso;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment.KEY_SESSION_STATUS;
import static com.checkin.app.checkin.Menu.SessionMenuActivity.KEY_RESTAURANT_PK;
import static com.checkin.app.checkin.Menu.SessionMenuActivity.KEY_SESSION_PK;
import static com.checkin.app.checkin.Menu.SessionMenuActivity.SESSION_ARG;


public class ActiveSessionNotification extends Service {
    public static final String ACTIVE_RESTAURANT_NAME = "active.restaurant_name";
    public static final String ACTIVE_RESTAURANT_LOGO = "active.restaurant_logo";
    public static final String ACTIVE_RESTAURANT_PK = "active.restaurant_pk";
    public static final String ACTIVE_SESSION_PK = "active.session_pk";
    Notification notification;
    long restaurant_pk, session_pk;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.STARTFOREGROUND_ACTION)) {
            if(notification == null)
            showNotification(intent);

        } else if (intent.getAction().equals(Constants.STOPFOREGROUND_ACTION)) {
            stopForeground(true);
            stopSelf();
        } else if (intent.getAction().equals(Constants.MENU_ACTION)) {
            openMenu();
        }
        return START_STICKY;
    }


    private void showNotification(Intent intent) {
        restaurant_pk = Long.parseLong(intent.getStringExtra(ACTIVE_RESTAURANT_PK));
        session_pk = intent.getLongExtra(ACTIVE_SESSION_PK, 0);
        String restaurant_name = "At " + intent.getStringExtra(ACTIVE_RESTAURANT_NAME);
        String restaurant_logo = intent.getStringExtra(ACTIVE_RESTAURANT_LOGO);

        RemoteViews bigViews = new RemoteViews(getPackageName(), R.layout.active_session_persistent_notification);

        bigViews.setTextViewText(R.id.tv_as_noti_restaurant_name, restaurant_name);

        Intent activeSessionIntent = new Intent(this, ActiveSessionActivity.class);
        activeSessionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activeSessionIntent, 0);

        Intent menuIntent = new Intent(this, ActiveSessionNotification.class);
        menuIntent.setAction(Constants.MENU_ACTION);
        PendingIntent pMenuIntent = PendingIntent.getService(this, 0, menuIntent, 0);

        bigViews.setOnClickPendingIntent(R.id.ll_as_noti_live_container, pendingIntent);
        bigViews.setOnClickPendingIntent(R.id.ll_menu, pMenuIntent);

        String NOTIFICATION_CHANNEL_NAME = getString(R.string.app_name);
        String NOTIFICATION_CHANNEL_ID = "session channel name";
        String NOTIFICATION_CHANNEL_D = "session channel description";

        notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setContentTitle("Live")
                .setContentText(restaurant_name)
                .setCustomBigContentView(bigViews)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(R.id.ll_menu, "Menu", pMenuIntent)
                .build();

        Picasso
                .with(this)
                .load(restaurant_logo)
                .into(bigViews, R.id.im_as_noti_restaurant_logo, Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        /*try {
            Bitmap  bitmap = Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(restaurant_logo)
                    .submit(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                    .get();

            bigViews.setImageViewBitmap(R.id.im_as_noti_restaurant_logo, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NOTIFICATION_CHANNEL_D);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    private void openMenu(){
        Intent menuIntent = new Intent(this, SessionMenuActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(KEY_SESSION_STATUS, MenuGroupsFragment.SESSION_STATUS.ACTIVE);
        args.putLong(KEY_RESTAURANT_PK, restaurant_pk);
        args.putLong(KEY_SESSION_PK, session_pk);
        menuIntent.putExtra(SESSION_ARG, args);
        menuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(menuIntent);
    }
}
