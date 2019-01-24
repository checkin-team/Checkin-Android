package com.checkin.app.checkin.Session.Model;

import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class EventBriefModel {
    @JsonProperty("type")
    private  SessionChatModel.CHAT_EVENT_TYPE type;

    @JsonProperty("message")
    private String message;

    @JsonProperty("timestamp")
    private Date timestamp;

    public EventBriefModel() {}

    public SessionChatModel.CHAT_EVENT_TYPE getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @JsonProperty("type")
    public void setType(int event) {
        this.type = SessionChatModel.CHAT_EVENT_TYPE.getByTag(event);
    }

    public String formatTimestamp() {
        return Utils.formatDateTo24HoursTime(timestamp);
    }
}
