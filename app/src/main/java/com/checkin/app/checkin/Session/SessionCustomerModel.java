package com.checkin.app.checkin.Session;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionCustomerModel {
    @JsonProperty("pk")
    private String pk;

    @JsonProperty("user")
    private BriefModel user;

    @JsonProperty("is_owner")
    private boolean isOwner;

    @JsonProperty("is_payee")
    private boolean isPayee;

    public SessionCustomerModel() {}

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
}
