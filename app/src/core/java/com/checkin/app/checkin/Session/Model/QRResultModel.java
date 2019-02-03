package com.checkin.app.checkin.Session.Model;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QRResultModel {
    @JsonProperty("session_pk")
    private long sessionPk;

    @JsonProperty("restaurant_pk")
    private long restaurantPk;

    @JsonProperty("detail")
    private String detail;

    @Nullable
    @JsonProperty("table")
    private String table;

    public QRResultModel() {}

    public long getSessionPk() {
        return sessionPk;
    }

    public long getRestaurantPk() {
        return restaurantPk;
    }

    public String getDetail() {
        return detail;
    }

    @Nullable
    public String getTable() {
        return table;
    }
}
