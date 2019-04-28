package com.checkin.app.checkin.Utility;

import com.checkin.app.checkin.BuildConfig;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class Constants {
    public static final String API_VERSION = "0.8.6";
    public static final String API_PROTOCOL = "https://";
    public static final String API_HOST = getApiHost();

    public static final String ACCOUNT_TYPE = "com.checkin.accounts";
    public static final String ACCOUNT_UID = "account_uid";
    public static final String ACCOUNT_NAME = "account_name";
    public static final String ACCOUNT_PIC = "account_pic";

    public static final String SP_SESSION_RESTAURANT_PK = "session_active.restaurant.pk";
    public static final String SP_SESSION_ACTIVE_PK = "session_active.pk";
    public static final String SP_SYNC_DEVICE_TOKEN = "device_token";
    public static final String SP_LAST_ACCOUNT_TYPE = "last_account.type";
    public static final String SP_LAST_ACCOUNT_PK = "last_account.pk";

    public static final String SP_MANAGER_LIVE_ORDER_ACTIVE_KEY = "com.checkin.app.checkin.sp.manager.live.order.key";
    public static final String SP_SHOP_PREFERENCES_TABLE = "com.checkin.app.checkin.sp.shop.preferences";

    public static final String TARGET_ID = "target_id";
    public static final String LAST_NOTIF_ID = "last_notif_id";
    public static final int NEW_NOTIF_RANGE = 2;//number of days

    public static final long DEFAULT_ORDER_CANCEL_DURATION = MILLISECONDS.convert(5, MINUTES);
    public static final long DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT = MILLISECONDS.convert(1, MINUTES);

    public final static String EXPAND_TEXT = "....";

    public static final String PAYTM_CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";

    private Constants() {
    }

    private static String getApiHost() {
        if (!BuildConfig.DEBUG && BuildConfig.BUILD_TYPE.equalsIgnoreCase("release"))
            return "api.check-in.in";
        return "dev.api.check-in.in";
    }
}
