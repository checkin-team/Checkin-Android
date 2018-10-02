package com.checkin.app.checkin.Utility;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Constants {
    public static final String API_VERSION = "v0.1";
    public static final String API_PROTOCOL = "http://";
    public static final String API_HOST = "172.17.37.59:80";

    public static final String ACCOUNT_TYPE = "com.checkin.accounts";
    public static final String ACCOUNT_UID = "account_uid";

    public static final String SP_LOGGED_IN = "logged_in";
    public static final String SP_USERNAME = "username";
    public static final String SP_USER_ID = "user_id";
    public static final String SP_LOGIN_TOKEN = "token";
    public static final String SP_CHECKED_IN = "checked_in";
    public static final String SP_SYNC_DEVICE_TOKEN = "device_token";

    public static final String EXTRA_SELECTED_USER_ID = "extra_selected_user_id";

    public static final String SHOP_ID = "shop_id";
    public static final String USER_ID = "user_id";
    public static final String SESSION_ID = "session_id";
    public static final String TARGET_ID = "target_id";
    public static final String LAST_NOTIF_ID = "last_notif_id";
    public static final int NEW_NOTIF_RANGE = 2;//number of days

    public static final String API_URL_DECRYPT_QR = "/qr/decrypt/";
    public static final String API_URL_SHOP_PROFILE_URL = "/shops/%(" + SHOP_ID + ")/";
    public static final String API_URL_USER_PROFILE_URL = "/users/%(" + USER_ID + ")/";

    public static final long DEFAULT_ORDER_CANCEL_DURATION = MILLISECONDS.convert(3, MINUTES);
    public static final long DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT = MILLISECONDS.convert(1, MINUTES);

    public static final String CHANNEL_ID="myChannelId";

    public static final String CHANNEL_NAME="myChannelName";
    public static final String CHANNEL_DESCRIPTION="my description";
    public static final String FCM_ACTION_CODE = "ACTION_CODE";

    private Constants() {}
}
