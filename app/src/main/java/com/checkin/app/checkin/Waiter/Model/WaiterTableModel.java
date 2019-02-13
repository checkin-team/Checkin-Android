package com.checkin.app.checkin.Waiter.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WaiterTableModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("table")
    private String table;

    private int eventCount;

    public WaiterTableModel() {}

    public WaiterTableModel(long pk, String table){
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

    public String formatEventCount() {
        return String.valueOf(eventCount);
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    public void resetEventCount() {
        setEventCount(0);
    }

    public void increaseEventCount() {
        this.eventCount += 1;
    }
}
