package com.checkin.app.checkin.data.notifications

import android.content.Context
import android.net.Uri
import androidx.annotation.RawRes
import com.checkin.app.checkin.data.notifications.MessageObjectModel.MESSAGE_OBJECT_TYPE
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object Constants {
    const val FCM_INTENT_CATEGORY = "checkin.fcm_real_time"
    const val KEY_DATA = "fcm.message_data"
    const val FILTER_DATA_SCHEME = "checkin"
    const val FILTER_DATA_TARGET_PATH = "target/%d"
    const val FORMAT_NOTIFICATION_GROUP = "com.checkin.message.group.%s_%d"
    const val FORMAT_NOTIFICATION_TAG = "com.checkin.message.tag.%s_%d"
    const val FORMAT_PROMO_NOTIFICATION_TAG = "com.checkin.promo.tag.%s"
    const val NOTIFICATION_GROUP_SUMMARY = "com.checkin.message.group.summary"
    const val FORMAT_SP_KEY_NOTIFICATION_CHANNEL = "com.checkin.app.checkin.Data.Message.notif.%s"
    const val SP_TABLE_NOTIFICATION = "com.checkin.app.checkin.Data.Message.notification"
    const val SERVICE_ACTION_FOREGROUND_START = "checkin.service.foreground.start"
    const val SERVICE_ACTION_FOREGROUND_STOP = "checkin.service.foreground.stop"
    private val atomicInteger = AtomicInteger(100)
    val notificationID: Int
        get() = atomicInteger.incrementAndGet()

    fun getNotificationGroup(type: MESSAGE_OBJECT_TYPE, objectPk: Long): String {
        return String.format(Locale.getDefault(), FORMAT_NOTIFICATION_GROUP, type.toString(), objectPk)
    }

    fun getNotificationTag(type: MESSAGE_OBJECT_TYPE, objectPk: Long): String {
        return String.format(Locale.getDefault(), FORMAT_NOTIFICATION_TAG, type.toString(), objectPk)
    }

    fun getPromoNotificationTag(link: String) = FORMAT_PROMO_NOTIFICATION_TAG.format(link)

    fun getNotificationSummaryID(type: MESSAGE_OBJECT_TYPE, objectPk: Long): Int {
        return ((type.id + objectPk) % 100).toInt()
    }

    fun getAlertOrdersSoundUri(context: Context, @RawRes soundId: Int): Uri {
        return Uri.parse("android.resource://" + context.packageName + "/" + soundId)
    }

    enum class CHANNEL_GROUP(val id: String, val title: String) {
        DEFAULT_USER("group.user", "User"),
        RESTAURANT_CUSTOMER("group.customer", "Eatery Customer"),
        RESTAURANT_MEMBER("group.member", "Eatery Staff"),
        MISC("group.misc", "Miscellaneous");
    }

    enum class CHANNEL(val id: String, val title: String) {
        DEFAULT("channel.default", "Default"),

        // User group
        ACTIVE_SESSION("channel.active_session", "Active Session"),
        ACTIVE_SESSION_PERSISTENT("channel.active_session_persistent", "Active Session Persistent"),
        SCHEDULED_SESSION("channel.scheduled_session", "Scheduled Session"),

        // Restaurant Member group
        MEMBER("channel.restaurant_member", "Eatery New Staff"),
        ORDERS("channel.restaurant.orders", "New Orders"),
        ORDER_COOKED("channel.session.order_cooked", "Order cooked"),
        EVENT("channel.restaurant.events", "Manager Events"),
        ADMIN("channel.restaurant_admin", "Admin"),
        MANAGER("channel.restaurant_manager", "Manager"),
        WAITER("channel.restaurant_waiter", "Waiter"),
        COOK("channel.restaurant_cook", "Cook"),

        // Misc group
        MEDIA_UPLOAD("channel.media_upload", "Media upload progress"),
        LOCATION_TRACK("channel.track_location", "Track current location"),
        PROMOTIONAL("channel.promotional", "Promotional campaign");
    }
}