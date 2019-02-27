package com.checkin.app.checkin.Data.Message;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.checkin.app.checkin.Data.Message.Constants.CHANNEL;
import com.checkin.app.checkin.Data.Message.Constants.CHANNEL_GROUP;
import com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.ProgressRequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static androidx.core.app.NotificationCompat.PRIORITY_LOW;
import static com.checkin.app.checkin.Data.Message.Constants.KEY_DATA;

public class MessageUtils {
    private static final String TAG = MessageUtils.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createChannels(NotificationManager notificationManager, CHANNEL_GROUP group, final int importance, CHANNEL... channelTypes) {
        List<NotificationChannel> channels = new ArrayList<>(channelTypes.length);
        for (CHANNEL channelType : channelTypes) {
            NotificationChannel channel = new NotificationChannel(channelType.id, channelType.title, importance);
            channel.setGroup(group.id);
            channels.add(channel);
        }
        notificationManager.createNotificationChannels(channels);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createChannelGroups(NotificationManager notificationManager, CHANNEL_GROUP... channelGroups) {
        List<NotificationChannelGroup> groups = new ArrayList<>(channelGroups.length);
        for (CHANNEL_GROUP channelGroup : channelGroups) {
            groups.add(new NotificationChannelGroup(channelGroup.id, channelGroup.title));
        }
        notificationManager.createNotificationChannelGroups(groups);
    }

    static void createDefaultChannels(NotificationManager notificationManager) {
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.DEFAULT_USER, CHANNEL_GROUP.RESTAURANT_CUSTOMER, CHANNEL_GROUP.MISC);

            createChannels(notificationManager, CHANNEL_GROUP.DEFAULT_USER, NotificationManager.IMPORTANCE_DEFAULT, CHANNEL.DEFAULT);
            createChannels(notificationManager, CHANNEL_GROUP.RESTAURANT_CUSTOMER, NotificationManager.IMPORTANCE_DEFAULT, CHANNEL.ACTIVE_SESSION);
            createChannels(notificationManager, CHANNEL_GROUP.MISC, NotificationManager.IMPORTANCE_LOW, CHANNEL.MEDIA_UPLOAD);
        }
    }

    public static void createManagerChannels(NotificationManager notificationManager) {
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER);

            createChannels(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER, NotificationManager.IMPORTANCE_HIGH, CHANNEL.MEMBER, CHANNEL.MANAGER);
        }
    }

    public static void createWaiterChannels(NotificationManager notificationManager) {
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER);

            createChannels(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER, NotificationManager.IMPORTANCE_HIGH, CHANNEL.MEMBER, CHANNEL.WAITER);
        }
    }

    public static void createAdminChannels(NotificationManager notificationManager) {
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER);

            createChannels(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER, NotificationManager.IMPORTANCE_DEFAULT, CHANNEL.MEMBER, CHANNEL.ADMIN);
        }
    }

    public static void registerLocalReceiver(Context context, BroadcastReceiver receiver, @NonNull MESSAGE_TYPE... types) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(Constants.FCM_INTENT_CATEGORY);
        for (MESSAGE_TYPE type : types)
            intentFilter.addAction(type.actionTag());
        LocalBroadcastManager.getInstance(context.getApplicationContext())
                .registerReceiver(receiver, intentFilter);
    }

    public static void registerLocalReceiver(Context context, BroadcastReceiver receiver, long targetPk, @NonNull MESSAGE_TYPE... types) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(Constants.FCM_INTENT_CATEGORY);
        intentFilter.addDataScheme(Constants.FILTER_DATA_SCHEME);
        intentFilter.addDataAuthority(Constants.FILTER_DATA_HOST, "");
        intentFilter.addDataPath(String.format(Locale.ENGLISH, Constants.FILTER_DATA_TARGET_PATH, targetPk), 0);
        for (MESSAGE_TYPE type : types)
            intentFilter.addAction(type.actionTag());
        LocalBroadcastManager.getInstance(context.getApplicationContext())
                .registerReceiver(receiver, intentFilter);
    }

    public static void unregisterLocalReceiver(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(context.getApplicationContext())
                .unregisterReceiver(receiver);
    }

    public static boolean sendLocalBroadcast(Context context, @NonNull MessageModel data) {
        Intent intent = new Intent(data.getType().actionTag());
        intent.addCategory(Constants.FCM_INTENT_CATEGORY);
        intent.putExtra(Constants.KEY_DATA, data);
        return LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }

    public static NotificationCompat.Builder createUploadNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL.MEDIA_UPLOAD.id);
        builder.setContentTitle("Media upload")
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setPriority(PRIORITY_LOW)
                .setProgress(100, 0, false);
        return builder;
    }

    public static MessageModel parseMessage(Intent intent) {
        try {
            return ((MessageModel) intent.getSerializableExtra(KEY_DATA));
        } catch (ClassCastException e) {
            Log.e(TAG, "Invalid message object received.");
            e.printStackTrace();
            return null;
        }
    }

    public static void createRequiredChannel(CHANNEL channel, Context context) {
        NotificationManager notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        switch (channel) {
            case ADMIN:
            case MEMBER:
                createAdminChannels(notificationManager);
                break;
            case WAITER:
                createWaiterChannels(notificationManager);
                break;
            case MANAGER:
                createManagerChannels(notificationManager);
                break;
            case ACTIVE_SESSION:
            case MEDIA_UPLOAD:
            case DEFAULT:
                createDefaultChannels(notificationManager);
        }
    }

    public static class NotificationUpdate implements ProgressRequestBody.UploadCallbacks {
        private NotificationCompat.Builder builder;
        private NotificationManager notificationManager;
        private int notificationId;

        public NotificationUpdate(Context context, NotificationCompat.Builder builder) {
            this.notificationId = Constants.getNotificationID();
            this.notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
            this.builder = builder;

            notificationManager.notify(notificationId, builder.build());
        }

        @Override
        public void onProgressUpdate(int percentage) {
            Log.d("Update Status", percentage + "");
            builder.setProgress(100, percentage, false);
            notificationManager.notify(notificationId, builder.build());
        }

        @Override
        public void onSuccess() {
            builder.setContentText("Upload completed.")
                    .setProgress(0, 0, false)
                    .setAutoCancel(true);
            notificationManager.notify(notificationId, builder.build());
        }

        @Override
        public void onFailure() {
            builder.setContentText("Upload error.")
                    .setProgress(0, 0, false)
                    .setAutoCancel(true);
            notificationManager.notify(notificationId, builder.build());
        }
    }
}
