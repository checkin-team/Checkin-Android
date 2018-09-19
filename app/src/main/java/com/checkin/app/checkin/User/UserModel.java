package com.checkin.app.checkin.User;

import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserModel {
    @Id private long id;
    private String username;
    @Convert(converter = Converters.GenderConverter.class, dbType = Character.class)
    private GENDER gender;
    private String bio;
    @JsonProperty("profile_pic") private String profilePic;
    private String location;
    private long followers;
    private long checkins;

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

    public UserModel(String username, String profilePic, String location, long followers, long checkins) {
        this.username = username;
        this.profilePic = profilePic;
        this.location = location;
        this.followers = followers;
        this.checkins = checkins;
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

    public void setBio(String bio) {
        this.bio = bio;
    }

    public long getFollowers() {
        return followers;
    }

    public String getLocation() {
        return location;
    }

    public long getCheckins() {
        return checkins;
    }

    public String formatFollowers() {
        return Util.formatCount(followers);
    }

    public String formatCheckins() {
        return Util.formatCount(checkins);
    }

    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePic() {
        return profilePic;
    }
}

