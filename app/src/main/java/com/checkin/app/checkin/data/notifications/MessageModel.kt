package com.checkin.app.checkin.data.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.checkin.app.checkin.R
import com.checkin.app.checkin.Utility.Utils
import com.checkin.app.checkin.Waiter.WaiterWorkActivity
import com.checkin.app.checkin.data.notifications.Constants.CHANNEL
import com.checkin.app.checkin.data.notifications.MessageDataModel.MessageDataDeserializer
import com.checkin.app.checkin.data.notifications.MessageObjectModel.MESSAGE_OBJECT_TYPE
import com.checkin.app.checkin.data.notifications.MessageObjectModel.MessageObjectDeserializer
import com.checkin.app.checkin.home.activities.SplashActivity
import com.checkin.app.checkin.manager.activities.ManagerSessionActivity
import com.checkin.app.checkin.manager.activities.ManagerWorkActivity
import com.checkin.app.checkin.manager.fragments.RestaurantOrdersFragmentType
import com.checkin.app.checkin.session.activesession.ActiveSessionActivity
import com.checkin.app.checkin.session.scheduled.activities.PreorderSessionDetailActivity
import com.checkin.app.checkin.session.scheduled.activities.QSRFoodReadyActivity
import com.checkin.app.checkin.session.scheduled.activities.QSRSessionDetailActivity
import com.checkin.app.checkin.user.activities.SuccessfulTransactionActivity
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.io.IOException
import java.io.Serializable
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageModel(
        val description: String?,
        @JsonDeserialize(using = MessageObjectDeserializer::class) val actor: MessageObjectModel?,
        @JsonDeserialize(using = MessageObjectDeserializer::class) val `object`: MessageObjectModel?,
        @JsonDeserialize(using = MessageObjectDeserializer::class) val target: MessageObjectModel?,
        @JsonDeserialize(using = MessageDataDeserializer::class)
        @JsonProperty("data") val rawData: MessageDataModel?,
        @JsonDeserialize(using = MessageTypeDeserializer::class) val type: MESSAGE_TYPE
) : Serializable {

    @get:JsonProperty("data")
    val data: String
        get() = rawData.toString()

    val formatDescription: String? = description?.let { Utils.fromHtml(it).toString() }

    val channel: CHANNEL
        get() = when (type) {
            MESSAGE_TYPE.MANAGER_SESSION_ORDERS_PUSH, MESSAGE_TYPE.COOK_SESSION_ORDERS_PUSH, MESSAGE_TYPE.WAITER_SESSION_ORDERS_PUSH, MESSAGE_TYPE.WAITER_ORDER_COOKED_NOTIFY_HOST -> CHANNEL.ORDERS
            MESSAGE_TYPE.SHOP_MEMBER_ADDED -> CHANNEL.MEMBER
            else -> when {
                isUserActiveSessionNotification -> CHANNEL.ACTIVE_SESSION
                isShopManagerNotification -> CHANNEL.MANAGER
                isShopWaiterNotification -> CHANNEL.WAITER
                isUserCBYGSessionNotification or isUserQSRSessionNotification -> CHANNEL.SCHEDULED_SESSION
                else -> CHANNEL.DEFAULT
            }
        }

    private fun getDefaultIntent(context: Context): Intent? = when {
        type == MESSAGE_TYPE.USER_SCHEDULED_QSR_DONE && sessionDetail != null -> QSRFoodReadyActivity.withSessionIntent(context, sessionDetail!!.pk)
        isUserCBYGSessionNotification && sessionDetail != null -> PreorderSessionDetailActivity.withSessionIntent(context, sessionDetail!!.pk)
        isUserQSRSessionNotification && sessionDetail != null -> QSRSessionDetailActivity.withSessionIntent(context, sessionDetail!!.pk)
        else -> null
    }.also { it?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) }

    private fun getNotificationIntent(context: Context): Intent {
        val componentName = getTargetComponent(context)
        return getDefaultIntent(context) ?: Intent.makeRestartActivityTask(componentName
                ?: ComponentName(context, SplashActivity::class.java)).apply {
            addIntentExtra(this, componentName?.className)
        }
    }

    private fun addIntentExtra(intent: Intent, className: String?) {
        if (className == null) return
        val shopDetail = shopDetail
        val sessionDetail = sessionDetail
        if (isShopManagerNotification && shopDetail != null) {
            if (type == MESSAGE_TYPE.MANAGER_SESSION_END) intent.putExtra(ManagerWorkActivity.KEY_NOTIF_OPEN_LAST_TABLES, true)
            val sessionType = when {
                type.id > 670 -> RestaurantOrdersFragmentType.MASTER_QR
                type.id > 650 -> RestaurantOrdersFragmentType.PRE_ORDER
                else -> RestaurantOrdersFragmentType.ACTIVE_SESSION
            }
            val bundle = bundleOf(
                    ManagerWorkActivity.KEY_NOTIF_LIVE_ORDER_TYPE to sessionType,
                    ManagerWorkActivity.KEY_NOTIF_SESSION_PK to sessionDetail?.pk,
                    ManagerSessionActivity.KEY_OPEN_ORDERS to (type == MESSAGE_TYPE.MANAGER_SESSION_ORDERS_PUSH)
            )
            intent.putExtra(ManagerWorkActivity.KEY_RESTAURANT_PK, shopDetail.pk)
                    .putExtra(ManagerWorkActivity.KEY_SESSION_NOTIFICATION_BUNDLE, bundle)
        } else if (isShopWaiterNotification && shopDetail != null) {
            intent.putExtra(WaiterWorkActivity.KEY_SHOP_PK, shopDetail.pk)
                    .putExtra(WaiterWorkActivity.KEY_SESSION_PK, sessionDetail?.pk ?: 0L)
        } else if (isUserReviewNotification && sessionDetail != null)
            intent.putExtra(SuccessfulTransactionActivity.KEY_SESSION_ID, sessionDetail.pk)
    }

    private fun getTargetComponent(context: Context) = when {
        isUserActiveSessionNotification -> ComponentName(context, ActiveSessionActivity::class.java)
        isShopWaiterNotification -> ComponentName(context, WaiterWorkActivity::class.java)
        isShopManagerNotification -> ComponentName(context, ManagerWorkActivity::class.java)
        isUserReviewNotification -> ComponentName(context, SuccessfulTransactionActivity::class.java)
        else -> null
    }

    private fun getNotificationBuilder(context: Context, pendingIntent: PendingIntent): NotificationCompat.Builder {
        val channel = channel
        val builder = NotificationCompat.Builder(context, channel.id)
        MessageUtils.createRequiredChannel(channel, context)
        var summary = formatDescription ?: ""
        var isBigText = false
        if (summary.length > 30) {
            isBigText = true
            summary = summary.substring(0, 30) + "..."
        }
        builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(summary)
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setAutoCancel(true)
        if (isBigText) builder.setStyle(NotificationCompat.BigTextStyle().bigText(formatDescription))
        addNotificationExtra(context, builder, pendingIntent)
        return builder
    }

    private fun addNotificationExtra(context: Context, builder: NotificationCompat.Builder, pendingIntent: PendingIntent) {
        if (isShopWaiterNotification || isShopManagerNotification) builder.priority = Notification.PRIORITY_HIGH
        if (type == MESSAGE_TYPE.MANAGER_SESSION_ORDERS_PUSH || type == MESSAGE_TYPE.WAITER_SESSION_ORDERS_PUSH) {
            builder.setSound(Constants.getAlertOrdersSoundUri(context))
                    .setFullScreenIntent(pendingIntent, true)
        } else if (type == MESSAGE_TYPE.USER_SCHEDULED_QSR_DONE) {
            builder.setFullScreenIntent(pendingIntent, true)
        }
        if (type == MESSAGE_TYPE.WAITER_SESSION_NEW) {
            val waiterIntent = Intent(context, WaiterWorkActivity::class.java)
            waiterIntent.action = WaiterWorkActivity.ACTION_NEW_TABLE
            waiterIntent.putExtra(WaiterWorkActivity.KEY_SESSION_QR_ID, rawData!!.sessionQRId)
            val waiterPendingIntent = PendingIntent.getActivity(context, 0, waiterIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.addAction(R.drawable.ic_action_show_menu, "Take Order", waiterPendingIntent)
        }
        tryGroupNotification(builder)
    }

    fun showNotification(context: Context): Notification {
        val intent = getNotificationIntent(context)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return getNotificationBuilder(context, pendingIntent)
                .setContentIntent(pendingIntent)
                .build()
    }

    fun shouldShowNotification(): Boolean = !description.isNullOrBlank()

    private fun tryGroupNotification(builder: NotificationCompat.Builder) {
        groupKey?.let { builder.setGroup(it) }
    }

    val groupKey: String?
        get() {
            if (isGroupedNotification) {
                val session = sessionDetail ?: return null
                return Constants.getNotificationGroup(session.type, session.pk)
            }
            return null
        }

    val notificationTag: String
        get() {
            val session = sessionDetail
            if (session != null) return Constants.getNotificationTag(session.type, session.pk)
            val objectModel = `object` ?: target
            return if (objectModel != null) Constants.getNotificationTag(objectModel.type, objectModel.pk) else Constants.getNotificationTag(MESSAGE_OBJECT_TYPE.NONE, 0)
        }

    val isGroupedNotification: Boolean
        get() = isShopManagerNotification || isShopWaiterNotification || isUserActiveSessionNotification || isUserQSRSessionNotification || isUserCBYGSessionNotification

    val groupSummaryID: Int
        get() {
            val session = sessionDetail ?: return 0
            return Constants.getNotificationSummaryID(session.type, session.pk)
        }

    fun getGroupIntent(context: Context): Intent {
        val intent = Intent()
        val componentName = getTargetComponent(context)
        if (componentName != null) {
            intent.component = componentName
            val shopDetail = shopDetail
            val sessionDetail = sessionDetail
            if (shopDetail == null) return intent
            if (isShopManagerNotification) {
                val bundle = Bundle()
                bundle.putLong(ManagerSessionActivity.KEY_SESSION_PK, sessionDetail?.pk ?: 0L)
                intent.putExtra(ManagerWorkActivity.KEY_RESTAURANT_PK, shopDetail.pk)
                        .putExtra(ManagerWorkActivity.KEY_SESSION_NOTIFICATION_BUNDLE, bundle)
            } else if (isShopWaiterNotification) {
                intent.putExtra(WaiterWorkActivity.KEY_SHOP_PK, shopDetail.pk)
                        .putExtra(WaiterWorkActivity.KEY_SESSION_PK, sessionDetail?.pk ?: 0L)
            }
        }
        return intent
    }

    val groupTitle: String = sessionDetail?.displayName ?: shopDetail?.displayName ?: ""

    val isOnlyUiUpdate: Boolean = when (type) {
        MESSAGE_TYPE.USER_SESSION_ADDED_BY_OWNER,
        MESSAGE_TYPE.USER_SESSION_BILL_CHANGE,
        MESSAGE_TYPE.USER_SESSION_END,
        MESSAGE_TYPE.USER_SESSION_MEMBER_ADD_REQUEST,
        MESSAGE_TYPE.USER_SESSION_HOST_ASSIGNED,
        MESSAGE_TYPE.USER_SESSION_ORDER_ACCEPTED_REJECTED,
        MESSAGE_TYPE.MANAGER_SESSION_NEW,
        MESSAGE_TYPE.MANAGER_SESSION_EVENT_CONCERN,
        MESSAGE_TYPE.MANAGER_SESSION_CHECKOUT_REQUEST,
        MESSAGE_TYPE.MANAGER_SESSION_END,
        MESSAGE_TYPE.WAITER_SESSION_NEW,
        MESSAGE_TYPE.WAITER_SESSION_EVENT_SERVICE,
        MESSAGE_TYPE.WAITER_SESSION_COLLECT_CASH,
        MESSAGE_TYPE.WAITER_SESSION_END,
        MESSAGE_TYPE.COOK_SESSION_END,
        MESSAGE_TYPE.USER_SCHEDULED_CBYG_ACCEPTED,
        MESSAGE_TYPE.USER_SCHEDULED_CBYG_PREPARATION,
        MESSAGE_TYPE.USER_SCHEDULED_QSR_ACCEPTED,
        MESSAGE_TYPE.USER_SCHEDULED_QSR_PREPARATION,
        MESSAGE_TYPE.USER_SCHEDULED_QSR_DONE,
        MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_NEW_PAID,
        MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_PREPARATION_START,
        MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_CANCELLED,
        MESSAGE_TYPE.MANAGER_SCHEDULED_CBYG_AUTO_CANCELLED,
        MESSAGE_TYPE.MANAGER_SCHEDULED_QSR_NEW_PAID,
        MESSAGE_TYPE.MANAGER_SCHEDULED_QSR_CANCELLED
        -> false
        else -> true
    }

    fun shouldTryUpdateUi(): Boolean = when (type) {
        MESSAGE_TYPE.USER_SESSION_ORDER_ACCEPTED_REJECTED,
        MESSAGE_TYPE.MANAGER_SESSION_ORDERS_PUSH,
        MESSAGE_TYPE.WAITER_SESSION_ORDERS_PUSH,
        MESSAGE_TYPE.COOK_SESSION_ORDERS_PUSH,
        MESSAGE_TYPE.USER_ACTIVITY_REQUEST_REVIEW,
        MESSAGE_TYPE.WAITER_ORDER_COOKED_NOTIFY_HOST,
        MESSAGE_TYPE.USER_SCHEDULED_CBYG_DONE,
        MESSAGE_TYPE.USER_SCHEDULED_CBYG_AUTO_CANCELLED,
        MESSAGE_TYPE.USER_SCHEDULED_CBYG_CANCELLED,
        MESSAGE_TYPE.USER_SCHEDULED_CBYG_REMINDER,
        MESSAGE_TYPE.USER_SCHEDULED_CBYG_LATE,
        MESSAGE_TYPE.USER_SCHEDULED_QSR_CANCELLED
        -> false
        else -> true
    }

    private val isUserActiveSessionNotification: Boolean = type.id in 301..349

    private val isUserCBYGSessionNotification: Boolean = type.id in 351..370

    private val isUserQSRSessionNotification: Boolean = type.id in 371..399

    private val isShopWaiterNotification: Boolean = type.id in 701..799

    private val isShopManagerNotification: Boolean = type.id in 601..699

    private val isUserReviewNotification: Boolean = type == MESSAGE_TYPE.USER_ACTIVITY_REQUEST_REVIEW

    val shopDetail: MessageObjectModel?
        get() = target?.takeIf { it.type == MESSAGE_OBJECT_TYPE.RESTAURANT }
                ?: `object`?.takeIf { it.type == MESSAGE_OBJECT_TYPE.RESTAURANT }
                ?: actor?.takeIf { it.type == MESSAGE_OBJECT_TYPE.RESTAURANT }

    val sessionDetail: MessageObjectModel?
        get() = target?.takeIf { it.type == MESSAGE_OBJECT_TYPE.SESSION }
                ?: `object`?.takeIf { it.type == MESSAGE_OBJECT_TYPE.SESSION }

    override fun toString(): String = "${type.name} -- $description"
}

