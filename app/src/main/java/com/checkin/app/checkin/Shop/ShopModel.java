package com.checkin.app.checkin.Shop;

import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by Bhavik Patel on 18/08/2018.
 */

@Entity
public class ShopModel {
    @Id(assignable = true) private long id;
    private String name;
    private String bio;
    private boolean active;
    @JsonProperty(value = "only_vegetarian") private boolean onlyVegetarian;
    private String address;
    private String city;
    //todo location
    private String phone;
    private String email;
    private String website;
    private boolean verified;
    private long followers;
    private long checkins;
    private float rating;

    public ShopModel() {}


    public ShopModel(String name, String bio, String city, String phone, float rating, long followers, long checkins) {
        this.name = name;
        this.bio = bio;
        this.active = true;
        this.onlyVegetarian = false;
        this.city = city;
        this.phone = phone;
        this.verified = true;
        this.rating = rating;
        this.followers = followers;
        this.checkins = checkins;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isOnlyVegetarian() {
        return onlyVegetarian;
    }

    public String getAddress() {
        return address;
    }

    public long getFollowers() {
        return followers;
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

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public boolean isVerified() {
        return verified;
    }

    public float getRating() {
        return rating;
    }

    public String getCity() {
        return city;
    }
}
