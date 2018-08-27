package com.checkin.app.checkin.User;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserModel {
    @Id private long id;
    private String username;
    @JsonProperty("profile_pic") private String profilePic;

    public UserModel() {}

    public UserModel(String username, String profilePic) {
        this.username = username;
        this.profilePic = profilePic;
    }

    public UserModel(long id, String username, String profilePic) {
        this.id = id;
        this.username = username;
        this.profilePic = profilePic;
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