enum class MESSAGE_TYPE(var id: Int) {
    NONE(0),

    /* Users */
    // Activity
    USER_ACTIVITY_REQUEST_REVIEW(220),

    // Active Session
    USER_SESSION_MEMBER_ADD_REQUEST(302),
    USER_SESSION_ADDED_BY_OWNER(303), USER_SESSION_MEMBER_REMOVED(304),
    USER_SESSION_HOST_ASSIGNED(305), USER_SESSION_MEMBER_ADDED(306), USER_SESSION_END(309),
    USER_SESSION_BILL_CHANGE(311), USER_SESSION_EVENT_NEW(312), USER_SESSION_EVENT_UPDATE(313),
    USER_SESSION_ORDER_NEW(314), USER_SESSION_ORDER_ACCEPTED_REJECTED(315), USER_SESSION_UPDATE_ORDER(316),
    USER_SESSION_PROMO_AVAILED(321), USER_SESSION_PROMO_REMOVED(322),

    // Scheduled Session
    USER_SCHEDULED_CBYG_ACCEPTED(354),
    USER_SCHEDULED_CBYG_PREPARATION(355), USER_SCHEDULED_CBYG_AUTO_CANCELLED(356),
    USER_SCHEDULED_CBYG_CANCELLED(357), USER_SCHEDULED_CBYG_DONE(358), USER_SCHEDULED_CBYG_CHECKOUT(359),
    USER_SCHEDULED_CBYG_REMINDER(361), USER_SCHEDULED_CBYG_LATE(362),
    USER_SCHEDULED_QSR_ACCEPTED(374), USER_SCHEDULED_QSR_PREPARATION(375), USER_SCHEDULED_QSR_CANCELLED(377),
    USER_SCHEDULED_QSR_CHECKOUT(379), USER_SCHEDULED_QSR_DONE(381),

