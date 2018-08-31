package com.checkin.app.checkin.Utility;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class Constants {
    public static final short API_VERSION = 1;
    public static final String API_HOST = "172.17.37.59";
    public static final short API_PORT = 8000;

    public static final String APP_DATABASE = "app_database";

    public static final String SP_LOGGED_IN = "logged_in";
    public static final String SP_USERNAME = "username";
    public static final String SP_USER_ID = "user_id";
    public static final String SP_LOGIN_TOKEN = "token";
    public static final String SP_CHECKED_IN = "checked_in";

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

    private Constants() {}
}
