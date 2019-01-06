package com.checkin.app.checkin.Search;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchRestaurantResultModel extends SearchResultModel{

    @JsonProperty("rating")
    private int rating;

    @JsonProperty("category")
    private String category;

    @JsonProperty("cuisine")
    private String cuisine;

    private FRIEND_STATUS mStatus;

    public enum FRIEND_STATUS {
        NONE("none"), FRIENDS("frnd"), PENDING_REQUEST("rqst");

        private String tag;
        FRIEND_STATUS(String tag) {
            this.tag = tag;
        }

        public static FRIEND_STATUS getByTag(String tag) {
            for (FRIEND_STATUS status: FRIEND_STATUS.values()) {
                if (status.tag.equals(tag)) {
                    return status;
                }
            }
            return NONE;
        }
    }


    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


    public FRIEND_STATUS getmStatus() {
        return mStatus;
    }

    @JsonProperty("friend_status")
    public void setUserStatus(String tag){
        this.mStatus = FRIEND_STATUS.getByTag(tag);
    }
}
