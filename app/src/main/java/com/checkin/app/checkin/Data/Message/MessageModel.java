package com.checkin.app.checkin.Data.Message;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Message.Constants.CHANNEL;
import com.checkin.app.checkin.Notifications.NotificationActivity;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.User.Friendship.FriendshipBroadcastReceiver;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;

public class MessageModel {
    private MESSAGE_TYPE type;

    @JsonProperty("actor")
    private MessageObjectModel actor;

    @JsonProperty("description")
    private String description;

    @JsonProperty("object")
    private MessageObjectModel object;

    @JsonProperty("target")
    private MessageObjectModel target;

    public enum MESSAGE_TYPE {
        NONE(0),

        // Users
        FRIENDSHIP_REQUEST_SENT(301), FRIENDSHIP_REQUEST_ACCEPTED(302), FRIENDSHIP_REQUEST_REJECTED(303);

        int id;

        MESSAGE_TYPE(int id) {
            this.id = id;
        }

        static MESSAGE_TYPE getById(int id) {
            for (MESSAGE_TYPE type: MESSAGE_TYPE.values()) {
                if (type.id == id)
                    return type;
            }
            return NONE;
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

    @Override
    public String toString() {
        return this.type.name() + " -- " + this.description;
    }

    public String getChannelId() {
        if (this.type.id >= 300 && this.type.id < 304) {
            return CHANNEL.FRIEND_REQUEST.id;
        }
        return CHANNEL.DEFAULT.id;
    }

    public NotificationCompat.Builder getNotificationBuilder(Context context, int notificationId) {
        if (!shouldShowNotification())
            return null;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, this.getChannelId());
        builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(this.description)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true);

        if (this.type == MESSAGE_TYPE.FRIENDSHIP_REQUEST_SENT) {
            builder.addAction(
                    android.R.drawable.btn_star, "View Profile", Utils.launchUserProfile(
                            context, this.actor.getPk(), notificationId
                    ))
                    .addAction(
                    android.R.drawable.btn_star, "Accept", FriendshipBroadcastReceiver.getRequestAcceptIntent(
                            context, this.object.getPk(), notificationId
                    ));
        }

        return builder;
    }

    public boolean shouldShowNotification() {
        return this.type.id > 300;
    }

    public boolean shouldTryUpdateUi() {
        return false;
    }

    public <T extends Class> T getTargetActivity() {
        return (T) NotificationActivity.class;
    }
}
