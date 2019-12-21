package com.checkin.app.checkin.session.models;

import com.checkin.app.checkin.misc.models.BriefModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionBasicModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("restaurant")
    private BriefModel restaurant;

    public SessionBasicModel() {
    }

    public long getPk() {
        return pk;
    }

    public BriefModel getRestaurant() {
        return restaurant;
    }

    public String getLiveStatus() {
        return String.format(Locale.ENGLISH, "Live at %s", restaurant.getDisplayName());
    }
}
