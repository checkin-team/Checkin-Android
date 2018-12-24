package com.checkin.app.checkin.Session.ActiveSession;

import com.checkin.app.checkin.Menu.OrderedItemModel;
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
    private double bill;

    // TODO: Remove from here.
    private List<OrderedItemModel> orderedItems;

    @JsonProperty("customers")
    private List<SessionCustomerModel> customers;

    @JsonProperty("checked_in")
    private Date checkedIn;

    @JsonProperty("checked_out")
    private Date checkedOut;

    @JsonProperty("host")
    private BriefModel host;

    public ActiveSessionModel() {}

    public ActiveSessionModel(int bill, List<OrderedItemModel> orderedItems, List<SessionCustomerModel> customers) {
        this.bill = bill;
        this.orderedItems = orderedItems;
        this.customers = customers;
    }

    public double getBill() {
        return bill;
    }

    public List<OrderedItemModel> getOrderedItems() {
        return orderedItems;
    }

    public List<SessionCustomerModel> getCustomers() {
        return customers;
    }
}
