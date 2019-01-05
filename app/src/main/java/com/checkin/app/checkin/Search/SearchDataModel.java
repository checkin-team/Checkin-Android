package com.checkin.app.checkin.Search;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchDataModel {

    @JsonProperty("rating")
    private int rating;


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("category")
    private String category;


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
