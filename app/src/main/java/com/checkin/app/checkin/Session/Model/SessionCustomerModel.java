package com.checkin.app.checkin.Session.Model;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionCustomerModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("user")
    private BriefModel user;

    @JsonProperty("is_owner")
    private boolean isOwner;

    @JsonProperty("isPublic")
    private boolean isPublic;

    @JsonProperty("is_payee")
    private boolean isPayee;

    @JsonProperty("is_accepted")
    private boolean isAccepted;

    public SessionCustomerModel() {}

    public SessionCustomerModel(long pk, BriefModel user, boolean isOwner, boolean isAccepted) {
        this.pk = pk;
        this.user = user;
        this.isOwner = isOwner;
        this.isAccepted = isAccepted;
    }

    public long getPk() {
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

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}
