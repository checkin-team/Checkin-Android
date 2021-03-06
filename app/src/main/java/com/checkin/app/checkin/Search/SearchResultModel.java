package com.checkin.app.checkin.Search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Jogi Miglani on 26-10-2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResultModel {
    public static final int TYPE_PEOPLE = 1, TYPE_RESTAURANT = 2;

    @JsonProperty("pk")
    private Long pk;

    @JsonProperty("display_name")
    private String name;

    @JsonProperty("display_pic")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPk() {
        return pk;
    }

    public int getType() {
        if (this instanceof SearchResultPeopleModel)
            return TYPE_PEOPLE;
        else if (this instanceof SearchResultShopModel)
            return TYPE_RESTAURANT;
        return 0;
    }

    public boolean isTypeRestaurant() {
        return this instanceof SearchResultShopModel;
    }

    public boolean isTypePeople() {
        return this instanceof SearchResultPeopleModel;
    }
}
