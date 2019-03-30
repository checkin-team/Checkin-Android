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

    @Nullable
    @JsonProperty("session")
    private TableSessionModel tableSessionModel;

    private int eventCount;

    public RestaurantTableModel() {
    }

    public RestaurantTableModel(long qrPk, String table, @Nullable TableSessionModel tableSessionModel) {
        this.qrPk = qrPk;
        this.table = table;
        this.tableSessionModel = tableSessionModel;
    }

    public long getQrPk() {
        return qrPk;
    }

    public void setQrPk(long qrPk) {
        this.qrPk = qrPk;
    }

    public String getTable() {
        return table;
    }

    @Nullable
    public TableSessionModel getTableSessionModel() {
        return tableSessionModel;
    }

    public void setTableSessionModel(@Nullable TableSessionModel tableSessionModel) {
        this.tableSessionModel = tableSessionModel;
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