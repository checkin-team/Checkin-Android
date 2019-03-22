package com.checkin.app.checkin.Data.Message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import static com.checkin.app.checkin.Data.Message.MessageUtils.isNotificationEnabled;

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
        int notifCount = 1;
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        for (StatusBarNotification statusBarNotification : mNotificationManager.getActiveNotifications()) {
            if (statusBarNotification.getNotification().getGroup() == null) continue;
            if (statusBarNotification.getNotification().getGroup().equals(notifGroup) && statusBarNotification.getTag() != null) {
                style.addLine(statusBarNotification.getTag());
                notifCount++;
            }
        }
        if (notifCount == 1) return;

        style.setBigContentTitle(data.getGroupTitle())
                .setSummaryText(String.format(Locale.getDefault(), "%d events", notifCount))
                .addLine(data.getDescription());
        String groupTitle = String.format(Locale.getDefault(), "%s - %d events", data.getGroupTitle(), notifCount);

        Intent intent = data.getGroupIntent(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification summaryNotif = new NotificationCompat.Builder(this, data.getChannel().id)
                .setContentTitle(this.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setContentText(groupTitle)
                .setGroup(notifGroup)
                .setGroupSummary(true)
                .setStyle(style)
                .setSound(null)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();


        mNotificationManager.notify(null, data.getGroupSummaryID(), summaryNotif);
    }

    private void showNotification(MessageModel data) {
        if (isNotificationEnabled(getApplicationContext(), data.getChannel())) {
            int notificationId = Constants.getNotificationID();
            Notification notification = data.showNotification(this, mNotificationManager, notificationId);

            mNotificationManager.notify(data.getDescription(), notificationId, notification);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && data.isGroupedNotification())
                showGroupedNotifications(data);
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
