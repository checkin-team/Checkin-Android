package com.checkin.app.checkin.Home.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrendingRestaurantsModel {

    @JsonProperty("pk")
    private long pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("address")
    private String address;

    @JsonProperty("seat_availability")
    private int seatAvailability;

    @JsonProperty("rating")
    private float rating;

    @JsonProperty("off")
    private int off;

    public TrendingRestaurantsModel() {
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSeatAvailability() {
        return seatAvailability;
    }

    public void setSeatAvailability(int seatAvailability) {
        this.seatAvailability = seatAvailability;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getOff() {
        return off;
    }

    public void setOff(int off) {
        this.off = off;
    }
}
