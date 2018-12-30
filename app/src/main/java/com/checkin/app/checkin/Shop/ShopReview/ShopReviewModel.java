package com.checkin.app.checkin.Shop.ShopReview;

import com.checkin.app.checkin.Misc.BriefModel;

import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Locale;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ShopReviewModel {

    @JsonProperty("body")
    private String reviewBody;

    @JsonProperty("user_count_followers")
    private int userCountFollowers;

    @JsonProperty("user_count_reviews")
    private int userCountReviews;

    @JsonProperty("rating_count")
    private int ratingCount;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("user")
    private BriefModel userInfo;

    @JsonProperty("liked")
    private long countLikes;

    @JsonProperty("is_liked")
    private boolean isLiked;

    @JsonProperty("thumbnails")
    private List<String> thumbnails;

    public ShopReviewModel() {
    }

    public ShopReviewModel(String textBody, int userCountFollowers, int noOfReviews, int ratingCount, Date created, BriefModel userInfo, long countlikes, boolean isLiked) {
        this.reviewBody = textBody;
        this.userCountFollowers = userCountFollowers;
        this.userCountReviews = noOfReviews;
        this.ratingCount = ratingCount;
        this.created = created;
        this.userInfo = userInfo;
        this.countLikes = countlikes;
        this.isLiked = isLiked;
    }

    public String getdescriptionBody() {
        return reviewBody;
    }

    public void setdescriptionBody(String descriptionBody) {
        this.reviewBody = descriptionBody;
    }


    public int getUserCountFollowers() {
        return userCountFollowers;
    }

    public void setUserCountFollowers(int userCountFollowers) {
        this.userCountFollowers = userCountFollowers;
    }

    public int getUserCountReviews() {
        return userCountReviews;
    }

    public void setUserCountReviews(int userCountReviews) {
        this.userCountReviews = userCountReviews;
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
        return countLikes;
    }

    public void setNoOfLikes(int noOfLikes) {
        this.countLikes = countLikes;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public String formatReviewTime() {
        return Util.formatElapsedTime(created);
    }

    public String formatUserStats() {
        return String.format(Locale.ENGLISH, "%d reviews, %d followers", userCountReviews, userCountFollowers);
    }

    public String formatCountLikes()
    {
        return Util.formatCount(countLikes);
    }
}