    /* Restaurant */
    // Members
    SHOP_MEMBER_ADDED(505),

    // Manager
    MANAGER_SESSION_NEW(611),
    MANAGER_SESSION_MEMBER_CHANGE(612), MANAGER_SESSION_HOST_ASSIGNED(613),
    MANAGER_SESSION_NEW_ORDER(615), MANAGER_SESSION_UPDATE_ORDER(616), MANAGER_SESSION_EVENT_SERVICE(617),
    MANAGER_SESSION_EVENT_CONCERN(618), MANAGER_SESSION_EVENT_UPDATE(619), MANAGER_SESSION_ORDERS_PUSH(620),
    MANAGER_SESSION_BILL_CHANGE(623), MANAGER_SESSION_CHECKOUT_REQUEST(625), MANAGER_SESSION_END(626),
    MANAGER_SESSION_SWITCH_TABLE(621),

    // Manager Scheduled
    MANAGER_SCHEDULED_CBYG_NEW_PAID(652),
    MANAGER_SCHEDULED_CBYG_PREPARATION_START(655),
    MANAGER_SCHEDULED_CBYG_CANCELLED(657), MANAGER_SCHEDULED_CBYG_AUTO_CANCELLED(658),
    MANAGER_SCHEDULED_QSR_NEW_PAID(672), MANAGER_SCHEDULED_QSR_CANCELLED(677),

