package com.checkin.app.checkin.Search;

import com.checkin.app.checkin.User.Friendship.FriendshipModel.FRIEND_STATUS;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

public class SearchResultPeopleModel extends SearchResultModel {
    @JsonProperty("checkins")
    private long checkins;
    private FRIEND_STATUS friendshipStatus;

    public FRIEND_STATUS getFriendshipStatus() {
        return friendshipStatus;
    }

    @JsonProperty("friendship_status")
    public void setFriendshipStatus(String status) {
        this.friendshipStatus = FRIEND_STATUS.getByTag(status);
    }

    public long getCheckins() {
        return checkins;
    }

    public String formatCheckins() {
        return Utils.formatCount(checkins);
    }

    public String formatExtra() {
        if (friendshipStatus == FRIEND_STATUS.FRIENDS)
            return String.format(Locale.ENGLISH, "%s Checkins | Following", formatCheckins());
        else
            return String.format(Locale.ENGLISH, "%s Checkins", formatCheckins());
    }

    public boolean isNotFollowed() {
        return friendshipStatus == FRIEND_STATUS.NONE;
    }

    public boolean isRequested() {
        return friendshipStatus == FRIEND_STATUS.PENDING_REQUEST;
    }
}
