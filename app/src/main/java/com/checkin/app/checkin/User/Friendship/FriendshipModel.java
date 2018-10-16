package com.checkin.app.checkin.User.Friendship;

import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FriendshipModel {
    @JsonProperty("user_name") private String mUserName;
    @JsonProperty("no_checkins") private long mCheckins;
    @JsonProperty("user_pk") private Long mUserPk;
    @JsonProperty("user_pic") private String mUserPic;
    private FRIEND_STATUS mStatus;

    public enum FRIEND_STATUS {
        NONE("none"), FRIENDS("frnd"), PENDING_REQUEST("rqst"), BLOCKED("blkd");

        private String tag;
        FRIEND_STATUS(String tag) {
            this.tag = tag;
        }

        public static FRIEND_STATUS getByTag(String tag) {
            for (FRIEND_STATUS status: FRIEND_STATUS.values()) {
                if (status.tag.equals(tag)) {
                    return status;
                }
            }
            return NONE;
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

    public FRIEND_STATUS getStatus() {
        return mStatus;
    }

    @JsonProperty("friend_status")
    public void setUserStatus(String tag){
        this.mStatus = FRIEND_STATUS.getByTag(tag);
    }
}