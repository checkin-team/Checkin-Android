package com.checkin.app.checkin.User;

/**
 * Created by Bhavik Patel on 25/08/2018.
 */

public class MutualCheckInModel {

    private String profileUrl;
    private String actionUrl;
    private String profileName;
    private String actionName;

    public MutualCheckInModel(String profileUrl, String actionUrl, String profileName, String actionName) {
        this.profileUrl = profileUrl;
        this.actionUrl = actionUrl;
        this.profileName = profileName;
        this.actionName = actionName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
