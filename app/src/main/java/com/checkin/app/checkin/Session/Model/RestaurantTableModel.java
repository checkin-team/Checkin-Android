package com.checkin.app.checkin.Session.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import androidx.annotation.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantTableModel {
    public static final long NO_QR_ID = -1;

    @JsonProperty("qr_pk")
    private long qrPk;

    @JsonProperty("table")
    private String table;

    @Nullable
    @JsonProperty("session")
    private TableSessionModel tableSession;

    private int eventCount;

    public RestaurantTableModel() {
    }

    public RestaurantTableModel(long qrPk, String table, @Nullable TableSessionModel tableSessionModel) {
        this.qrPk = qrPk;
        this.table = table;
        this.tableSession = tableSessionModel;
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
    public TableSessionModel getTableSession() {
        return tableSession;
    }

    public void setTableSession(@Nullable TableSessionModel tableSessionModel) {
        this.tableSession = tableSessionModel;
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

    public boolean isSessionActive() {
        return tableSession != null;
    }
}