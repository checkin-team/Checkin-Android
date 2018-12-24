package com.checkin.app.checkin.Shop;

import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Bhavik Patel on 18/08/2018.
 */

@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class RestaurantModel extends ShopModel {
    @JsonProperty("has_nonveg")
    private boolean hasNonveg;
    @JsonProperty("has_home_delivery")
    private boolean hasHomeDelivery;
    @JsonProperty("has_alcohol")
    private boolean hasAlcohol;

    @JsonProperty("cuisines")
    private CharSequence[] cuisines;
    @JsonProperty("categories")
    private CharSequence[] categories;

    @JsonProperty("no_followers")
    private long followers;
    @JsonProperty("no_checkins")
    private long checkins;
    @JsonProperty("no_reviews")
    private long reviews;

    public RestaurantModel() {}

    public RestaurantModel(String pk) {
        super(pk);
    }

    public boolean servesNonveg() {
        return hasNonveg;
    }

    public void setHasNonveg(boolean hasNonveg) {
        this.hasNonveg = hasNonveg;
    }

    public boolean hasHomeDelivery() {
        return hasHomeDelivery;
    }

    public void setHasHomeDelivery(boolean hasHomeDelivery) {
        this.hasHomeDelivery = hasHomeDelivery;
    }

    public boolean servesAlcohol() {
        return hasAlcohol;
    }

    public void setHasAlcohol(boolean hasAlcohol) {
        this.hasAlcohol = hasAlcohol;
    }

    public CharSequence[] getCuisines() {
        return cuisines;
    }

    public void setCuisines(CharSequence... cuisines) {
        this.cuisines = cuisines;
    }

    public CharSequence[] getCategories() {
        return categories;
    }

    public void setCategories(CharSequence... categories) {
        this.categories = categories;
    }

    // -------------------------------

    @JsonIgnore
    public long getFollowers() {
        return followers;
    }

    @JsonIgnore
    public long getCheckins() {
        return checkins;
    }

    @JsonIgnore
    public long getReviews() { return reviews; }

    public String formatFollowers() {
        return Util.formatCount(followers);
    }

    public String formatCheckins() {
        return Util.formatCount(checkins);
    }

    public String formatReviews() { return Util.formatCount(reviews); }
}
