package com.checkin.app.checkin.Session.ActiveSession.Chat;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel.EVENT_CONCERN_TYPE;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_EVENT_TYPE;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionEventBasicModel {
    @JsonProperty("pk")
    private int pk;

    @JsonProperty("type")
    private CHAT_EVENT_TYPE type;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private SessionChatModel.CHAT_STATUS_TYPE status;

    public SessionEventBasicModel() {}

    public int getPk() {
        return pk;
    }

    public CHAT_EVENT_TYPE getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public SessionChatModel.CHAT_STATUS_TYPE getStatus() {
        return status;
    }

    @JsonProperty("type")
    public void setType(int type) {
        this.type = CHAT_EVENT_TYPE.getByTag(type);
    }

    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = SessionChatModel.CHAT_STATUS_TYPE.getByTag(status);
    }

    @DrawableRes
    public static int getEventIcon(CHAT_EVENT_TYPE type, @Nullable EVENT_REQUEST_SERVICE_TYPE service, @Nullable EVENT_CONCERN_TYPE concern) {
        switch (type) {
            case EVENT_SESSION_CHECKIN:
                return R.drawable.ic_session_event_checkin;
            case EVENT_CUSTOM_MESSAGE:
                return R.drawable.ic_session_event_custom;
            case EVENT_REQUEST_CHECKOUT:
                return R.drawable.ic_session_event_request_checkout;
            case EVENT_CONCERN:
                if (concern != null) {
                    return (concern == EVENT_CONCERN_TYPE.CONCERN_DELAY ? R.drawable.ic_session_event_concern_delay : R.drawable.ic_session_event_concern_delay);
                }
            case EVENT_REQUEST_SERVICE:
                if (service != null) {
                    if (service == EVENT_REQUEST_SERVICE_TYPE.SERVICE_CALL_WAITER)
                        return R.drawable.ic_session_event_request_waiter;
                    else if (service == EVENT_REQUEST_SERVICE_TYPE.SERVICE_BRING_COMMODITY)
                        return R.drawable.ic_session_event_request_item;
                }
            default:
                // TODO: Remove test icons.
                return R.drawable.ic_session_event_checkin;
        }
    }
}
