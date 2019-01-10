package com.checkin.app.checkin.Data.Message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.checkin.app.checkin.Auth.DeviceTokenService;
import com.checkin.app.checkin.Data.Converters;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Map;

public class AppMessagingService extends FirebaseMessagingService {
    private static final String TAG = AppMessagingService.class.getSimpleName();
    private LocalBroadcastManager mBroadcastManager;
    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        mNotificationManager = ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE));
        Utils.createDefaultChannels(mNotificationManager);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> params = remoteMessage.getData();

        if (params == null) {
            return;
        }
        MessageModel data;
        try {
            String json = Converters.objectMapper.writeValueAsString(params);
            data = Converters.objectMapper.readValue(json, MessageModel.class);
            Log.e(TAG, data.toString());
        } catch (IOException e) {
            Log.e(TAG, "Couldn't parse FCM remote data.", e);
            return;
        }

        boolean shouldShowNotification = data.shouldShowNotification();

        Intent intent = new Intent(this, data.getTargetActivity());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if (data.shouldTryUpdateUi()) {
            shouldShowNotification = shouldShowNotification && !mBroadcastManager.sendBroadcast(intent);
        }

        if (shouldShowNotification) {
            int notificationId = Utils.getNotificationId();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = data.getNotificationBuilder(this, notificationId)
                    .setContentIntent(pendingIntent)
                    .build();
            mNotificationManager.notify(notificationId, notification);
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Intent intent = new Intent(getApplicationContext(), DeviceTokenService.class);
        intent.putExtra(DeviceTokenService.KEY_TOKEN, token);
        startService(intent);
    }
}
