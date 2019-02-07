package com.checkin.app.checkin.Data.Message;

public class Constants {
    public static final String FCM_INTENT_CATEGORY = "checkin.fcm_real_time";
    public static final String KEY_DATA = "fcm.message_data";
    public static final String FILTER_DATA_SCHEME = "checkin";
    public static final String FILTER_DATA_HOST = "realtime.data";
    public static final String FILTER_DATA_TARGET_PATH = "target/%d";


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
