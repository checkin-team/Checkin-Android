package com.checkin.app.checkin.Data.Message;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.checkin.app.checkin.Auth.DeviceTokenService;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

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
            this.showNotification(data);
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void showGroupedNotifications(MessageModel data) {
        String notifGroup = data.getGroupKey();
        int notifCount = 0;
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        for (StatusBarNotification statusBarNotification: mNotificationManager.getActiveNotifications()) {
            if (statusBarNotification.getGroupKey().equals(notifGroup) && statusBarNotification.getTag() != null) {
                style.addLine(statusBarNotification.getTag());
                notifCount++;
            }
        }
        style.setBigContentTitle(data.getGroupTitle())
                .setSummaryText(String.format(Locale.getDefault(), "%d events", notifCount));
        String groupTitle = String.format(Locale.getDefault(), "%s - %d events", data.getGroupTitle(), notifCount);
        Notification summaryNotif = new NotificationCompat.Builder(this, data.getChannel().id)
                .setContentTitle(this.getString(R.string.app_name))
                .setContentText(groupTitle)
                .setGroup(notifGroup)
                .setStyle(style)
                .build();

        mNotificationManager.notify(Constants.NOTIFICATION_GROUP_SUMMARY, data.getGroupSummaryID(), summaryNotif);
    }

    private void showNotification(MessageModel data) {
        int notificationId = Constants.getNotificationID();
        Notification notification = data.showNotification(this, mNotificationManager, notificationId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            showGroupedNotifications(data);
        mNotificationManager.notify(data.getDescription(), notificationId, notification);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Intent intent = new Intent(getApplicationContext(), DeviceTokenService.class);
        intent.putExtra(DeviceTokenService.KEY_TOKEN, token);
        startService(intent);
    }
}
