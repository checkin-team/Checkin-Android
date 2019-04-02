package com.checkin.app.checkin.Data.Message;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.NotificationTarget;
import com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment;
import com.checkin.app.checkin.Menu.SessionMenuActivity;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionActivity;
import com.checkin.app.checkin.Utility.GlideApp;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.checkin.app.checkin.Menu.Fragment.MenuGroupsFragment.KEY_SESSION_STATUS;
import static com.checkin.app.checkin.Menu.SessionMenuActivity.KEY_RESTAURANT_PK;
import static com.checkin.app.checkin.Menu.SessionMenuActivity.KEY_SESSION_PK;
import static com.checkin.app.checkin.Menu.SessionMenuActivity.SESSION_ARG;

public class ActiveSessionNotificationService extends Service {
    public static final String ACTIVE_RESTAURANT = "active.restaurant_details";
    public static final String ACTIVE_SESSION_PK = "active.session_pk";
    Notification notification;
    long restaurant_pk, session_pk;
    private NotificationTarget notificationTarget;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.START_FOREGROUND_ACTION)) {
            if (notification == null)
                showNotification(intent);

        } else if (intent.getAction().equals(Constants.STOP_FOREGROUND_ACTION)) {
            stopForeground(true);
            stopSelf();
        } else if (intent.getAction().equals(Constants.MENU_ACTION)) {
            openMenu();
            Intent hideNotifDrawerIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            this.sendBroadcast(hideNotifDrawerIntent);
        }
        return START_STICKY;
    }


    private void showNotification(Intent intent) {
        BriefModel restaurant_details = (BriefModel) intent.getSerializableExtra(ACTIVE_RESTAURANT);
        session_pk = intent.getLongExtra(ACTIVE_SESSION_PK, 0);
        restaurant_pk = Long.parseLong(restaurant_details.getPk());
        String restaurant_name = "At " + restaurant_details.getDisplayName();
        String restaurant_logo = restaurant_details.getDisplayPic();

        RemoteViews bigViews = new RemoteViews(getPackageName(), R.layout.active_session_persistent_notification);

        bigViews.setTextViewText(R.id.tv_as_noti_restaurant_name, restaurant_name);

        Intent activeSessionIntent = new Intent(this, ActiveSessionActivity.class);
        activeSessionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activeSessionIntent, 0);

        Intent menuIntent = new Intent(this, ActiveSessionNotificationService.class);
        menuIntent.setAction(Constants.MENU_ACTION);
        PendingIntent pMenuIntent = PendingIntent.getService(this, 0, menuIntent, 0);

        bigViews.setOnClickPendingIntent(R.id.ll_as_noti_live_container, pendingIntent);
        bigViews.setOnClickPendingIntent(R.id.ll_menu, pMenuIntent);

        notification = new NotificationCompat.Builder(this, Constants.CHANNEL.ACTIVE_SESSION_PERSISTENT.id)
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setContentTitle("Live")
                .setContentText(restaurant_name)
                .setCustomBigContentView(bigViews)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(R.id.ll_menu, "Menu", pMenuIntent)
                .build();

        notificationTarget = new NotificationTarget(
                this,
                R.id.im_as_noti_restaurant_logo,
                bigViews,
                notification,
                Constants.FOREGROUND_SERVICE);

        GlideApp
                .with(getApplicationContext())
                .asBitmap()
                .load(restaurant_logo)
                .into(notificationTarget);

        MessageUtils.createRequiredChannel(Constants.CHANNEL.ACTIVE_SESSION_PERSISTENT, this);
        startForeground(Constants.FOREGROUND_SERVICE, notification);
    }

    private void openMenu() {
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
