package com.checkin.app.checkin.Search;

import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Locale;

public class SearchResultShopModel extends SearchResultModel{
    @JsonProperty("rating")
    private float rating;

    @JsonProperty("categories")
    private List<String> categories;

    @JsonProperty("locality")
    private String locality;

    @JsonProperty("is_following")
    private boolean isFollowing;

    public List<String> getCategories() {
        return categories;
    }

    public float getRating() {
        return rating;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public String getLocality() {
        return locality;
    }

    public String formatCategories() {
        if (categories.size() > 0)
            return Util.joinCollection(categories, ", ");
        return "None";
    }

    public String formatExtra() {
        if (isFollowing)
            return String.format(Locale.ENGLISH, "%.1f | %s | Following", rating, formatCategories());
        else
            return String.format(Locale.ENGLISH, "%f | %s", rating, formatCategories());
    }
}
