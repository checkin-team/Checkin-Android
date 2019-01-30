package com.checkin.app.checkin.Data.Message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Message.Constants.CHANNEL;
import com.checkin.app.checkin.Notifications.NotificationActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.Friendship.FriendshipBroadcastReceiver;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.Serializable;

public class MessageModel implements Serializable {
    private MESSAGE_TYPE type;

    @JsonProperty("actor")
    private MessageObjectModel actor;

    @JsonProperty("description")
    private String description;

    @JsonProperty("object")
    private MessageObjectModel object;

    @JsonProperty("target")
    private MessageObjectModel target;

    @JsonProperty("data")
    private ObjectNode data;

    public enum MESSAGE_TYPE {
        NONE(0),

        /* Users */
        // Friendship
        FRIENDSHIP_REQUEST_RECEIVED(151), FRIENDSHIP_REQUEST_ACCEPTED(152),

        // Activity
        REVIEW_WRITE_REQUEST(220), REVIEW_LIKED(221),

        // Active Session
        SESSION_MEMBER_ADDED(302), SESSION_HOST_ASSIGNED(305), SESSION_END(309),
        SESSION_BILL_CHANGE(311);

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
            return String.valueOf(id);
        }
    }

    public MessageModel() {}

    @JsonProperty("type")
    public void setType(int type) {
        this.type = MESSAGE_TYPE.getById(type);
    }

    @JsonProperty("actor")
    public void setActor(String actor) {
        try {
            this.actor = Converters.objectMapper.readValue(actor, MessageObjectModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @JsonProperty("object")
    public void setObject(String object) {
        try {
            this.object = Converters.objectMapper.readValue(object, MessageObjectModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @JsonProperty("target")
    public void setTarget(String target) {
        try {
            this.target = Converters.objectMapper.readValue(target, MessageObjectModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @JsonProperty("data")
    public void setData(String data) {
        try {
            this.data = Converters.objectMapper.readValue(data, ObjectNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public ObjectNode getRawData() {
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

    public String getChannelId() {
        switch (this.type) {
            case FRIENDSHIP_REQUEST_ACCEPTED:
            case FRIENDSHIP_REQUEST_RECEIVED:
                return CHANNEL.FRIEND_REQUEST.id;
            case SESSION_BILL_CHANGE:
            case SESSION_MEMBER_ADDED:
                return CHANNEL.ACTIVE_SESSION.id;
            default:
                return CHANNEL.DEFAULT.id;
        }
    }

    public Intent getNotificationIntent(Context context) {
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_DATA, this);
        intent.setComponent(getTargetComponent(context));
        return intent;
    }

    private ComponentName getTargetComponent(Context context) {
        return new ComponentName(context, NotificationActivity.class);
    }

    public NotificationCompat.Builder getNotificationBuilder(Context context, int notificationId) {
        if (!shouldShowNotification())
            return null;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, this.getChannelId());
        builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(this.description)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true);
        addNotificationExtra(context, builder, notificationId);
        return builder;
    }

    private void addNotificationExtra(Context context, NotificationCompat.Builder builder, int notificationId) {
        if (this.type == MESSAGE_TYPE.FRIENDSHIP_REQUEST_RECEIVED) {
            builder.addAction(android.R.drawable.btn_star, "View Profile", MessageUtils.launchUserProfile(
                    context, this.actor.getPk(), notificationId
            ))
            .addAction(
            android.R.drawable.btn_star, "Accept", FriendshipBroadcastReceiver.getRequestAcceptIntent(
                    context, this.object.getPk(), notificationId
                    ));
        }
    }

    public void showNotification(Context context, NotificationManager notificationManager, int notificationId) {
        Intent intent = getNotificationIntent(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = getNotificationBuilder(context, notificationId)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(notificationId, notification);
    }

    public boolean shouldShowNotification() {
        return !TextUtils.isEmpty(description) && !isOnlyUiUpdate();
    }

    public boolean isOnlyUiUpdate() {
        switch (this.type) {
            case SESSION_BILL_CHANGE:
            case SESSION_END:
            case SESSION_HOST_ASSIGNED:
                return false;
            default:
                return true;
        }
    }

    public boolean shouldTryUpdateUi() {
        return true;
    }
}
