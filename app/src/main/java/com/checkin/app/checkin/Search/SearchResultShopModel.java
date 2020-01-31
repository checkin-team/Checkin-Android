package com.checkin.app.checkin.Search;

import com.checkin.app.checkin.utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResultShopModel extends SearchResultModel {
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
        return locality != null ? locality : "None";
    }

    public String formatCategories() {
        if (categories.size() > 0)
            return Utils.joinCollection(categories, ", ");
        return "None";
    }

    public String formatExtra() {
        if (isFollowing)
            return String.format(Locale.ENGLISH, "%.1f | %s | Following", rating, formatCategories());
        else
            return String.format(Locale.ENGLISH, "%f | %s", rating, formatCategories());
    }
}
