package com.checkin.app.checkin.Shop.Private.Invoice;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantSessionModel implements Serializable {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("hash_id")
    private String hashId;

    @JsonProperty("count_orders")
    private int countOrders;

    @JsonProperty("count_customers")
    private int countCustomers;

    @JsonProperty("total")
    private double total;

    @JsonProperty("host")
    private BriefModel host;

    @JsonProperty("table")
    private String table;

    @JsonProperty("checked_in")
    private Date checkedIn;

    @JsonProperty("checked_out")
    private Date checkedOut;

    public RestaurantSessionModel() {
    }

    public long getPk() {
        return pk;
    }

    public String getHashId() {
        return hashId;
    }

    public int getCountOrders() {
        return countOrders;
    }

    public int getCountCustomers() {
        return countCustomers;
    }

    public double getTotal() {
        return total;
    }

    public BriefModel getHost() {
        return host;
    }

    public String getTable() {
        return table;
    }

    public Date getCheckedIn() {
        return checkedIn;
    }

    public Date getCheckedOut() {
        return checkedOut;
    }

    public String getFormattedDate() {
        return Utils.formatCompleteDate(checkedOut);
    }

    public String formatTotal() {
        return String.valueOf(total);
    }
}
