package com.checkin.app.checkin.Data.Message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;

import com.checkin.app.checkin.Data.Message.Constants.CHANNEL;
import com.checkin.app.checkin.Manager.ManagerWorkActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionActivity;
import com.checkin.app.checkin.Waiter.WaiterWorkActivity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MessageModel implements Serializable {
    private MESSAGE_TYPE type;

    @JsonProperty("description")
    private String description;

    @JsonDeserialize(using = MessageObjectModel.MessageObjectDeserializer.class)
    @JsonProperty("actor")
    private MessageObjectModel actor;

    @JsonDeserialize(using = MessageObjectModel.MessageObjectDeserializer.class)
    @JsonProperty("object")
    private MessageObjectModel object;

    @JsonDeserialize(using = MessageObjectModel.MessageObjectDeserializer.class)
    @JsonProperty("target")
    private MessageObjectModel target;

    @JsonDeserialize(using = MessageDataModel.MessageDataDeserializer.class)
    @JsonProperty("data")
    private MessageDataModel data;

    public enum MESSAGE_TYPE {
        NONE(0),

        /* Users */

        // Active Session
        USER_SESSION_MEMBER_ADD_REQUEST(302), USER_SESSION_ADDED_BY_OWNER(303), USER_SESSION_MEMBER_REMOVED(304),
        USER_SESSION_HOST_ASSIGNED(305), USER_SESSION_MEMBER_ADDED(306), USER_SESSION_END(309),
        USER_SESSION_BILL_CHANGE(311), USER_SESSION_EVENT_NEW(312), USER_SESSION_EVENT_UPDATE(313),
        USER_SESSION_ORDER_ACCEPTED(315), USER_SESSION_ORDER_REJECTED(316),

        /* Restaurant */

        // Members
        SHOP_MEMBER_ADDED(505),

        // Manager
        MANAGER_SESSION_NEW(611), MANAGER_SESSION_MEMBER_CHANGE(612), MANAGER_SESSION_HOST_ASSIGNED(613),
        MANAGER_SESSION_NEW_ORDER(615), MANAGER_SESSION_UPDATE_ORDER(616), MANAGER_SESSION_EVENT_SERVICE(617),
        MANAGER_SESSION_EVENT_CONCERN(618), MANAGER_SESSION_BILL_CHANGE(619), MANAGER_SESSION_CHECKOUT_REQUEST(625),

        // Waiter
        WAITER_SESSION_NEW(711), WAITER_SESSION_MEMBER_CHANGE(712), WAITER_SESSION_HOST_ASSIGNED(713),
        WAITER_SESSION_NEW_ORDER(715), WAITER_SESSION_UPDATE_ORDER(716), WAITER_SESSION_EVENT_SERVICE(717),
        WAITER_SESSION_COLLECT_CASH(725), WAITER_SESSION_END(726);

        public int id;

        MESSAGE_TYPE(int id) {
            this.id = id;
        }

        public static MESSAGE_TYPE getById(int id) {
            for (MESSAGE_TYPE type: MESSAGE_TYPE.values()) {
                if (type.id == id)
                    return type;
            }
            return NONE;
        }

        public String actionTag() {
            return String.format(Locale.ENGLISH, "CHECKIN.MESSAGE_TYPE.%d", id);
        }
    }

    @JsonCreator
    public MessageModel() {}

    @JsonProperty("type")
    public void setType(int type) {
        this.type = MESSAGE_TYPE.getById(type);
    }

    public MessageObjectModel getTarget() {
        return target;
    }

    public MESSAGE_TYPE getType() {
        return type;
    }

    @JsonProperty("data")
    public String getData() {
        return data.toString();
    }

    public MessageDataModel getRawData() {
        return data;
    }

    public MessageObjectModel getActor() {
        return actor;
    }

    public String getDescription() {
        return description;
    }

    public MessageObjectModel getObject() {
        return object;
    }

    @Override
    public String toString() {
        return this.type.name() + " -- " + this.description;
    }

    protected String getChannelId() {
        switch (this.type) {
            case USER_SESSION_BILL_CHANGE:
            case USER_SESSION_MEMBER_ADD_REQUEST:
                return CHANNEL.ACTIVE_SESSION.id;
            default:
                return CHANNEL.DEFAULT.id;
        }
    }

    private Intent getNotificationIntent(Context context) {
        Intent intent = new Intent();
        ComponentName componentName = getTargetComponent(context);
        if (componentName != null)
            intent.setComponent(componentName);
        addIntentExtra(intent, componentName != null ? componentName.getClass() : null);
        return intent;
    }

    private void addIntentExtra(Intent intent, @Nullable Class<?> classObj) {
    }

    @Nullable
    private ComponentName getTargetComponent(Context context) {
        if (isUserActiveSessionNotification())
            return new ComponentName(context, ActiveSessionActivity.class);
        if (isShopWaiterNotification())
            return new ComponentName(context, WaiterWorkActivity.class);
        if (isShopManagerNotification())
            return new ComponentName(context, ManagerWorkActivity.class);
        return null;
    }

    private NotificationCompat.Builder getNotificationBuilder(Context context, int notificationId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, this.getChannelId());
        builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(Html.fromHtml(this.description).toString())
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setAutoCancel(true);
        addNotificationExtra(context, builder, notificationId);
        return builder;
    }

    private void addNotificationExtra(Context context, NotificationCompat.Builder builder, int notificationId) {
    }

    void showNotification(Context context, NotificationManager notificationManager, int notificationId) {
        Intent intent = getNotificationIntent(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = getNotificationBuilder(context, notificationId)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(notificationId, notification);
    }

    protected boolean shouldShowNotification() {
        return !TextUtils.isEmpty(description) && !isOnlyUiUpdate();
    }

    protected boolean isOnlyUiUpdate() {
        switch (this.type) {
            case USER_SESSION_BILL_CHANGE:
            case USER_SESSION_END:
            case USER_SESSION_MEMBER_ADD_REQUEST:
            case USER_SESSION_HOST_ASSIGNED:
            case USER_SESSION_ORDER_ACCEPTED:
            case USER_SESSION_ORDER_REJECTED:
            case MANAGER_SESSION_NEW:
            case MANAGER_SESSION_NEW_ORDER:
            case MANAGER_SESSION_EVENT_CONCERN:
            case MANAGER_SESSION_CHECKOUT_REQUEST:
            case WAITER_SESSION_NEW:
            case WAITER_SESSION_NEW_ORDER:
            case WAITER_SESSION_EVENT_SERVICE:
            case WAITER_SESSION_COLLECT_CASH:
                return false;
            default:
                return true;
        }
    }

    boolean shouldTryUpdateUi() {
        switch (this.type) {
            case USER_SESSION_ORDER_ACCEPTED:
            case USER_SESSION_ORDER_REJECTED:
            case SHOP_MEMBER_ADDED:
                return false;
            default:
                return true;
        }
    }

    private boolean isUserActiveSessionNotification() {
        return this.type.id > 300 && this.type.id < 400;
    }

    private boolean isShopWaiterNotification() {
        return this.type.id > 700 && this.type.id < 800;
    }

    private boolean isShopManagerNotification() {
        return this.type.id > 600 && this.type.id < 700;
    }
}
