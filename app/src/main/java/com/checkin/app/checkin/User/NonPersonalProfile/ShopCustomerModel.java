package com.checkin.app.checkin.User.NonPersonalProfile;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopCustomerModel {

    @JsonProperty("shop")
    private BriefModel shop;

    @JsonProperty("locality")
    private String location;

    @JsonProperty("no_visits")
    private int countVisits;

    public ShopCustomerModel() {}

    public BriefModel getShop() {
        return shop;
    }

    public String getLocation() {
        return location;
    }

    public int getCountVisits() {
        return countVisits;
    }

    public String formatCountVisits() {
        return String.valueOf(countVisits);
    }
}
