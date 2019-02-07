package com.checkin.app.checkin.Waiter.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WaiterTableModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("table")
    private String table;

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
}
