package com.checkin.app.checkin.Data.Message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;

import com.checkin.app.checkin.Data.Message.Constants.CHANNEL;
import com.checkin.app.checkin.Data.Message.MessageObjectModel.MESSAGE_OBJECT_TYPE;
import com.checkin.app.checkin.Manager.ManagerSessionActivity;
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

import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.MANAGER_SESSION_ORDERS_PUSH;
import static com.checkin.app.checkin.Data.Message.MessageModel.MESSAGE_TYPE.WAITER_SESSION_ORDERS_PUSH;

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
        USER_SESSION_ORDER_NEW(314), USER_SESSION_ORDER_ACCEPTED_REJECTED(315), USER_SESSION_UPDATE_ORDER(316),

        /* Restaurant */

        // Members
        SHOP_MEMBER_ADDED(505),

        // Manager
        MANAGER_SESSION_NEW(611), MANAGER_SESSION_MEMBER_CHANGE(612), MANAGER_SESSION_HOST_ASSIGNED(613),
        MANAGER_SESSION_NEW_ORDER(615), MANAGER_SESSION_UPDATE_ORDER(616), MANAGER_SESSION_EVENT_SERVICE(617),
        MANAGER_SESSION_EVENT_CONCERN(618), MANAGER_SESSION_EVENT_UPDATE(619), MANAGER_SESSION_ORDERS_PUSH(620),
        MANAGER_SESSION_BILL_CHANGE(623), MANAGER_SESSION_CHECKOUT_REQUEST(625), MANAGER_SESSION_END(626),

        // Waiter
        WAITER_SESSION_NEW(711), WAITER_SESSION_MEMBER_CHANGE(712), WAITER_SESSION_HOST_ASSIGNED(713),
        WAITER_SESSION_NEW_ORDER(715), WAITER_SESSION_UPDATE_ORDER(716), WAITER_SESSION_EVENT_SERVICE(717),
        WAITER_SESSION_EVENT_UPDATE(719), WAITER_SESSION_ORDERS_PUSH(720), WAITER_SESSION_COLLECT_CASH(725), WAITER_SESSION_END(726);

        public int id;

        MESSAGE_TYPE(int id) {
            this.id = id;
        }

        public static MESSAGE_TYPE getById(int id) {
            for (MESSAGE_TYPE type : MESSAGE_TYPE.values()) {
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
    public MessageModel() {
    }

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
        return Html.fromHtml(description).toString();
    }

    public MessageObjectModel getObject() {
        return object;
    }

    @Override
    public String toString() {
        return this.type.name() + " -- " + this.description;
    }

    protected CHANNEL getChannel() {
        if (this.type == MANAGER_SESSION_ORDERS_PUSH || this.type == WAITER_SESSION_ORDERS_PUSH)
            return CHANNEL.ORDERS;
        if (isUserActiveSessionNotification())
            return CHANNEL.ACTIVE_SESSION;
        if (isShopWaiterNotification())
            return CHANNEL.WAITER;
        if (isShopManagerNotification())
            return CHANNEL.MANAGER;
        if (this.type == MESSAGE_TYPE.SHOP_MEMBER_ADDED)
            return CHANNEL.MEMBER;
        return CHANNEL.DEFAULT;
    }

    private Intent getNotificationIntent(Context context) {
        ComponentName componentName = getTargetComponent(context);
        Intent intent = Intent.makeRestartActivityTask(componentName);
        addIntentExtra(intent, componentName != null ? componentName.getClassName() : null);
        return intent;
    }

    private void addIntentExtra(Intent intent, @Nullable String className) {
        if (className == null)
            return;
        MessageObjectModel shopDetail = getShopDetail();
        MessageObjectModel sessionDetail = getSessionDetail();
        if (shopDetail == null) return;
        if (isShopManagerNotification()) {
            Bundle bundle = new Bundle();
            bundle.putLong(ManagerSessionActivity.KEY_SESSION_PK, sessionDetail != null ? sessionDetail.getPk() : 0L);
            bundle.putBoolean(ManagerSessionActivity.KEY_OPEN_ORDERS, this.type == MANAGER_SESSION_ORDERS_PUSH);
            intent.putExtra(ManagerWorkActivity.KEY_RESTAURANT_PK, shopDetail.getPk())
                    .putExtra(ManagerWorkActivity.KEY_SESSION_BUNDLE, bundle);
        } else if (isShopWaiterNotification()) {
            intent.putExtra(WaiterWorkActivity.KEY_SHOP_PK, shopDetail.getPk())
                    .putExtra(WaiterWorkActivity.KEY_SESSION_PK, sessionDetail != null ? sessionDetail.getPk() : 0L);
        }
    }

    @Nullable
    private ComponentName getTargetComponent(Context context) {
        ComponentName componentName = null;
        if (isUserActiveSessionNotification())
            componentName = new ComponentName(context, ActiveSessionActivity.class);
        else if (isShopWaiterNotification())
            componentName = new ComponentName(context, WaiterWorkActivity.class);
        else if (isShopManagerNotification())
            componentName = new ComponentName(context, ManagerWorkActivity.class);
        return componentName;
    }

    private NotificationCompat.Builder getNotificationBuilder(Context context, PendingIntent pendingIntent) {
        CHANNEL channel = this.getChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel.id);
        MessageUtils.createRequiredChannel(channel, context);
        builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(getDescription())
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setAutoCancel(true);
        addNotificationExtra(context, builder, pendingIntent);
        return builder;
    }

    private void addNotificationExtra(Context context, NotificationCompat.Builder builder, PendingIntent pendingIntent) {
        if (isShopWaiterNotification() || isShopManagerNotification())
            builder.setPriority(Notification.PRIORITY_HIGH);
        if (this.type == MANAGER_SESSION_ORDERS_PUSH || this.type == WAITER_SESSION_ORDERS_PUSH) {
            builder.setSound(Constants.getAlertOrdersSoundUri(context))
                    .setFullScreenIntent(pendingIntent,true);
        }
        tryGroupNotification(builder);
    }

    public Notification showNotification(Context context, NotificationManager notificationManager, int notificationId) {
        Intent intent = getNotificationIntent(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return getNotificationBuilder(context, pendingIntent)
                .setContentIntent(pendingIntent)
                .build();
    }

    protected boolean shouldShowNotification() {
        return !TextUtils.isEmpty(description) && !isOnlyUiUpdate();
    }

    private void tryGroupNotification(NotificationCompat.Builder builder) {
        String group = getGroupKey();
        if (group != null) {
            builder.setGroup(group);
        }
    }

    @Nullable
    public String getGroupKey() {
        if (isGroupedNotification()) {
            MessageObjectModel session = getSessionDetail();
            if (session == null) return null;
            return Constants.getNotificationGroup(session.getType(), session.getPk());
        }
        return null;
    }

    public boolean isGroupedNotification() {
        return isShopManagerNotification() || isShopWaiterNotification() || isUserActiveSessionNotification();
    }

    public int getGroupSummaryID() {
        MessageObjectModel session = getSessionDetail();
        if (session == null)
            return 0;
        return Constants.getNotificationSummaryID(session.getType(), session.getPk());
    }

    public Intent getGroupIntent(Context context) {
        Intent intent = new Intent();
        ComponentName componentName = getTargetComponent(context);
        if (componentName != null) {
            intent.setComponent(componentName);
            MessageObjectModel shopDetail = getShopDetail();
            MessageObjectModel sessionDetail = getSessionDetail();
            if (shopDetail == null) return intent;
            if (isShopManagerNotification()) {
                Bundle bundle = new Bundle();
                bundle.putLong(ManagerSessionActivity.KEY_SESSION_PK, sessionDetail != null ? sessionDetail.getPk() : 0L);
                intent.putExtra(ManagerWorkActivity.KEY_RESTAURANT_PK, shopDetail.getPk())
                        .putExtra(ManagerWorkActivity.KEY_SESSION_BUNDLE, bundle);
            } else if (isShopWaiterNotification()) {
                intent.putExtra(WaiterWorkActivity.KEY_SHOP_PK, shopDetail.getPk())
                        .putExtra(WaiterWorkActivity.KEY_SESSION_PK, sessionDetail != null ? sessionDetail.getPk() : 0L);
            }
        }
        return intent;
    }

    public String getGroupTitle() {
        MessageObjectModel objectModel;
        objectModel = getSessionDetail();
        if (objectModel != null) return objectModel.getDisplayName();
        objectModel = getShopDetail();
        if (objectModel != null) return objectModel.getDisplayName();
        return "";
    }

    protected boolean isOnlyUiUpdate() {
        switch (this.type) {
            case USER_SESSION_ADDED_BY_OWNER:
            case USER_SESSION_BILL_CHANGE:
            case USER_SESSION_END:
            case USER_SESSION_MEMBER_ADD_REQUEST:
            case USER_SESSION_HOST_ASSIGNED:
            case USER_SESSION_ORDER_ACCEPTED_REJECTED:
            case SHOP_MEMBER_ADDED:
            case MANAGER_SESSION_NEW:
            case MANAGER_SESSION_EVENT_CONCERN:
            case MANAGER_SESSION_CHECKOUT_REQUEST:
            case WAITER_SESSION_NEW:
            case WAITER_SESSION_EVENT_SERVICE:
            case WAITER_SESSION_COLLECT_CASH:
                return false;
            default:
                return true;
        }
    }

    boolean shouldTryUpdateUi() {
        switch (this.type) {
            case USER_SESSION_ORDER_ACCEPTED_REJECTED:
            case SHOP_MEMBER_ADDED:
            case MANAGER_SESSION_ORDERS_PUSH:
            case WAITER_SESSION_ORDERS_PUSH:
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

    @Nullable
    public MessageObjectModel getShopDetail() {
        if (target != null && target.getType() == MESSAGE_OBJECT_TYPE.RESTAURANT)
            return target;
        if (object != null && object.getType() == MESSAGE_OBJECT_TYPE.RESTAURANT)
            return object;
        if (actor != null && actor.getType() == MESSAGE_OBJECT_TYPE.RESTAURANT)
            return actor;
        return null;
    }

    @Nullable
    public MessageObjectModel getSessionDetail() {
        if (target != null && target.getType() == MESSAGE_OBJECT_TYPE.SESSION)
            return target;
        if (object != null && object.getType() == MESSAGE_OBJECT_TYPE.SESSION)
            return object;
        return null;
    }
}
