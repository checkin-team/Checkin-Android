package com.checkin.app.checkin.Session.ActiveSession.Chat;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionChatDataModel {
    // CHAT_EVENT_TYPE.EVENT_MEMBER_ADD
    @JsonProperty("user")
    private int user;

    // CHAT_EVENT_TYPE.EVENT_MENU_ORDER_ITEM
    @JsonProperty("ordered_item")
    private int orderedItem;

    // CHAT_EVENT_TYPE.EVENT_REQUEST_SERVICE
    @Nullable
    @JsonProperty("service")
    private EVENT_REQUEST_SERVICE_TYPE service;

    // CHAT_EVENT_TYPE.EVENT_MENU_ORDER_ITEM
    @Nullable
    @JsonProperty("canceler")
    private SessionChatCancelerModel canceler;

    // CHAT_EVENT_TYPE.EVENT_CONCERN
    @Nullable
    @JsonProperty("concern")
    private EVENT_CONCERN_TYPE concern;

    // CHAT_EVENT_TYPE.EVENT_CONCERN
    @Nullable
    @JsonProperty("event")
    private SessionEventBasicModel event;

    public enum EVENT_REQUEST_SERVICE_TYPE {
        SERVICE_NONE(0), SERVICE_CALL_WAITER(1), SERVICE_CLEAN_TABLE(2), SERVICE_BRING_COMMODITY(3);

        public final int tag;

        EVENT_REQUEST_SERVICE_TYPE(int tag) {
            this.tag = tag;
        }

        public static EVENT_REQUEST_SERVICE_TYPE getByTag(int tag) {
            for (EVENT_REQUEST_SERVICE_TYPE type : EVENT_REQUEST_SERVICE_TYPE.values())
                if (type.tag == tag)
                    return type;
            return SERVICE_NONE;
        }
    }

    public enum EVENT_CONCERN_TYPE {
        CONCERN_NONE(0), CONCERN_DELAY(1), CONCERN_QUALITY(2), CONCERN_REMARK(3);

        final int tag;

        EVENT_CONCERN_TYPE(int tag) {
            this.tag = tag;
        }

        public static EVENT_CONCERN_TYPE getByTag(int tag) {
            for (EVENT_CONCERN_TYPE type : EVENT_CONCERN_TYPE.values())
                if (type.tag == tag)
                    return type;
            return CONCERN_NONE;
        }
    }

    public SessionChatDataModel() {
    }

    public int getUser() {
        return user;
    }

    public int getOrderedItem() {
        return orderedItem;
    }

    @Nullable
    public SessionChatCancelerModel getCanceler() {
        return canceler;
    }

    @JsonProperty("service")
    public void setType(int tag) {
        this.service = EVENT_REQUEST_SERVICE_TYPE.getByTag(tag);
    }

    @Nullable
    public EVENT_REQUEST_SERVICE_TYPE getService() {
        return service;
    }

    @Nullable
    public EVENT_CONCERN_TYPE getConcern() {
        return concern;
    }

    @Nullable
    public SessionEventBasicModel getEvent() {
        return event;
    }

    public void setConcern(int concern) {
        this.concern = EVENT_CONCERN_TYPE.getByTag(concern);
    }
}
