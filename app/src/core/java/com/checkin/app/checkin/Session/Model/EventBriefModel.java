package com.checkin.app.checkin.Session.Model;

import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel.EVENT_CONCERN_TYPE;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_EVENT_TYPE;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class EventBriefModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("type")
    private CHAT_EVENT_TYPE type;

    @JsonProperty("message")
    private String message;

    @JsonProperty("timestamp")
    private Date timestamp;

    @JsonProperty("service")
    private EVENT_REQUEST_SERVICE_TYPE service;

    @JsonProperty("concern")
    private EVENT_CONCERN_TYPE concern;

    public EventBriefModel() {}

    public EventBriefModel(long pk, CHAT_EVENT_TYPE type, String message) {
        this.pk = pk;
        this.type = type;
        this.message = message;
        this.timestamp = new Date();
    }

    public CHAT_EVENT_TYPE getType() {
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
        this.type = CHAT_EVENT_TYPE.getByTag(event);
    }

    public String formatTimestamp() {
        return Utils.formatDateTo24HoursTime(timestamp);
    }

    public String formatElapsedTime() {
        return Utils.formatElapsedTime(timestamp);
    }

    public EVENT_REQUEST_SERVICE_TYPE getService() {
        return service;
    }

    @JsonProperty("service")
    public void setService(int service) {
        this.service = EVENT_REQUEST_SERVICE_TYPE.getByTag(service);
    }

    public EVENT_CONCERN_TYPE getConcern() {
        return concern;
    }

    @JsonProperty("concern")
    public void setConcern(int concern) {
        this.concern = EVENT_CONCERN_TYPE.getByTag(concern);
    }

    public long getPk() {
        return pk;
    }
}