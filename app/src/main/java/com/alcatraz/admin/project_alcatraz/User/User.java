package com.alcatraz.admin.project_alcatraz.User;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.alcatraz.admin.project_alcatraz.Utility.Constants;
import com.alcatraz.admin.project_alcatraz.Utility.NamedFormatter;

import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "users")
public class User {
    @PrimaryKey()
    private int id;
    private String username;
    @Ignore
    private String mImageUrl;

    public User(String username) {
        this.username = username;
    }

    @Ignore
    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    @Ignore
    public User(String name, String imageUrl) {
        username = name;
        mImageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getProfileUrl() {
        Map<String, Integer> values = new HashMap<>();
        values.put(Constants.USER_ID, id);
        return NamedFormatter.format(Constants.API_URL_USER_PROFILE_URL, values);
    }
}

