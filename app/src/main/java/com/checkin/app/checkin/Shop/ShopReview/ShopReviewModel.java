package com.checkin.app.checkin.Shop.ShopReview;

import com.checkin.app.checkin.Misc.BriefModel;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)

public class ShopReviewModel {

    @JsonProperty("description_body")
    private String descriptionBody;
    @JsonProperty("no_of_followers")
    private int noOfFollowers;
    @JsonProperty("no_of_reviews")
    private int noOfReviews;
    @JsonProperty("rating_count")
    private int ratingCount;
    @JsonProperty("time")
    private int time;
    @JsonProperty("user")
    private BriefModel userInfo;

    @JsonProperty("no_of_likes")
    private int noOfLikes;

    public ShopReviewModel() {
    }

    public ShopReviewModel(String descriptionBody, int noOfFollowers, int noOfReviews, int ratingCount, int time, BriefModel userInfo,int noOfLikes) {
        this.descriptionBody = descriptionBody;
        this.noOfFollowers = noOfFollowers;
        this.noOfReviews = noOfReviews;
        this.ratingCount = ratingCount;
        this.time = time;
        this.userInfo = userInfo;
        this.noOfLikes = noOfLikes;
    }

    public String getdescriptionBody() {
        return descriptionBody;
    }

    public void setdescriptionBody(String descriptionBody) {
        this.descriptionBody = descriptionBody;
    }

    public int getNoOfFollowers() {
        return noOfFollowers;
    }

    public void setNoOfFollowers(int noOfFollowers) {
        this.noOfFollowers = noOfFollowers;
    }

    public int getNoOfReviews() {
        return noOfReviews;
    }

    public void setNoOfReviews(int noOfReviews) {
        this.noOfReviews = noOfReviews;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public BriefModel getUserInfo() {
        return userInfo;
    }

    public void setuserInfo(BriefModel userInfo) {
        this.userInfo = userInfo;
    }

    public int getNoOfLikes() {
        return noOfLikes;
    }

    public void setNoOfLikes(int noOfLikes) {
        this.noOfLikes = noOfLikes;
    }
}
