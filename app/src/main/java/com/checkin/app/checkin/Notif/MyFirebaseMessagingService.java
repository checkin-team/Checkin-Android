package com.checkin.app.checkin.Notif;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.checkin.app.checkin.Home.HomeActivity;
import com.checkin.app.checkin.Home.UserProfileActivity;
import com.checkin.app.checkin.Utility.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by Jogi Miglani on 23-08-2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String actionCode = remoteMessage.getData().get(Constants.FCM_ACTION_CODE);
        Log.e(TAG, "Action code: " + actionCode);

        Intent intent = new Intent(this, NotifActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        createNotification(remoteMessage, pendingIntent);
        String message = remoteMessage.getData().get("message");
        intent.putExtra("Action Code",actionCode);
        intent.setAction("new notification");
        intent.putExtra("message", message);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);



    }

    private  void createNotification(RemoteMessage remoteMessage,PendingIntent pendingIntent)
    {
        NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this, Constants.CHANNEL_ID);
        notificationBuilder.setContentTitle("FCM NOTIFICATION");
        notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
        notificationBuilder.setSmallIcon(android.support.v4.R.drawable.notification_template_icon_bg);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        Log.d("MyFirebaseToken", "Refreshed token: " + s);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                .putString(Constants.SP_DEVICE_TOKEN, s)
                .apply();
        super.onNewToken(s);
    }
}
