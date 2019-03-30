package com.checkin.app.checkin.Session.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import androidx.annotation.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantTableModel {

    @JsonProperty("qr_pk")
    private long qrPk;

    @JsonProperty("table")
    private String table;

    @JsonProperty("session")
    private WaiterEndSessionModel waiterEndSessionModel;

    private int eventCount;

    public RestaurantTableModel() {
    }

    public RestaurantTableModel(long qrPk, String table, @Nullable WaiterEndSessionModel waiterEndSessionModel) {
        this.qrPk = qrPk;
        this.table = table;
        this.waiterEndSessionModel = waiterEndSessionModel;
    }

    public String getTable() {
        return table;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    public String formatEventCount() {
        return String.valueOf(eventCount);
    }

    public void addEventCount() {
        this.eventCount++;
    }
}