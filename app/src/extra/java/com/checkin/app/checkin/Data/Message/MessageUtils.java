package com.checkin.app.checkin.Data.Message;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.checkin.app.checkin.Data.Message.Constants.CHANNEL;
import com.checkin.app.checkin.Data.Message.Constants.CHANNEL_GROUP;
import com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE;
import com.checkin.app.checkin.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.support.v4.app.NotificationCompat.PRIORITY_LOW;

public class MessageUtils {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createChannels(NotificationManager notificationManager, CHANNEL_GROUP group, final int importance, CHANNEL... channelTypes) {
        List<NotificationChannel> channels = new ArrayList<>(channelTypes.length);
        for (CHANNEL channelType: channelTypes) {
            NotificationChannel channel = new NotificationChannel(channelType.id, channelType.title, importance);
            channel.setGroup(group.id);
            channels.add(channel);
        }
        notificationManager.createNotificationChannels(channels);
    }

    public static void createDefaultChannels(NotificationManager notificationManager) {
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            List<NotificationChannelGroup> channelGroups = new ArrayList<>();
            channelGroups.add(
                    new NotificationChannelGroup(CHANNEL_GROUP.DEFAULT_USER.id, CHANNEL_GROUP.DEFAULT_USER.title));
            channelGroups.add(
                    new NotificationChannelGroup(CHANNEL_GROUP.RESTAURANT_CUSTOMER.id, CHANNEL_GROUP.RESTAURANT_CUSTOMER.title)
            );
            notificationManager.createNotificationChannelGroups(channelGroups);

            createChannels(notificationManager, CHANNEL_GROUP.DEFAULT_USER, NotificationManager.IMPORTANCE_DEFAULT, CHANNEL.DEFAULT, CHANNEL.FRIEND_REQUEST, CHANNEL.ACTIVE_SESSION);
        }
    }

    public static int getNotificationId() {
        return new Random().nextInt();
    }

    public static void registerLocalReceiver(Context context, BroadcastReceiver receiver, @NonNull MESSAGE_TYPE... types) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(Constants.FCM_INTENT_CATEGORY);
        for (MESSAGE_TYPE type: types)
            intentFilter.addAction(type.actionTag());
        LocalBroadcastManager.getInstance(context.getApplicationContext())
                .registerReceiver(receiver, intentFilter);
    }

    public static boolean sendLocalBroadcast(Context context, @NonNull MessageModel data) {
        Intent intent = new Intent(data.getType().actionTag());
        intent.addCategory(Constants.FCM_INTENT_CATEGORY);
        intent.putExtra(Constants.KEY_DATA, data);
        return LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }

    public static PendingIntent launchUserProfile(Context context, long userPk, int notificationId) {
        Intent intent = new Intent(context, UserProfileActivity.class)
                .putExtra(UserProfileActivity.KEY_PROFILE_USER_ID, userPk);
        return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static NotificationCompat.Builder createDownloadNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL.MEDIA_UPLOAD.id);
        builder.setContentTitle("Media upload")
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(PRIORITY_LOW)
                .setProgress(100, 0, false);
        return builder;
    }
}
