package com.checkin.app.checkin.session.activesession.chat;

import androidx.annotation.Nullable;

import com.checkin.app.checkin.misc.models.BriefModel;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ACER on 1/2/2019.
 */

public class SessionChatModel implements Serializable {
    @JsonProperty("pk")
    private int pk;

    @JsonProperty("message")
    private String message;

    @JsonProperty("type")
    private CHAT_EVENT_TYPE type;

    @JsonProperty("status")
    private CHAT_STATUS_TYPE status;

    @JsonProperty("data")
    private SessionChatDataModel data;

    @JsonProperty("sender")
    private CHAT_SENDER_TYPE sender;

    @Nullable
    @JsonProperty("user")
    private BriefModel user;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("modified")
    private Date modified;

    public SessionChatModel() {
    }

    @JsonProperty("sender")
    public void setSender(int tag) {
        this.sender = CHAT_SENDER_TYPE.getByTag(tag);
    }

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public CHAT_EVENT_TYPE getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(int tag) {
        this.type = CHAT_EVENT_TYPE.getByTag(tag);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CHAT_STATUS_TYPE getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = CHAT_STATUS_TYPE.getByTag(status);
    }

    public SessionChatDataModel getData() {
        return data;
    }

    public void setData(SessionChatDataModel data) {
        this.data = data;
    }

    public CHAT_SENDER_TYPE getSender() {
        return sender;
    }

    public void setSender(CHAT_SENDER_TYPE sender) {
        this.sender = sender;
    }

    @Nullable
    public BriefModel getUser() {
        return user;
    }

    public void setUser(@Nullable BriefModel user) {
        this.user = user;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public boolean allowConcernRaise() {
        return (this.type == CHAT_EVENT_TYPE.EVENT_MENU_ORDER_ITEM || this.type == CHAT_EVENT_TYPE.EVENT_REQUEST_SERVICE)
                && this.status != CHAT_STATUS_TYPE.CANCELLED;
    }

    public String formatEventTime() {
        return Utils.formatDateTo24HoursTime(modified);
    }

    public boolean isOfAnyType(CHAT_EVENT_TYPE... types) {
        for (CHAT_EVENT_TYPE type : types) {
            if (type == this.type)
                return true;
        }
        return false;
    }

    public enum CHAT_EVENT_TYPE {
        EVENT_NONE(400), EVENT_SESSION_CHECKIN(401), EVENT_SESSION_CHECKOUT(409), EVENT_MEMBER_ADD(411), EVENT_MEMBER_REMOVE(412),
        EVENT_HOST_ASSIGNED(415), EVENT_REQUEST_CHECKOUT(421), EVENT_REQUEST_SERVICE(422), EVENT_MENU_ORDER_ITEM(431), EVENT_CONCERN(432),
        EVENT_COMMAND_MANAGER(441), EVENT_CUSTOM_MESSAGE(450);

        final int tag;

        CHAT_EVENT_TYPE(int tag) {
            this.tag = tag;
        }

        public static CHAT_EVENT_TYPE getByTag(int tag) {
            for (CHAT_EVENT_TYPE type : CHAT_EVENT_TYPE.values()) {
                if (type.tag == tag)
                    return type;
            }
            return EVENT_NONE;
        }
    }

    public enum CHAT_SENDER_TYPE {
        SENDER_NONE(0), SENDER_CUSTOMER(1), SENDER_RESTAURANT(2);

        final int tag;

        CHAT_SENDER_TYPE(int tag) {
            this.tag = tag;
        }

        public static CHAT_SENDER_TYPE getByTag(int tag) {
            for (CHAT_SENDER_TYPE type : CHAT_SENDER_TYPE.values())
                if (type.tag == tag)
                    return type;
            return SENDER_NONE;
        }
    }

    public enum CHAT_STATUS_TYPE {
        NONE(0), OPEN(1), IN_PROGRESS(5), COOKED(6), CANCELLED(9), DONE(10);

        public final int tag;

        CHAT_STATUS_TYPE(int tag) {
            this.tag = tag;
        }//constructor of enum


        public static CHAT_STATUS_TYPE getByTag(int id) {
            for (CHAT_STATUS_TYPE type : CHAT_STATUS_TYPE.values()) {
                if (type.tag == id)
                    return type;
            }
            return NONE;
        }
    }
}
