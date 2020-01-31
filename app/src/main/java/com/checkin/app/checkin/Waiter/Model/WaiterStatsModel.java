package com.checkin.app.checkin.Waiter.Model;

import com.checkin.app.checkin.utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WaiterStatsModel {
    @JsonProperty("tip_for_day")
    private double tipOfDay;

    @JsonProperty("orders_for_day")
    private int orderOfDay;

    public WaiterStatsModel() {
    }

    public double getTipOfDay() {
        return tipOfDay;
    }

    public int getOrderOfDay() {
        return orderOfDay;
    }

    public String formatOrdersTaken() {
        return Utils.formatCount(orderOfDay);
    }
}
