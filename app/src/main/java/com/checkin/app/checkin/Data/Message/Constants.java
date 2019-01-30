package com.checkin.app.checkin.Data.Message;

public class Constants {
    public static final String FCM_INTENT_CATEGORY = "checkin.fcm_real_time";
    public static final String KEY_DATA = "fcm.message_data";

    public enum CHANNEL_GROUP {
        DEFAULT_USER("group.user", "User"),
        RESTAURANT_CUSTOMER("group.customer", "Restaurant Customer"),
        RESTAURANT_MEMBER("group.member", "Restaurant Member");

        String id, title;

        CHANNEL_GROUP(String id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    public enum CHANNEL {
        DEFAULT("channel.default", "Default"),
        // User group
        FRIEND_REQUEST("channel.friend_requests", "Friend Requests"),
        ACTIVE_SESSION("channel.active_session", "Active Session");

        String id, title;

        CHANNEL(String id, String title) {
            this.id = id;
            this.title = title;
        }
    }
}
