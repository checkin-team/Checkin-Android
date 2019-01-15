package com.checkin.app.checkin.Session;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SelfPresenceModel {

    @JsonProperty("user")
    private BriefModel user;

    @JsonProperty("is_owner")
    private boolean isOwner;

    @JsonProperty("is_public")
    private boolean is_public;

    public SelfPresenceModel(){}

    public SelfPresenceModel(String pk, BriefModel user, boolean isOwner, boolean is_public) {
        this.user = user;
        this.isOwner = isOwner;
        this.is_public = is_public;
    }

    public BriefModel getUser() {
        return user;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public boolean isIs_public() {
        return is_public;
    }
}
