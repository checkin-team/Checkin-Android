package com.checkin.app.checkin.Shop.Private.Invoice;

import com.checkin.app.checkin.misc.models.BriefModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopSessionFeedbackModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("user")
    private BriefModel user;

    @JsonProperty("body")
    private String body;

    @JsonProperty("food_rating")
    private int foodRating;

    @JsonProperty("ambience_rating")
    private int ambienceRating;

    @JsonProperty("hospitality_rating")
    private int hospitalityRating;

    @JsonProperty("overall_rating")
    private float overallRating;

    @JsonProperty("thumbnails")
    private String[] thumbnails;

    @JsonProperty("created")
    private Date created;

    public ShopSessionFeedbackModel() {
    }

    public long getPk() {
        return pk;
    }

    public BriefModel getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public int getFoodRating() {
        return foodRating;
    }

    public int getAmbienceRating() {
        return ambienceRating;
    }

    public int getHospitalityRating() {
        return hospitalityRating;
    }

    public float getOverallRating() {
        return overallRating;
    }

    public String[] getThumbnails() {
        return thumbnails;
    }

    public Date getCreated() {
        return created;
    }
}
