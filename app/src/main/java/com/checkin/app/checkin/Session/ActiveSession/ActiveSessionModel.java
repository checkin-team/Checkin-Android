package com.checkin.app.checkin.Session.ActiveSession;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Session.Model.SessionCustomerModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by Bhavik Patel on 04/08/2018.
 */

public class ActiveSessionModel {
    @JsonProperty("pk")
    private int pk;

    @JsonProperty("bill")
    private String bill;

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
    private boolean isPublic;

    public ActiveSessionModel() {}

    public String getBill() {
        return bill;
    }

    public int getPk() {
        return pk;
    }

    public Date getCheckedIn() {
        return checkedIn;
    }

    public Date getCheckedOut() {
        return checkedOut;
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

    public boolean isCheckinPublic() {
        return isPublic;
    }

    public int getShopPk() {
        return Integer.valueOf(restaurant.getPk());
    }

    public void setBill(String bill) {
        this.bill = bill;
    }
}