    // Waiter
    WAITER_SESSION_NEW(711),
    WAITER_SESSION_MEMBER_CHANGE(712), WAITER_SESSION_HOST_ASSIGNED(713),
    WAITER_SESSION_NEW_ORDER(715), WAITER_SESSION_UPDATE_ORDER(716), WAITER_SESSION_EVENT_SERVICE(717),
    WAITER_ORDER_COOKED_NOTIFY_HOST(718), WAITER_SESSION_EVENT_UPDATE(719), WAITER_SESSION_ORDERS_PUSH(720),
    WAITER_SESSION_COLLECT_CASH(725), WAITER_SESSION_END(726), WAITER_SESSION_SWITCH_TABLE(721),

    // Cook
    COOK_SESSION_NEW(811),
    COOK_SESSION_HOST_ASSIGNED(813), COOK_SESSION_NEW_ORDER(815),
    COOK_SESSION_UPDATE_ORDER(816), COOK_SESSION_ORDERS_PUSH(820), COOK_SESSION_SWITCH_TABLE(821),
    COOK_SESSION_END(826);

    val actionTag: String = String.format(Locale.ENGLISH, "CHECKIN.MESSAGE_TYPE.%d", id)

    companion object {
        fun getById(id: Int): MESSAGE_TYPE = values().find { it.id == id } ?: NONE
    }
}

class MessageTypeDeserializer : JsonDeserializer<MESSAGE_TYPE>() {
    @Throws(IOException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): MESSAGE_TYPE {
        return MESSAGE_TYPE.getById(jsonParser.text.toInt())
    }
}