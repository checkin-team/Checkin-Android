package com.checkin.app.checkin.Home;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class UserProfileEntity {
    @Id  long id;
    String name,profession,bio,profilepicurl,place;
    int nFollowers,ncheckins;
    double rating;

    public UserProfileEntity() {
    }

    public UserProfileEntity(String name, String profession, String bio, String profilepicurl, String place, int nFollowers, int ncheckins, double rating) {
        this.name = name;
        this.profession = profession;
        this.bio = bio;
        this.profilepicurl = profilepicurl;
        this.place = place;
        this.nFollowers = nFollowers;
        this.ncheckins = ncheckins;
        this.rating = rating;
    }
}
