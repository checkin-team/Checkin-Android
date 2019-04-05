package com.checkin.app.checkin.Waiter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import androidx.annotation.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WaiterTableModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("table")
    private String table;

    private int eventCount;

    public WaiterTableModel() {
    }

    public WaiterTableModel(long pk, String table) {
        this.pk = pk;
        this.table = table;
    }

    public long getPk() {
        return pk;
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
        return eventCount > 0 ? String.valueOf(eventCount) : null;
    }

    public void resetEventCount() {
        setEventCount(0);
    }

    public void increaseEventCount() {
        this.eventCount += 1;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            WaiterTableModel table = ((WaiterTableModel) obj);
            return table != null && table.getPk() == this.getPk();
        } catch (ClassCastException ignored) {
            return false;
        }
    }

}
