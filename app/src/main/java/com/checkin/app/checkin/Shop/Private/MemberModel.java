package com.checkin.app.checkin.Shop.Private;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberModel {
    public static final char ROLE_OWNER = 'o';
    public static final char ROLE_ADMIN = 'a';
    public static final char ROLE_MANAGER = 'm';
    public static final char ROLE_WAITER = 'w';
    public static final char ROLE_COOK = 'c';

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

    public MemberModel() {
    }

    public MemberModel(long userPk, String userName, String userPic) {
        user = new BriefModel(userPk, userName, userPic);
    }

    public BriefModel getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(BriefModel user) {
        this.user = user;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public boolean isCook() {
        return isCook;
    }

    public void setCook(boolean cook) {
        isCook = cook;
    }

    public boolean isWaiter() {
        return isWaiter;
    }

    public void setWaiter(boolean waiter) {
        isWaiter = waiter;
    }

    @JsonProperty("user")
    public long getUserId() {
        return Long.valueOf(user.getPk());
    }

    public void assignRoles(CharSequence[] roles) {
        resetRoles();
        for (CharSequence charSequence : roles) {
            char role = charSequence.charAt(0);
            switch (role) {
                case ROLE_OWNER:
                    isOwner = true;
                    break;
                case ROLE_ADMIN:
                    isAdmin = true;
                    break;
                case ROLE_MANAGER:
                    isManager = true;
                    break;
                case ROLE_WAITER:
                    isWaiter = true;
                    break;
                case ROLE_COOK:
                    isCook = true;
                    break;
            }
        }
    }

    private void resetRoles() {
        isCook = false;
        isWaiter = false;
        isManager = false;
        isAdmin = false;
        isOwner = false;
    }

    public Object[] getRoles() {
        List<Object> selectedRoles = new ArrayList<>();
        if (this.isOwner())
            selectedRoles.add(ROLE_OWNER);
        if (this.isAdmin())
            selectedRoles.add(ROLE_ADMIN);
        if (this.isManager())
            selectedRoles.add(ROLE_MANAGER);
        if (this.isWaiter())
            selectedRoles.add(ROLE_WAITER);
        if (this.isCook())
            selectedRoles.add(ROLE_COOK);
        return selectedRoles.toArray();
    }
}
