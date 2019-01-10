package com.checkin.app.checkin.Review.ShopReview;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Locale;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ShopReviewModel {
    @JsonProperty("pk")
    private String pk;

    @JsonProperty("user")
    private BriefModel userInfo;

    @JsonProperty("body")
    private String reviewBody;

    @JsonProperty("user_count_followers")
    private int userCountFollowers;

    @JsonProperty("user_count_reviews")
    private int userCountReviews;

    @JsonProperty("overall_rating")
    private int overallRating;

    @JsonProperty("food_rating")
    private int foodRating;

    @JsonProperty("ambience_rating")
    private int ambienceRating;

    @JsonProperty("hospitality_rating")
    private int hospitalityRating;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("modified")
    private Date modified;

    @JsonProperty("liked")
    private long countLikes;

    @JsonProperty("is_liked")
    private boolean isLiked;

    @JsonProperty("ambience_pics")
    private String[] ambiencePics;

    @JsonProperty("food_pics")
    private String[] foodPics;

    @JsonProperty("thumbnails")
    private String[] thumbnails;

    public ShopReviewModel() {
    }

    public boolean isLiked() {
        return isLiked;
    }

    public String formatOverallRating() {
        return String.format(Locale.ENGLISH, "RATED  %d", overallRating);
    }

    public String formatReviewTime() {
        if (!isEdited())
            return Utils.formatElapsedTime(created);
        else
            return String.format(Locale.ENGLISH, "(EDITED)\n%s", Utils.formatElapsedTime(modified));
    }

    public String formatUserStats() {
        return String.format(Locale.ENGLISH, "%d reviews, %d followers", userCountReviews, userCountFollowers);
    }

    public String formatCountLikes()
    {
        return Utils.formatCount(countLikes);
    }

    public BriefModel getUserInfo() {
        return userInfo;
    }

    public String getReviewBody() {
        return reviewBody;
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

    public Date getModified() {
        return modified;
    }

    public String[] getAmbiencePics() {
        return ambiencePics;
    }

    public String[] getFoodPics() {
        return foodPics;
    }

    public String[] getThumbnails() {
        return thumbnails;
    }

    public String getPk() {
        return pk;
    }

    public void toggleLike() {
        isLiked = !isLiked;
    }

    public boolean isEdited() {
        return modified != null && created.getTime() != modified.getTime();
    }
}
