package com.checkin.app.checkin.session.model;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionCustomerModel {
    @JsonProperty("pk")
    private String pk;

    @JsonProperty("user")
    private BriefModel user;

    @JsonProperty("is_owner")
    private boolean isOwner;

    @JsonProperty("is_public")
    private boolean is_public;

    @JsonProperty("is_payee")
    private boolean isPayee;

    @JsonProperty("is_accepted")
    private boolean isAccepted;

    public SessionCustomerModel() {}

    public SessionCustomerModel(String pk, BriefModel user, boolean isOwner, boolean is_public, boolean isPayee) {
        this.pk = pk;
        this.user = user;
        this.isOwner = isOwner;
        this.is_public = is_public;
        this.isPayee = isPayee;
    }

    public String getPk() {
        return pk;
    }

    public BriefModel getUser() {
        return user;
    }

    public boolean isPayee() {
        return isPayee;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public boolean isIs_public() {
        return is_public;
    }

    public boolean isAccepted() {
        return isAccepted;
    }
}
