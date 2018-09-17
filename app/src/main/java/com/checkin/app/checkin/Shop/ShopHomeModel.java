package com.checkin.app.checkin.Shop;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by Bhavik Patel on 18/08/2018.
 */

@Entity
public class ShopHomeModel {
    @Id private long id;
    private String name;
    private String bio;
    private boolean active;
    //private List<String> categories;
    @JsonProperty(value = "only_vegetarian") private boolean onlyVegetarian;
    private String address;
    //todo location
    private String city;
    private String phone;
    private String email;
    private String website;
    private boolean verified;
    //private Map<String,List<String>> timings;
    //private List<String> paymentModes;
    private float rating;


    public ShopHomeModel(long id, String name, String bio, boolean active, boolean onlyVegetarian, String address, String city, String phone, String email, String website, boolean verified, float rating) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.active = active;
        this.onlyVegetarian = onlyVegetarian;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.verified = verified;
        this.rating = rating;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isOnlyVegetarian() {
        return onlyVegetarian;
    }

    public void setOnlyVegetarian(boolean onlyVegetarian) {
        this.onlyVegetarian = onlyVegetarian;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
