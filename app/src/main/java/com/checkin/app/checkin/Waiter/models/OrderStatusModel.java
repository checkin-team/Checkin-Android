package com.checkin.app.checkin.Waiter.models;

import com.checkin.app.checkin.session.activesession.chat.SessionChatModel;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OrderStatusModel {
    @JsonProperty("pk")
    private long pk;
    private SessionChatModel.CHAT_STATUS_TYPE status;
    private String detail;
    private boolean isAvailable;

    public OrderStatusModel() {
    }

    public OrderStatusModel(long pk, SessionChatModel.CHAT_STATUS_TYPE status) {
        this.pk = pk;
        this.status = status;
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    @JsonProperty("status")
    public int getStatus() {
        return status.tag;
    }

    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = SessionChatModel.CHAT_STATUS_TYPE.getByTag(status);
    }

    public String getDetail() {
        return detail;
    }

    @JsonProperty("detail")
    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    @JsonProperty("is_available")
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
