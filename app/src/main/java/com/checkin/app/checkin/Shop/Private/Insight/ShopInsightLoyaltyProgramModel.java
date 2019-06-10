package com.checkin.app.checkin.Shop.Private.Insight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopInsightLoyaltyProgramModel {

    @JsonProperty("discounts")
    private double discounts;

    public ShopInsightLoyaltyProgramModel() {
    }

    public double getDiscounts() {
        return discounts;
    }
}
