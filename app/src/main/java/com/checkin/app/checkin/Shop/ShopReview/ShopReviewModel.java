package com.checkin.app.checkin.Shop.ShopReview;

import com.checkin.app.checkin.Misc.BriefModel;

import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)

public class ShopReviewModel {

    @JsonProperty("body")
    private String textBody;

    @JsonProperty("count_followers")
    private int countFollowers;

    @JsonProperty("no_of_reviews")
    private int noOfReviews;

    @JsonProperty("rating_count")
    private int ratingCount;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("user")
    private BriefModel userInfo;

    @JsonProperty("likes")
    private long countlikes;

    @JsonProperty("is_liked")
    private boolean isLiked;

    @JsonProperty("thumbnails")
    private List<String> thumbnails;

    public ShopReviewModel() {
    }

    public ShopReviewModel(String textBody, int countFollowers, int noOfReviews, int ratingCount, Date created, BriefModel userInfo, long countlikes,boolean isLiked) {
        this.textBody = textBody;
        this.countFollowers = countFollowers;
        this.noOfReviews = noOfReviews;
        this.ratingCount = ratingCount;
        this.created = created;
        this.userInfo = userInfo;
        this.countlikes = countlikes;
        this.isLiked = isLiked;
    }

    public String getdescriptionBody() {
        return textBody;
    }

    public void setdescriptionBody(String descriptionBody) {
        this.textBody = descriptionBody;
    }


    public int getCountFollowers() {
        return countFollowers;
    }

    public void setCountFollowers(int countFollowers) {
        this.countFollowers = countFollowers;
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

    public Date getTime() {
        return created;
    }

    public void setTime(int time) {
        this.created = created;
    }

    public BriefModel getUserInfo() {
        return userInfo;
    }

    public void setuserInfo(BriefModel userInfo) {
        this.userInfo = userInfo;
    }

    public long getNoOfLikes() {
        return countlikes;
    }

    public void setNoOfLikes(int noOfLikes) {
        this.countlikes = countlikes;
    }
    public boolean isLiked() {
        return isLiked;
    }
    public String formatReviewTime(){
        return Util.formatDateToHoursTime(created);
    }
    public String formatCountLikes()
    {
        return Util.formatCount(countlikes);
    }
}
