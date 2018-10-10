package com.checkin.app.checkin.Shop;

import java.util.ArrayList;

/**
 * Created by Jogi Miglani on 08-10-2018.
 */

public class ShopReviewPOJO {

    private String name,reviews_and_followers,totalVisits,fullReview,time;
    private int rating;

    private ArrayList<ShopReviewPOJO> shopReviewPOJO =new ArrayList<>();

    public ShopReviewPOJO() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReviewsAndFollowers() {
        return reviews_and_followers;
    }

    public void setReviewsAndFollowers(String reviews_and_followers) {
        this.reviews_and_followers = reviews_and_followers;
    }

    public String getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(String totalVisits) {
        this.totalVisits = totalVisits;
    }

    public String getFullReview() {
        return fullReview;
    }

    public void setFullReview(String fullReview) {
        this.fullReview = fullReview;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
