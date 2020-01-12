package com.checkin.app.checkin.data.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Utility.ProgressRequestBody;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.data.notifications.Constants.CHANNEL;
import com.checkin.app.checkin.data.notifications.Constants.CHANNEL_GROUP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static androidx.core.app.NotificationCompat.PRIORITY_LOW;
import static com.checkin.app.checkin.data.notifications.Constants.FORMAT_SP_KEY_NOTIFICATION_CHANNEL;
import static com.checkin.app.checkin.data.notifications.Constants.KEY_DATA;
import static com.checkin.app.checkin.data.notifications.Constants.SP_TABLE_NOTIFICATION;

public class MessageUtils {
    private static final String TAG = MessageUtils.class.getSimpleName();

    private static final Map<String, List<Integer>> notifTagToIds = new HashMap<>();
    private static final Handler sHandler = new Handler(Looper.myLooper());

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static List<NotificationChannel> createChannels(CHANNEL_GROUP group, final int importance, CHANNEL... channelTypes) {
        List<NotificationChannel> channels = new ArrayList<>(channelTypes.length);
        for (CHANNEL channelType : channelTypes) {
            NotificationChannel channel = new NotificationChannel(channelType.id, channelType.title, importance);
            channel.setGroup(group.id);
            channels.add(channel);
        }
        return channels;
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

            List<NotificationChannel> channels;
            channels = createChannels(CHANNEL_GROUP.DEFAULT_USER, NotificationManager.IMPORTANCE_DEFAULT, CHANNEL.DEFAULT);
            channels.addAll(createChannels(CHANNEL_GROUP.RESTAURANT_CUSTOMER, NotificationManager.IMPORTANCE_DEFAULT, CHANNEL.ACTIVE_SESSION));
            channels.addAll(createChannels(CHANNEL_GROUP.RESTAURANT_CUSTOMER, NotificationManager.IMPORTANCE_DEFAULT, CHANNEL.ACTIVE_SESSION_PERSISTENT));
            channels.addAll(createChannels(CHANNEL_GROUP.MISC, NotificationManager.IMPORTANCE_LOW, CHANNEL.MEDIA_UPLOAD));

            notificationManager.createNotificationChannels(channels);
        }
    }

    public static void createManagerChannels(NotificationManager notificationManager) {
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER);

            List<NotificationChannel> channels;
            channels = createChannels(CHANNEL_GROUP.RESTAURANT_MEMBER, NotificationManager.IMPORTANCE_HIGH, CHANNEL.MEMBER, CHANNEL.MANAGER);
            notificationManager.createNotificationChannels(channels);
        }
    }

    public static void createWaiterChannels(NotificationManager notificationManager) {
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER);

            List<NotificationChannel> channels;
            channels = createChannels(CHANNEL_GROUP.RESTAURANT_MEMBER, NotificationManager.IMPORTANCE_HIGH, CHANNEL.MEMBER, CHANNEL.WAITER);
            notificationManager.createNotificationChannels(channels);
        }
    }

    public static void createAdminChannels(NotificationManager notificationManager) {
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER);

            List<NotificationChannel> channels;
            channels = createChannels(CHANNEL_GROUP.RESTAURANT_MEMBER, NotificationManager.IMPORTANCE_DEFAULT, CHANNEL.MEMBER, CHANNEL.ADMIN);
            notificationManager.createNotificationChannels(channels);
        }
    }

    public static void createActiveCustomerChannels(NotificationManager notificationManager) {
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_CUSTOMER);

            List<NotificationChannel> channels;
            channels = createChannels(CHANNEL_GROUP.RESTAURANT_CUSTOMER, NotificationManager.IMPORTANCE_MAX, CHANNEL.ACTIVE_SESSION_PERSISTENT);
            notificationManager.createNotificationChannels(channels);

            channels.get(0).setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        }
    }

    public static void createOrderChannels(Context context, NotificationManager notificationManager) {
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelGroups(notificationManager, CHANNEL_GROUP.RESTAURANT_MEMBER);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            List<NotificationChannel> channels;
            channels = createChannels(CHANNEL_GROUP.RESTAURANT_MEMBER, NotificationManager.IMPORTANCE_HIGH, CHANNEL.ORDERS);

            for (NotificationChannel channel : channels) {
                channel.setSound(Constants.getAlertOrdersSoundUri(context), audioAttributes);
                channel.enableVibration(true);
                channel.enableLights(true);
                channel.setBypassDnd(true);
            }

            notificationManager.createNotificationChannels(channels);
        }
    }

    public static void registerLocalReceiver(Context context, BroadcastReceiver receiver, @NonNull MESSAGE_TYPE... types) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(Constants.FCM_INTENT_CATEGORY);
        for (MESSAGE_TYPE type : types)
            intentFilter.addAction(type.getActionTag());
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
            intentFilter.addAction(type.getActionTag());
        LocalBroadcastManager.getInstance(context.getApplicationContext())
                .registerReceiver(receiver, intentFilter);
    }

    public static void unregisterLocalReceiver(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(context.getApplicationContext())
                .unregisterReceiver(receiver);
    }

    public static boolean sendLocalBroadcast(Context context, @NonNull MessageModel data) {
        Intent intent = new Intent(data.getType().getActionTag());
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
            case ORDERS:
                createOrderChannels(context, notificationManager);
                break;
            case ACTIVE_SESSION_PERSISTENT:
                createActiveCustomerChannels(notificationManager);
                break;
            case ACTIVE_SESSION:
            case MEDIA_UPLOAD:
            case DEFAULT:
                createDefaultChannels(notificationManager);

        }
    }

    public static void setEnableNotification(Context context, boolean status, CHANNEL... channels) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_TABLE_NOTIFICATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (CHANNEL channel : channels) {
            editor.putBoolean(String.format(Locale.ENGLISH, FORMAT_SP_KEY_NOTIFICATION_CHANNEL, channel), status);
        }
        editor.apply();
    }

    public static boolean isNotificationEnabled(Context context, CHANNEL channel) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_TABLE_NOTIFICATION, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(String.format(Locale.ENGLISH, FORMAT_SP_KEY_NOTIFICATION_CHANNEL, channel), true);
    }

    public static void dismissNotification(Context context, MessageObjectModel.MESSAGE_OBJECT_TYPE objectType, long objectPk) {
        final String notifTag = Constants.getNotificationTag(objectType, objectPk);
        sHandler.post(() -> {
            NotificationManager notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
            if (notificationManager == null) return;
            List<Integer> notifIdList = Utils.getOrDefault(notifTagToIds, notifTag, null);
            if (notifIdList != null) {
                for (int notifId : notifIdList)
                    notificationManager.cancel(notifTag, notifId);
            }
            notifTagToIds.remove(notifTag);
        });
    }

    protected static void saveNotificationId(String notifTag, final int notificationId) {
        sHandler.post(() -> {
            List<Integer> notifIdList = Utils.getOrDefault(notifTagToIds, notifTag, new ArrayList<>());
            notifIdList.add(notificationId);
            notifTagToIds.put(notifTag, notifIdList);
        });
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
