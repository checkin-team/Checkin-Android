package com.checkin.app.checkin.Waiter.Model;

import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderStatusModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("status")
    private SessionChatModel.CHAT_STATUS_TYPE status;

    public OrderStatusModel() {
    }

    public long getPk() {
        return pk;
    }

    public String getDetail() {
        return detail;
    }

    public SessionChatModel.CHAT_STATUS_TYPE getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = SessionChatModel.CHAT_STATUS_TYPE.getByTag(status);
    }
}
