package com.checkin.app.checkin.Notifications;

import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Shop.ShopPrivateProfile.ShopActivity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

//import com.checkin.app.checkin.Profile.ShopProfile.ShopProfileActivity;

/**
 * Created by Bhavik Patel on 20/08/2018.
 */

@Entity
public class NotificationModel {
    @Id private long id;
    private String message;
    private Date time;
    private String profileUrl;
    private String actionUrl;
    private int targetId;
    @JsonIgnore private boolean seen;
    @Convert(converter = Converters.ActionConverter.class, dbType = Integer.class)
    private ACTION action;


    public enum ACTION {
        NULL(0),FOLLOWED_YOU(1),FOLLOW_ACCEPTED (2)     //static action images
        ,ADDED_TO_SESSION(3),RECOMMENDED_YOU(4) ;   //non static action images

        public final int id;

        ACTION(int id) {
            this.id = id;
        }

        public static ACTION getById(int id, ACTION defaultAction) {
            for (ACTION action : ACTION.values())
                if (action.id == id)    return action;
            return defaultAction;
        }
    }



    public NotificationModel(String message, Date time, String profileUrl, String actionUrl, boolean seen, int targetId) {
        this.message = message;
        this.time = time;
        this.profileUrl = profileUrl;
        this.actionUrl = actionUrl;
        this.seen = seen;
        this.targetId = targetId;
    }

    public Class getActionClass(){
//        switch (action){
//            case FOLLOWED_YOU:
//                return ShopProfileActivity.class;
//            case FOLLOW_ACCEPTED:
//                return ShopProfileActivity.class;
//            case ADDED_TO_SESSION:
//                return ShopProfileActivity.class;
//            case RECOMMENDED_YOU:
//                return ShopProfileActivity.class;
//            default:
//                return ShopProfileActivity.class;
//        }
        return ShopActivity.class;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        this.action = ACTION.getById(actionCode, ACTION.NULL);
    }
}
