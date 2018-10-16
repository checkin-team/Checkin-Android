package com.checkin.app.checkin.User.PrivateProfile;

import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.objectbox.annotation.Convert;

public class FriendshipModel {
    @JsonProperty("user_name") private String mUserName;
    @JsonProperty("no_checkins") private long mCheckins;
    @JsonProperty("user_pk") private Long mUserPk;
    @JsonProperty("user_pic") private String mUserPic;
    @JsonProperty("friend_status") private String mUserStatus;
    private FRIEND_STATUS status;

    public enum FRIEND_STATUS {
        NONE("none"), BLOCKED("blkd"), REQUESTED("rqst"), FRIEND("frnd");

        final String tag;
        FRIEND_STATUS(String tag){this.tag = tag;}
        public static FRIEND_STATUS getByTag(String tag) {
            switch (tag) {
                case "none":
                    return NONE;
                case "blkd":
                    return BLOCKED;
                case "rqst":
                    return REQUESTED;
                case "frnd":
                    return FRIEND;
            }
            return FRIEND_STATUS.NONE;
        }
    }
    public FriendshipModel() {} //default constructor

    public String getUserName() {
        return mUserName;
    }

    public long getCheckins() {
        return mCheckins;
    }

    public String formatCheckins() {
        return Util.formatCount(mCheckins);
    }

    public Long getUserPk(){return mUserPk; }

    public String getUserPic(){return mUserPic; }

    public void setUserStatus(String tag){
        this.status = FRIEND_STATUS.getByTag(tag);
    }
}