package com.checkin.app.checkin.session.model;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Calendar;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonProperty("is_accepted_checkout")
    private boolean isRequestedCheckout;

    public SessionBriefModel() {
    }

    public long getPk() {
        return pk;
    }

    public BriefModel getHost() {
        return host;
    }

    public void setHost(BriefModel host) {
        this.host = host;
    }

    public int getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(int customerCount) {
        this.customerCount = customerCount;
    }

    public double getBill() {
        return bill;
    }

    public void setBill(double bill) {
        this.bill = bill;
    }

    public String formatBill() {
        return String.valueOf(bill);
    }

    public Date getCreated() {
        return created;
    }

    public String formatCustomerCount() {
        return String.valueOf(customerCount);
    }

    public String formatTimeDuration() {
        return Utils.formatTimeDuration(Calendar.getInstance().getTime().getTime() - created.getTime());
    }

    public String getTable() {
        return table;
    }

    public boolean isRequestedCheckout() {
        return isRequestedCheckout;
    }

    public void setRequestedCheckout(boolean requestedCheckout) {
        this.isRequestedCheckout = requestedCheckout;
    }
}
