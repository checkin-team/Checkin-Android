package com.checkin.app.checkin.Search;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchPeopleResultModel extends SearchResultModel {

    private FRIEND_STATUS mStatus;

    public enum FRIEND_STATUS {
        NONE("none"), FRIENDS("frnd"), PENDING_REQUEST("rqst");

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

    public FRIEND_STATUS getmStatus() {
        return mStatus;
    }

    @JsonProperty("friend_status")
    public void setUserStatus(String tag){
        this.mStatus = FRIEND_STATUS.getByTag(tag);
    }
}
