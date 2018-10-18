package com.checkin.app.checkin.User;

import android.support.annotation.Nullable;

import com.checkin.app.checkin.User.Friendship.FriendshipModel.FRIEND_STATUS;
import com.checkin.app.checkin.User.Friendship.FriendshipRequestModel;
import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

//@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserModel {
    @JsonProperty("pk")
//    @Id(assignable = true)
    private long id;

    @JsonProperty("username") private String username;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("locality")
    private String address;

//    @Convert(converter = Converters.GenderConverter.class, dbType = Character.class)
    private GENDER gender;

    @JsonProperty("profile_pic")
    private String profilePic;

    @JsonProperty("bio") private String bio;

    @JsonProperty("phone_no")
    private String phoneNo;

    @JsonProperty("friend_count") private long followers;
    private long checkins;
    @JsonProperty("no_reviews") private long reviews;
    @JsonProperty("is_public") private boolean isPublic;

    @Nullable
    @JsonProperty("friendship_request")
    private FriendshipRequestModel friendshipRequest;

    private FRIEND_STATUS friendStatus;

    public enum GENDER {
        MALE('m'), FEMALE('f');

        final char tag;
        GENDER(char tag) {
            this.tag = tag;
        }//constructor of enum

        public static GENDER getByTag(char tag) {
            switch (tag) {
                case 'm':
                    return MALE;
                case 'f':
                    return FEMALE;
            }
            return GENDER.MALE;
        }
    }

    // TODO: Move from here.


    public UserModel() {}

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public GENDER getGender() {
        return gender;
    }

    @JsonProperty("gender")
    public void setGender(char tag) {
        this.gender = GENDER.getByTag(tag);
    }

    public String getBio() {
        return bio;
    }

    public long getFollowers() {
        return followers;
    }

    public long getCheckins() {
        return checkins;
    }

    public long getReviews() {
        return reviews;
    }

    public String getPhoneNumber() {
        return phoneNo;
    }

    public FRIEND_STATUS getFriendStatus() {
        return friendStatus;
    }

    public String formatReviews() {
        return Util.formatCount(reviews);
    }

    public String formatFollowers() {
        return Util.formatCount(followers);
    }

    public String formatCheckins() {
        return Util.formatCount(checkins);
    }

    public String getAddress() {
        return address;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public boolean isPublic() {
        return isPublic;
    }

    @Nullable
    public FriendshipRequestModel getFriendshipRequest() {
        return friendshipRequest;
    }

    @JsonProperty("friend_status")
    public void setFriendStatus(String friendStatusTag) {
        this.friendStatus = FRIEND_STATUS.getByTag(friendStatusTag);
    }
}

