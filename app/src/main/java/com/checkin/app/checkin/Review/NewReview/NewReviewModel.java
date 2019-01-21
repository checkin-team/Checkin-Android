package com.checkin.app.checkin.Review.NewReview;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class NewReviewModel {
    @JsonProperty("body")
    private String body;

    @JsonProperty("food_rating")
    private int foodRating;

    @JsonProperty("ambience_rating")
    private int ambienceRating;

    @JsonProperty("hospitality_rating")
    private int hospitalityRating;

    @JsonProperty("image_pks")
    private List<String> imagePks = new ArrayList<>();

    public void addImage(String imagePk) {
        this.imagePks.add(imagePk);
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

    public List<String> getImagePks() {
        return imagePks;
    }

    public void setFoodRating(int foodRating) {
        this.foodRating = foodRating;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setAmbienceRating(int ambienceRating) {
        this.ambienceRating = ambienceRating;
    }

    public void setHospitalityRating(int hospitalityRating) {
        this.hospitalityRating = hospitalityRating;
    }

    public boolean isValidData() {
        return foodRating > 0 && ambienceRating > 0 && hospitalityRating > 0;
    }
}
