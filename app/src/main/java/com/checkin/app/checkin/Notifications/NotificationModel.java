package com.checkin.app.checkin.Notifications;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by Bhavik Patel on 20/08/2018.
 */

public class NotificationModel {

    private String message;
    private Date time;
    private String profileUrl;
    private String actionUrl;
    private int targetId;
    private boolean seen;
    private ACTION action;


    private enum ACTION {
        NULL,FOLLOWED_YOU,FOLLOW_ACCEPTED      //static action images
        ,ADDED_TO_SESSION,RECOMMENDED_YOU    //non static action images
    }

    public NotificationModel(String message, Date time, String profileUrl, String actionUrl, boolean seen, int targetId) {
        this.message = message;
        this.time = time;
        this.profileUrl = profileUrl;
        this.actionUrl = actionUrl;
        this.seen = seen;
        this.targetId = targetId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public ACTION getAction() {
        return action;
    }

    @JsonProperty("action")
    public void setAction(int actionCode) {
        switch (actionCode){
            case 0: action = ACTION.NULL;break;
        }
    }
}
