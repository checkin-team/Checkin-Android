package com.checkin.app.checkin.Data.Message;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.checkin.app.checkin.Auth.DeviceTokenService;
import com.checkin.app.checkin.Data.Converters;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Map;

public class AppMessagingService extends FirebaseMessagingService {
    private static final String TAG = AppMessagingService.class.getSimpleName();
    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE));
        MessageUtils.createDefaultChannels(mNotificationManager);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> params = remoteMessage.getData();

        if (params == null)
            return;
        MessageModel data;
        try {
            String json = Converters.objectMapper.writeValueAsString(params);
            data = Converters.objectMapper.readValue(json, MessageModel.class);
            Log.e(TAG, data.toString());
        } catch (IOException e) {
            Log.e(TAG, "Couldn't parse FCM remote data.", e);
            return;
        }

        boolean shouldShowNotification;
        if (data.shouldTryUpdateUi()) {
            boolean result = MessageUtils.sendLocalBroadcast(this, data);
            shouldShowNotification = data.shouldShowNotification();
            if (data.isOnlyUiUpdate()) shouldShowNotification = !result && shouldShowNotification;
        } else shouldShowNotification = true;

        if (shouldShowNotification) {
            int notificationId = Constants.getNotificationID();
            data.showNotification(this, mNotificationManager, notificationId);
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
