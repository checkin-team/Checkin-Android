package com.checkin.app.checkin.Session.Model;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class SessionBriefModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("host")
    private BriefModel host;

    @JsonProperty("customer_count")
    private int customerCount;

    @JsonProperty("bill")
    private double bill;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("table")
    private String table;

    public SessionBriefModel() {}

    public long getPk() {
        return pk;
    }

    public BriefModel getHost() {
        return host;
    }

    public int getCustomerCount() {
        return customerCount;
    }

    public double getBill() {
        return bill;
    }

    public Date getCreated() {
        return created;
    }

    public String formatCustomerCount() {
        return String.valueOf(customerCount);
    }

    public String getTable() {
        return table;
    }
}
