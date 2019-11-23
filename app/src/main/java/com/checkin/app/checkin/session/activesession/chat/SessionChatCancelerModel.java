package com.checkin.app.checkin.session.activesession.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionChatCancelerModel {
    @JsonProperty("pk")
    private int pk;

    @JsonProperty("sender")
    private int sender;

    public SessionChatCancelerModel() {
    }

    public int getPk() {
        return pk;
    }

    public int getSender() {
        return sender;
    }
}
