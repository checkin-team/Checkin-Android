package com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActiveSessionCustomChatDataModel {
    @JsonProperty("user")
    private int user;

    @JsonProperty("ordered_item")
    private int ordered_item;

    @JsonProperty("service")
    private int service;

    @JsonProperty("canceler")
    private ActiveSessionChatCancelerModel canceler;

    public ActiveSessionCustomChatDataModel(){}

    public int getUser() {
        return user;
    }

    public int getOrdered_item() {
        return ordered_item;
    }

    public ActiveSessionChatCancelerModel getCanceler() {
        return canceler;
    }
}
