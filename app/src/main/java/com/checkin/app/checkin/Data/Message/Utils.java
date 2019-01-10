package com.checkin.app.checkin.Data.Message;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.checkin.app.checkin.Data.Message.Constants.CHANNEL;
import com.checkin.app.checkin.Data.Message.Constants.CHANNEL_GROUP;
import com.checkin.app.checkin.User.NonPersonalProfile.UserProfileActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
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


            List<NotificationChannel> defaultChannels = new ArrayList<>();
            NotificationChannel channel1 = new NotificationChannel(CHANNEL.DEFAULT.id, CHANNEL.DEFAULT.title, NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setGroup(CHANNEL_GROUP.DEFAULT_USER.id);
            defaultChannels.add(channel1);

            NotificationChannel channel2 = new NotificationChannel(CHANNEL.FRIEND_REQUEST.id, CHANNEL.FRIEND_REQUEST.title, NotificationManager.IMPORTANCE_DEFAULT);
            channel2.setGroup(CHANNEL_GROUP.DEFAULT_USER.id);
            defaultChannels.add(channel2);
            notificationManager.createNotificationChannels(defaultChannels);
        }
    }

    public static int getNotificationId() {
        return new Random().nextInt();
    }

    public static PendingIntent launchUserProfile(Context context, long userPk, int notificationId) {
        Intent intent = new Intent(context, UserProfileActivity.class)
                .putExtra(UserProfileActivity.KEY_PROFILE_USER_ID, userPk);
        return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
