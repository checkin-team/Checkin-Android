package com.checkin.app.checkin.session.model;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            RestaurantTableModel table = ((RestaurantTableModel) obj);
            if (table == null) return false;
            if (table.getTableSession() != null && getTableSession() != null)
                return table.getTableSession().getPk() == getTableSession().getPk();
            return table.getQrPk() == this.getQrPk();
        } catch (ClassCastException ignored) {
            return false;
        }
    }
}