package com.checkin.app.checkin.Waiter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WaiterSessionCreateModel {
    @JsonProperty("session_pk")
    private long sessionPk;
    @JsonProperty("restaurant_pk")
    private long restaurant_pk;
    @JsonProperty("detail")
    private String detail;
    @JsonProperty("table")
    private String table;

    public long getSessionPk() {
        return sessionPk;
    }

    public void setSessionPk(long sessionPk) {
        this.sessionPk = sessionPk;
    }

    public long getRestaurant_pk() {
        return restaurant_pk;
    }

    public void setRestaurant_pk(long restaurant_pk) {
        this.restaurant_pk = restaurant_pk;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
