package com.checkin.app.checkin.User.Friendship;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class FriendshipRequestModel {
    @JsonProperty("pk") private String pk;
    @JsonProperty("from_user") private long fromUser;
    @JsonProperty("to_user") private long toUser;
    @JsonProperty("message") private String message;
    @JsonProperty("created") private Date created;
    @JsonProperty("viewed") private Date viewed;

    public FriendshipRequestModel() {}

    public String getPk() {
        return pk;
    }

    public long getFromUser() {
        return fromUser;
    }

    public long getToUser() {
        return toUser;
    }

    public String getMessage() {
        return message;
    }
}
