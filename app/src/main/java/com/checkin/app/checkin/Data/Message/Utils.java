package com.checkin.app.checkin.Data.Message;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.checkin.app.checkin.Data.Message.Constants.CHANNEL_GROUP;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static void createDefaultChannels(Context context) {
        NotificationManager notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannelGroup(
                    new NotificationChannelGroup(CHANNEL_GROUP.DEFAULT_USER.id, CHANNEL_GROUP.DEFAULT_USER.title));
            notificationManager.createNotificationChannelGroup(
                    new NotificationChannelGroup(CHANNEL_GROUP.RESTAURANT_CUSTOMER.id, CHANNEL_GROUP.RESTAURANT_CUSTOMER.title)
            );


            List<NotificationChannel> userChannels = new ArrayList<>();
        }
    }
}
