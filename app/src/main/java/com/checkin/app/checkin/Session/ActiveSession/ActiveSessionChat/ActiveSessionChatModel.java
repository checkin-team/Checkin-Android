package com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Session.SessionViewOrdersModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by ACER on 1/2/2019.
 */

public class ActiveSessionChatModel {
    @JsonProperty("pk")
    private int pk;

    @JsonProperty("message")
    private String message;

    private CHATEVENTTYPE type;

    @JsonProperty("status")
    private SessionViewOrdersModel.SESSIONEVENT status;

    @JsonProperty("data")
    private ActiveSessionCustomChatDataModel data;

    private CHATSENDERTYPES sender;

    @JsonProperty("user")
    private BriefModel user;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("modified")
    private Date modified;

    public ActiveSessionChatModel(){}

    public enum CHATEVENTTYPE {
        EVENT_NONE(400), EVENT_SESSION_CHECKIN(401), EVENT_SESSION_CHECKOUT(409), EVENT_MEMBER_ADD(411), EVENT_MEMBER_REMOVE(412),
        EVENT_HOST_ASSIGNED(415), EVENT_REQUEST_CHECKOUT(421), EVENT_REQUEST_SERVICE(422), EVENT_MENU_ORDER_ITEM(431), EVENT_CONCERN(432),
        EVENT_COMMAND_MANAGER(441), EVENT_CUSTOM_MESSAGE(450);

        final int tag;

        CHATEVENTTYPE(int tag) {
            this.tag = tag;
        }

        public static CHATEVENTTYPE getByTag(int tag) {
            switch (tag) {
                case 400:
                    return EVENT_NONE;
                case 401:
                    return EVENT_SESSION_CHECKIN;
                case 409:
                    return EVENT_SESSION_CHECKOUT;
                case 411:
                    return EVENT_MEMBER_ADD;
                case 412:
                    return EVENT_MEMBER_REMOVE;
                case 415:
                    return EVENT_HOST_ASSIGNED;
                case 421:
                    return EVENT_REQUEST_CHECKOUT;
                case 422:
                    return EVENT_REQUEST_SERVICE;
                case 431:
                    return EVENT_MENU_ORDER_ITEM;
                case 432:
                    return EVENT_CONCERN;
                case 441:
                    return EVENT_COMMAND_MANAGER;
                case 450:
                    return EVENT_CUSTOM_MESSAGE;
            }
            return EVENT_NONE;
        }
    }

    public ActiveSessionChatModel(int pk, String message, CHATEVENTTYPE type, SessionViewOrdersModel.SESSIONEVENT status, ActiveSessionCustomChatDataModel data, CHATSENDERTYPES sender, BriefModel user, Date created, Date modified) {
        this.pk = pk;
        this.message = message;
        this.type = type;
        this.status = status;
        this.data = data;
        this.sender = sender;
        this.user = user;
        this.created = created;
        this.modified = modified;
    }

    public enum CHATSENDERTYPES {
        SENDER_NONE(0), SENDER_CUSTOMER(1), SENDER_RESTAURANT(2);

        final int tag;

        CHATSENDERTYPES(int tag) {
            this.tag = tag;
        }

        public static CHATSENDERTYPES getByTag(int tag) {
            switch (tag) {
                case 0:
                    return SENDER_NONE;
                case 1:
                    return SENDER_CUSTOMER;
                case 2:
                    return SENDER_RESTAURANT;
            }
            return SENDER_NONE;
        }
    }

    @JsonProperty("type")
    public void setType(int tag) {
        this.type = CHATEVENTTYPE.getByTag(tag);
    }

    @JsonProperty("sender")
    public void setSender(int tag) {
        this.sender = CHATSENDERTYPES.getByTag(tag);
    }

    public int getPk() {
        return pk;
    }

    public CHATEVENTTYPE getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public SessionViewOrdersModel.SESSIONEVENT getStatus() {
        return status;
    }

    public ActiveSessionCustomChatDataModel getData() {
        return data;
    }

    public CHATSENDERTYPES getSender() {
        return sender;
    }

    public BriefModel getUser() {
        return user;
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }
}
