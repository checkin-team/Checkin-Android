package com.checkin.app.checkin.Session;

import com.checkin.app.checkin.User.UserModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActiveSessionMemberModel extends UserModel {
    private boolean isOwner;

    @JsonProperty("is_owner")
    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }
}
