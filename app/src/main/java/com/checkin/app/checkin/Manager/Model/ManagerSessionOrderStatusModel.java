package com.checkin.app.checkin.Manager.Model;

import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ManagerSessionOrderStatusModel {
    @JsonProperty("pk")
    private int pk;

    @JsonProperty("status")
    private int status;

    public ManagerSessionOrderStatusModel() {
    }

    public ManagerSessionOrderStatusModel(int pk, int status) {
        this.pk = pk;
        this.status = status;
    }

    public int getPk() {
        return pk;
    }

    public int getStatus() {
        return status;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
