package com.checkin.app.checkin.Session.Model;

import android.support.annotation.Nullable;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class RestaurantTableModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("table")
    private String table;

    @Nullable
    @JsonProperty("host")
    private BriefModel host;

    @JsonProperty("event")
    private EventBriefModel event;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("is_requested_checkout")
    private boolean isRequestedCheckout;

    private int eventCount;

    public RestaurantTableModel() {}

    public RestaurantTableModel(long pk, String table, @Nullable BriefModel host, EventBriefModel event) {
        this.pk = pk;
        this.table = table;
        this.host = host;
        this.event = event;
    }

    public long getPk() {
        return pk;
    }

    public String getTable() {
        return table;
    }

    @Nullable
    public BriefModel getHost() {
        return host;
    }

    public EventBriefModel getEvent() {
        return event;
    }

    public Date getCreated() {
        return created;
    }

    public boolean isRequestedCheckout() {
        return isRequestedCheckout;
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
}