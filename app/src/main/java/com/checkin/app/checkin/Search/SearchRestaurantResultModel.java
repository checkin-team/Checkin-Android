package com.checkin.app.checkin.Search;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchRestaurantResultModel extends SearchResultModel{

    @JsonProperty("rating")
    private int rating;

    @JsonProperty("category")
    private String category;

    @JsonProperty("cuisine")
    private String cuisine;

    @JsonProperty("location")
    private String location;

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
