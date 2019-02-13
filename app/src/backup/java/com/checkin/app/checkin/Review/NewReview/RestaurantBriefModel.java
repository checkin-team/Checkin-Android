package com.checkin.app.checkin.Review.NewReview;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestaurantBriefModel {

    @JsonProperty("pk")
    protected String pk;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("logo")
    protected String logo;

    @JsonProperty("locality")
    protected String locality;

    @JsonProperty("rating")
    protected String rating;

    public RestaurantBriefModel() {
    }

    public String getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public String getLocality() {
        return locality;
    }

    public String getRating() {
        return rating;
    }
}
