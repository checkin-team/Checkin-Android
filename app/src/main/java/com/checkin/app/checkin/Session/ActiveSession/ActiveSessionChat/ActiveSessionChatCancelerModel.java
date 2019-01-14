package com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActiveSessionChatCancelerModel {

    @JsonProperty("pk")
    private int pk;

    @JsonProperty("sender")
    private int sender;

    public ActiveSessionChatCancelerModel(){}

    public int getPk() {
        return pk;
    }

    public int getSender() {
        return sender;
    }
}
