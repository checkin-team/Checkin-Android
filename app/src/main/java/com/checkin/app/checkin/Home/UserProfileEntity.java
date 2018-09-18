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

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setProfilepicurl(String profilepicurl) {
        this.profilepicurl = profilepicurl;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setnFollowers(int nFollowers) {
        this.nFollowers = nFollowers;
    }

    public void setNcheckins(int ncheckins) {
        this.ncheckins = ncheckins;
    }

    public void setRating(double rating) {
        this.rating = rating;
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

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfession() {
        return profession;
    }

    public String getBio() {
        return bio;
    }

    public String getProfilepicurl() {
        return profilepicurl;
    }

    public String getPlace() {
        return place;
    }

    public int getnFollowers() {
        return nFollowers;
    }

    public int getNcheckins() {
        return ncheckins;
    }

    public double getRating() {
        return rating;
    }
}
