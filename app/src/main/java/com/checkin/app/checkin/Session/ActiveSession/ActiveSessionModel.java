package com.checkin.app.checkin.Session.ActiveSession;

import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Session.SessionCustomerModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by Bhavik Patel on 04/08/2018.
 */

public class ActiveSessionModel {
    @JsonProperty("pk")
    private String pk;

    @JsonProperty("bill")
    private String bill;

    // TODO: Remove from here.
    private List<OrderedItemModel> orderedItems;

    @JsonProperty("customers")
    private List<SessionCustomerModel> customers;

    @JsonProperty("restaurant")
    private BriefModel restaurant;

    @JsonProperty("checked_in")
    private Date checkedIn;

    @JsonProperty("checked_out")
    private Date checkedOut;

    @JsonProperty("host")
    private BriefModel host;

    @JsonProperty("is_public")
    private boolean is_public;

    public ActiveSessionModel() {}

    public ActiveSessionModel(String bill, List<OrderedItemModel> orderedItems, List<SessionCustomerModel> customers, BriefModel restaurant, BriefModel host) {
        this.bill = bill;
        this.orderedItems = orderedItems;
        this.customers = customers;
        this.restaurant = restaurant;
        this.host = host;
    }

    public String getBill() {
        return bill;
    }

    public String getPk() {
        return pk;
    }

    public Date getCheckedIn() {
        return checkedIn;
    }

    public Date getCheckedOut() {
        return checkedOut;
    }

    public List<OrderedItemModel> getOrderedItems() {
        return orderedItems;
    }

    public List<SessionCustomerModel> getCustomers() {
        return customers;
    }

    public BriefModel getRestaurant() {
        return restaurant;
    }

    public BriefModel gethost() {
        return host;
    }

    public boolean isIs_public() {
        return is_public;
    }
}
