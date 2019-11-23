package com.checkin.app.checkin.session.activesession.chat;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.session.activesession.chat.SessionChatDataModel.EVENT_CONCERN_TYPE;
import com.checkin.app.checkin.session.activesession.chat.SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_EVENT_TYPE;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionEventBasicModel {
    @JsonProperty("pk")
    private int pk;

    @JsonProperty("type")
    private CHAT_EVENT_TYPE type;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private SessionChatModel.CHAT_STATUS_TYPE status;

    public SessionEventBasicModel() {
    }

    @DrawableRes
    public static int getEventIcon(CHAT_EVENT_TYPE type, @Nullable EVENT_REQUEST_SERVICE_TYPE service, @Nullable EVENT_CONCERN_TYPE concern) {
        switch (type) {
            case EVENT_SESSION_CHECKIN:
            case EVENT_SESSION_CHECKOUT:
            case EVENT_MEMBER_ADD:
            case EVENT_MEMBER_REMOVE:
                return R.drawable.ic_session_event_checkin;
            case EVENT_CUSTOM_MESSAGE:
                return R.drawable.ic_session_event_custom;
            case EVENT_REQUEST_CHECKOUT:
                return R.drawable.ic_session_event_request_checkout;
            case EVENT_CONCERN:
                if (concern != null) {
                    return (concern == EVENT_CONCERN_TYPE.CONCERN_DELAY ? R.drawable.ic_session_event_concern_delay : R.drawable.ic_session_event_concern_quality);
                }
            case EVENT_REQUEST_SERVICE:
                if (service != null) {
                    switch (service) {
                        case SERVICE_BRING_COMMODITY:
                            return R.drawable.ic_session_event_request_item;
                        case SERVICE_CLEAN_TABLE:
                            return R.drawable.ic_session_event_request_clean_table;
                        case SERVICE_CALL_WAITER:
                            return R.drawable.ic_session_event_request_waiter;
                    }
                }
            case EVENT_MENU_ORDER_ITEM:
                return R.drawable.ic_session_event_order_new;
            default:
                return R.drawable.ic_session_event_custom;
        }
    }

    public int getPk() {
        return pk;
    }

    public CHAT_EVENT_TYPE getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(int type) {
        this.type = CHAT_EVENT_TYPE.getByTag(type);
    }

    public String getMessage() {
        return message;
    }

    public SessionChatModel.CHAT_STATUS_TYPE getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = SessionChatModel.CHAT_STATUS_TYPE.getByTag(status);
    }
}
