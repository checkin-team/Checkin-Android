package com.checkin.app.checkin.utility

import android.net.Uri

import com.checkin.app.checkin.BuildConfig

import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.MINUTES

object Constants {
    const val API_VERSION = "1.0.0"
    const val API_PROTOCOL = "https://"
    val API_HOST
        get() = if (IS_RELEASE_BUILD()) "api.check-in.in" else "dev.api.check-in.in"

    val PLAY_STORE_URI: Uri = Uri.parse("https://play.google.com/store/apps/details?id=com.checkin.app.checkin")

    const val ACCOUNT_TYPE = "com.checkin.accounts"
    const val ACCOUNT_UID = "account_uid"
    const val SP_USER_PROFILE_NAME = "user.profile.name"
    const val ACCOUNT_PIC = "account_pic"

    const val SP_SESSION_RESTAURANT_PK = "session_active.restaurant.pk"
    const val SP_SESSION_ACTIVE_PK = "session_active.pk"
    const val SP_SYNC_DEVICE_TOKEN = "device_token"
    const val SP_LAST_ACCOUNT_TYPE = "last_account.type"
    const val SP_LAST_ACCOUNT_PK = "last_account.pk"
    const val SP_LAST_USED_PAYMENT_MODE = "last_payment_mode"

    const val SP_MANAGER_LIVE_ORDER_ACTIVE_KEY = "com.checkin.app.checkin.sp.manager.live.order.key"
    const val SP_SHOP_PREFERENCES_TABLE = "com.checkin.app.checkin.sp.shop.preferences"

    val DEFAULT_ORDER_CANCEL_DURATION = MILLISECONDS.convert(5, MINUTES)
    val DEFAULT_OTP_AUTO_RETRIEVAL_TIMEOUT = MILLISECONDS.convert(1, MINUTES)

    fun IS_RELEASE_BUILD() = !BuildConfig.DEBUG && BuildConfig.BUILD_TYPE.equals("release", ignoreCase = true)
}

val pass = Unit
