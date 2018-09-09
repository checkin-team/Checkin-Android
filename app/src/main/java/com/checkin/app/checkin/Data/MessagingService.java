package com.checkin.app.checkin.Data;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.checkin.app.checkin.Notifications.NotificationActivity;
import com.checkin.app.checkin.Session.ActiveSessionActivity;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Util;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Jogi Miglani on 23-08-2018.
 */

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = MessagingService.class.getSimpleName();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String actionCode = remoteMessage.getData().get(Constants.FCM_ACTION_CODE);
        Log.e(TAG, "Action code: " + actionCode);

        Intent intent = getTargetIntent(actionCode);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String message = remoteMessage.getData().get("message");
        intent.putExtra("message", message);
        intent.setAction(Util.getActivityIntentFilter(getApplicationContext(), actionCode));
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        if (!localBroadcastManager.sendBroadcast(intent)) {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            createNotification(remoteMessage, pendingIntent);
        }
    }

    private Intent getTargetIntent(String actionCode) {
        if (ActiveSessionActivity.IDENTIFIER.equals(actionCode)) {
            return new Intent(this,ActiveSessionActivity.class);
        }
        else return new Intent(this, NotificationActivity.class);
    }

    private  void createNotification(RemoteMessage remoteMessage,PendingIntent pendingIntent)
    {
        createNotificationChannel();
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
    public void onNewToken(String token) {
        Log.d("MyFirebaseToken", "Refreshed token: " + token);
        saveDeviceToken(token);
        super.onNewToken(token);
    }

    private void saveDeviceToken(String token) {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                .putString(Constants.SP_DEVICE_TOKEN, token)
                .apply();
    }

    private void createNotificationChannel()
    {
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(Constants.CHANNEL_ID,Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(Constants.CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.enableVibration(true);

            NotificationManager notificationManager =getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }



    }
}
