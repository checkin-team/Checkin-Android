package com.checkin.app.checkin.Shop;

import android.content.Context;

import com.checkin.app.checkin.Data.Message.Constants.CHANNEL;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Utility.Constants;

public class ShopPreferences {
    public static void setManagerLiveOrdersActivated(Context context, boolean isEnabled) {
        context.getSharedPreferences(Constants.SP_SHOP_PREFERENCES_TABLE, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(Constants.SP_MANAGER_LIVE_ORDER_ACTIVE_KEY, isEnabled)
                .apply();
        MessageUtils.setEnableNotification(context, isEnabled, CHANNEL.MANAGER, CHANNEL.ORDERS);
    }

    public static boolean isManagerLiveOrdersActivated(Context context) {
        return context.getSharedPreferences(Constants.SP_SHOP_PREFERENCES_TABLE, Context.MODE_PRIVATE)
                .getBoolean(Constants.SP_MANAGER_LIVE_ORDER_ACTIVE_KEY, true);
    }
}
