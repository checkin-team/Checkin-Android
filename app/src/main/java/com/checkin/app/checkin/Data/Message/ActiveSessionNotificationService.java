package com.checkin.app.checkin.Data.Message;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.request.target.NotificationTarget;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.GlideApp;
import com.checkin.app.checkin.session.activesession.ActiveSessionActivity;

public class ActiveSessionNotificationService extends Service {
    public static final String ACTIVE_RESTAURANT_DETAIL = "active.restaurant.detail";
    public static final String ACTIVE_SESSION_PK = "active.session.pk";
    public static final String ACTION_OPEN_MENU = "active.menu.open";

    private static final int ID_FOREGROUND_SERVICE = 501;

    private Notification mNotification;
    private long mRestaurantPk, mSessionPk;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return START_STICKY;
        if (Constants.SERVICE_ACTION_FOREGROUND_START.equals(intent.getAction())) {
            if (mNotification == null) showNotification(intent);
        } else if (Constants.SERVICE_ACTION_FOREGROUND_STOP.equals(intent.getAction())) {
            stopForeground(true);
            stopSelf();
        } else if (ACTION_OPEN_MENU.equals(intent.getAction())) {
            openMenu();
            Intent hideNotificationDrawerIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            this.sendBroadcast(hideNotificationDrawerIntent);
        }
        return START_STICKY;
    }

    private void showNotification(Intent intent) {
        BriefModel restaurantDetail = (BriefModel) intent.getSerializableExtra(ACTIVE_RESTAURANT_DETAIL);
        mSessionPk = intent.getLongExtra(ACTIVE_SESSION_PK, 0);
        mRestaurantPk = Long.parseLong(restaurantDetail.getPk());
        if (mSessionPk == 0) return;

        String restaurant_name = "At " + restaurantDetail.getDisplayName();
        String restaurant_logo = restaurantDetail.getDisplayPic();

        RemoteViews bigViews = new RemoteViews(getPackageName(), R.layout.notification_persistent_active_session);

        bigViews.setTextViewText(R.id.tv_as_noti_restaurant_name, restaurant_name);

        Intent activeSessionIntent = new Intent(this, ActiveSessionActivity.class);
        activeSessionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activeSessionIntent, 0);

        Intent menuIntent = new Intent(this, ActiveSessionNotificationService.class);
        menuIntent.setAction(ACTION_OPEN_MENU);
        PendingIntent pMenuIntent = PendingIntent.getService(this, 0, menuIntent, 0);

        bigViews.setOnClickPendingIntent(R.id.ll_as_noti_live_container, pendingIntent);
        bigViews.setOnClickPendingIntent(R.id.ll_menu, pMenuIntent);

        mNotification = new NotificationCompat.Builder(this, Constants.CHANNEL.ACTIVE_SESSION_PERSISTENT.id)
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setContentTitle("Live")
                .setContentText(restaurant_name)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setCustomContentView(bigViews)
                .setCustomBigContentView(bigViews)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(R.id.ll_menu, "Menu", pMenuIntent)
                .build();

        NotificationTarget notificationTarget = new NotificationTarget(
                this,
                R.id.im_as_noti_restaurant_logo,
                bigViews,
                mNotification,
                ID_FOREGROUND_SERVICE);

        GlideApp
                .with(getApplicationContext())
                .asBitmap()
                .load(restaurant_logo)
                .into(notificationTarget);

        MessageUtils.createRequiredChannel(Constants.CHANNEL.ACTIVE_SESSION_PERSISTENT, this);
        startForeground(ID_FOREGROUND_SERVICE, mNotification);
    }

    private void openMenu() {
        Intent menuIntent = SessionMenuActivity.withSession(this, mRestaurantPk, mSessionPk, null);
        menuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(menuIntent);
    }

    public static void clearNotification(Context context) {
        Intent serviceIntent = new Intent(context, ActiveSessionNotificationService.class)
                .setAction(Constants.SERVICE_ACTION_FOREGROUND_STOP);
        context.startService(serviceIntent);
    }
}
