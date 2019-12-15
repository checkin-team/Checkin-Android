package com.checkin.app.checkin.Cook.Model;

import androidx.annotation.Nullable;

import com.checkin.app.checkin.misc.models.BriefModel;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CookTableModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("table")
    private String table;

    @Nullable
    @JsonProperty("host")
    private BriefModel host;

    @JsonProperty("pending_orders")
    private int pendingOrders;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("is_accepted_checkout")
    private boolean isRequestedCheckout;

    @JsonProperty("bill")
    private double bill;

    private int eventCount = 0;

    public CookTableModel() {
    }

    public CookTableModel(long pk, @Nullable BriefModel host) {
        this.pk = pk;
        this.host = host;
        this.pendingOrders = 0;
    }

    public long getPk() {
        return pk;
    }

    @Nullable
    public BriefModel getHost() {
        return host;
    }

    public void setHost(@Nullable BriefModel host) {
        this.host = host;
    }

    public boolean hasHost() {
        return host != null;
    }

    public boolean isRequestedCheckout() {
        return isRequestedCheckout;
    }

    public void setRequestedCheckout(boolean requestedCheckout) {
        isRequestedCheckout = requestedCheckout;
    }

    public Date getCreated() {
        return created;
    }

    public String formatTimeDuration() {
        return Utils.formatTimeDuration(Calendar.getInstance().getTime().getTime() - created.getTime());
    }

    public double getBill() {
        return bill;
    }

    public void setBill(double bill) {
        this.bill = bill;
    }

    public Integer getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(int pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            CookTableModel table = ((CookTableModel) obj);
            return table != null && table.getPk() == this.getPk();
        } catch (ClassCastException ignored) {
            return false;
        }
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Integer getEventCount() {
        return eventCount;
    }

    public void resetEventCount() {
        this.eventCount = 0;
    }

    public void increaseEventCount() {
        this.eventCount++;
    }

    public String formatEventCount() {
        return String.valueOf(eventCount);
    }

    public String formatOrderStatus() {
        if (eventCount > 0) {
            return "New Order!";
        } else if (pendingOrders > 0) {
            return String.format(Locale.getDefault(), "%d orders in line. Mark ready after preparation.", pendingOrders);
        } else {
            return "No active orders.";
        }
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}