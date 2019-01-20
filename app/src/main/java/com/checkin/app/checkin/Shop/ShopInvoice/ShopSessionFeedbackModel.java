package com.checkin.app.checkin.Shop.ShopInvoice;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopSessionFeedbackModel {

    @JsonProperty("pk")
    private Integer pk;
    @JsonProperty("user")
    private BriefModel user;
    @JsonProperty("body")
    private String body;
    @JsonProperty("food_rating")
    private Integer foodRating;
    @JsonProperty("ambience_rating")
    private Integer ambienceRating;
    @JsonProperty("hospitality_rating")
    private Integer hospitalityRating;
    @JsonProperty("overall_rating")
    private Integer overallRating;
    @JsonProperty("thumbnails")
    private List<String> thumbnails = null;
    @JsonProperty("created")
    private String created;

    public Integer getPk() {
        return pk;
    }

    public BriefModel getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public Integer getFoodRating() {
        return foodRating;
    }

    public Integer getAmbienceRating() {
        return ambienceRating;
    }

    public Integer getHospitalityRating() {
        return hospitalityRating;
    }

    public Integer getOverallRating() {
        return overallRating;
    }

    public List<String> getThumbnails() {
        return thumbnails;
    }

    public String getCreated() {
        return created;
    }
}
