package com.checkin.app.checkin.session.models;


import androidx.annotation.NonNull;

import com.checkin.app.checkin.manager.models.ManagerSessionEventModel;
import com.checkin.app.checkin.session.activesession.chat.SessionChatDataModel.EVENT_CONCERN_TYPE;
import com.checkin.app.checkin.session.activesession.chat.SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_EVENT_TYPE;
import com.checkin.app.checkin.utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Calendar;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public EventBriefModel() {
    }

    public EventBriefModel(long pk, CHAT_EVENT_TYPE type, String message) {
        this.pk = pk;
        this.type = type;
        this.message = message;
        this.timestamp = Calendar.getInstance().getTime();
    }

    @NonNull
    public static EventBriefModel getFromManagerEventModel(ManagerSessionEventModel eventModel) {
        EventBriefModel result = new EventBriefModel(eventModel.getPk(), eventModel.getType(), eventModel.getMessage());
        if (eventModel.getModified() != null)
            result.timestamp = eventModel.getModified();
        result.service = eventModel.getService();
        result.concern = eventModel.getConcern();
        return result;
    }

    public CHAT_EVENT_TYPE getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(int event) {
        this.type = CHAT_EVENT_TYPE.getByTag(event);
    }

    public void setType(CHAT_EVENT_TYPE type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
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