package com.alcatraz.admin.project_alcatraz.User;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class User {
    @Id private long id;
    private String username;
    private String imageUrl;

    User() {}

    public User(String username, String imageUrl) {
        this.username = username;
        this.imageUrl = imageUrl;
    }

    public User(long id, String username, String imageUrl) {
        this.id = id;
        this.username = username;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }
}

