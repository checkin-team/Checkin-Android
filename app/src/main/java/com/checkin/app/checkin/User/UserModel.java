package com.checkin.app.checkin.User;

import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Unique;

@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserModel {
    @JsonProperty("pk")
    @Id(assignable = true)
    private long id;

    private String username;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("address")
    private String address;

    @Convert(converter = Converters.GenderConverter.class, dbType = Character.class)
    private GENDER gender;

    @JsonProperty("profile_pic")
    private String profilePic;

    private String bio;

    private long followers;
    private long checkins;
    private long reviews;
    @JsonProperty("is_public") private boolean isPublic;

    public enum GENDER {
        MALE('m'), FEMALE('f');

        final char tag;
        GENDER(char tag) {
            this.tag = tag;
        }

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

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}

