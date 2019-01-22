package com.checkin.app.checkin.Shop.ShopInvoice;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantSessionModel  implements Serializable {
    @JsonProperty("pk")
    private Integer pk;
    @JsonProperty("hash_id")
    private String hashId;
    @JsonProperty("count_orders")
    private Integer countOrders;
    @JsonProperty("count_customers")
    private Integer countCustomers;
    @JsonProperty("total")
    private String total;
    @JsonProperty("host")
    private BriefModel host;
    @JsonProperty("table")
    private String table;
    @JsonProperty("checked_in")
    private Date checkedIn;
    @JsonProperty("checked_out")
    private Date checkedOut;

    public Integer getPk() {
        return pk;
    }

    public Integer getCountCustomers() {
        return countCustomers;
    }

    public String getHashId() {
        return hashId;
    }

    public Integer getCountOrders() {
        return countOrders;
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

    public String getFormattedDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm a",Locale.getDefault());
        return formatter.format(date);
    }

    public String getTotal() {
        return total;
    }

    public BriefModel getHost() {
        return host;
    }
}
