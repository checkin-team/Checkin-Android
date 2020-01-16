package com.checkin.app.checkin.user.models;

import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserModel implements Serializable {
    @JsonProperty("pk")
    private long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("locality")
    private String address;

    private GENDER gender;

    @JsonProperty("profile_pic")
    private String profilePic;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("phone_no")
    private String phoneNo;

    @JsonProperty("count_checkins")
    private long countCheckins;

    public UserModel() {
    }

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

    public long getCountCheckins() {
        return countCheckins;
    }

    public String getPhoneNumber() {
        return phoneNo;
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
}

