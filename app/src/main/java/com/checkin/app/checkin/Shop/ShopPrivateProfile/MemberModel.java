package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MemberModel {
    @JsonProperty("user")
    private BriefModel user;

    @JsonProperty("is_owner")
    private boolean isOwner;

    @JsonProperty("is_admin")
    private boolean isAdmin;

    @JsonProperty("is_manager")
    private boolean isManager;

    @JsonProperty("is_cook")
    private boolean isCook;

    @JsonProperty("is_waiter")
    private boolean isWaiter;

    public MemberModel() {}

    public BriefModel getUser() {
        return user;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isManager() {
        return isManager;
    }

    public boolean isCook() {
        return isCook;
    }

    public boolean isWaiter() {
        return isWaiter;
    }

    public void setWaiter(boolean waiter) {
        isWaiter = waiter;
    }

    public void setCook(boolean cook) {
        isCook = cook;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public void setUser(BriefModel user) {
        this.user = user;
    }
}
