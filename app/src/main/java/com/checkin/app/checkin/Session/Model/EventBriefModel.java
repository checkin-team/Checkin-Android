package com.checkin.app.checkin.Session.Model;

import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventBriefModel {

    @JsonProperty("pk")
    private Integer pk;

    @JsonProperty("type")
    private  SessionChatModel.CHAT_EVENT_TYPE type;

    @JsonProperty("message")
    private String message;

    @JsonProperty("service")
    private String service;

    @JsonProperty("concern")
    private String concern;

    @JsonProperty("timestamp")
    private Date timestamp;

    public EventBriefModel() {}

    public Integer getPk() {
        return pk;
    }

    public String getConcern() {
        return concern;
    }

    public String getService() {
        return service;
    }

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
