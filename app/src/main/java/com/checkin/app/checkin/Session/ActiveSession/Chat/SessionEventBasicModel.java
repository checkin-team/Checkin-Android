package com.checkin.app.checkin.Session.ActiveSession.Chat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionEventBasicModel {
    @JsonProperty("pk")
    private int pk;

    @JsonProperty("type")
    private SessionChatModel.CHAT_EVENT_TYPE type;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private SessionChatModel.CHAT_STATUS_TYPE status;

    public SessionEventBasicModel() {}

    public int getPk() {
        return pk;
    }

    public SessionChatModel.CHAT_EVENT_TYPE getType() {
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
        this.type = SessionChatModel.CHAT_EVENT_TYPE.getByTag(type);
    }

    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = SessionChatModel.CHAT_STATUS_TYPE.getByTag(status);
    }
}
