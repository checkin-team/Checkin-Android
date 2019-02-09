package com.checkin.app.checkin.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResultModel {
    @JsonProperty("user_id")
    private long userId;

    @JsonProperty("token")
    private String token;

    public AuthResultModel() {}

    public long getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}
