package com.checkin.app.checkin.User;

import android.support.annotation.Nullable;

import com.checkin.app.checkin.User.Friendship.FriendshipModel.FRIEND_STATUS;
import com.checkin.app.checkin.User.Friendship.FriendshipRequestModel;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

//@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserModel {
    @JsonProperty("pk")
//    @Id(assignable = true)
    private long id;

    @JsonProperty("username") private String username;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

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

    @JsonProperty("count_followers") private long countFollowers;
    @JsonProperty("count_checkins") private long countCheckins;
    @JsonProperty("count_reviews") private long countReviews;

    @Nullable
    @JsonProperty("friendship_request")
    private FriendshipRequestModel friendshipRequest;

    private FRIEND_STATUS friendStatus;

    public enum GENDER {
        MALE('m'), FEMALE('f');

        public final char tag;
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

    public UserModel(GENDER gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    public long getCountFollowers() {
        return countFollowers;
    }

    public long getCountCheckins() {
        return countCheckins;
    }

    public long getCountReviews() {
        return countReviews;
    }

    public String getPhoneNumber() {
        return phoneNo;
    }

    public FRIEND_STATUS getFriendStatus() {
        return friendStatus;
    }

    public String formatReviews() {
        return Utils.formatCount(countReviews);
    }

    public String formatFollowers() {
        return Utils.formatCount(countFollowers);
    }

    public String formatCheckins() {
        return Utils.formatCount(countCheckins);
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

    @Nullable
    public FriendshipRequestModel getFriendshipRequest() {
        return friendshipRequest;
    }

    @JsonProperty("friend_status")
    public void setFriendStatus(String friendStatusTag) {
        this.friendStatus = FRIEND_STATUS.getByTag(friendStatusTag);
    }
}

