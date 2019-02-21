package com.checkin.app.checkin.Data.Message;

import com.checkin.app.checkin.Data.Message.MessageObjectModel.MESSAGE_OBJECT_TYPE;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class Constants {
    public static final String FCM_INTENT_CATEGORY = "checkin.fcm_real_time";
    public static final String KEY_DATA = "fcm.message_data";
    public static final String FILTER_DATA_SCHEME = "checkin";
    public static final String FILTER_DATA_HOST = "realtime.data";
    public static final String FILTER_DATA_TARGET_PATH = "target/%d";

    public static final String FORMAT_NOTIFICATION_GROUP = "com.checkin.message.group.%s_%d";
    public static final String NOTIFICATION_GROUP_SUMMARY = "com.checkin.message.group.summary";

    private final static AtomicInteger atomicInteger = new AtomicInteger(100);

    public static int getNotificationID() {
        return atomicInteger.incrementAndGet();
    }

    public static String getNotificationGroup(MESSAGE_OBJECT_TYPE type, long objectPk) {
        return String.format(Locale.getDefault(), FORMAT_NOTIFICATION_GROUP, type.toString(), objectPk);
    }

    public static int getNotificationSummaryID(MESSAGE_OBJECT_TYPE type, long objectPk) {
        return ((int) ((type.id + objectPk) % 100));
    }

    public enum CHANNEL_GROUP {
        DEFAULT_USER("group.user", "User"),
        RESTAURANT_CUSTOMER("group.customer", "Restaurant Customer"),
        RESTAURANT_MEMBER("group.member", "Restaurant Member"),
        MISC("group.misc", "Miscellaneous");

        String id, title;

        CHANNEL_GROUP(String id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    public enum CHANNEL {
        DEFAULT("channel.default", "Default"),
        // User group
        ACTIVE_SESSION("channel.active_session", "Active Session"),

        // Restaurant Member group
        MEMBER("channel.restaurant_member", "Restaurant Member"),
        ADMIN("channel.restaurant_admin", "Restaurant Admin"),
        MANAGER("channel.restaurant_manager", "Restaurant Manager"),
        WAITER("channel.restaurant_waiter", "Restaurant Waiter"),

        // Misc group
        MEDIA_UPLOAD("channel.media_upload", "Media upload progress");

        String id, title;

        CHANNEL(String id, String title) {
            this.id = id;
            this.title = title;
        }
    }
}
