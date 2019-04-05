package com.checkin.app.checkin.Session.Model;

import android.content.Context;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by Bhavik Patel on 04/08/2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonProperty("is_requested_checkout")
    private boolean isRequestedCheckout;

    public ActiveSessionModel() {
    }

    public String getBill() {
        return bill;
    }

    public void setBill(String bill) {
        this.bill = bill;
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

    public void addCustomer(SessionCustomerModel customerModel) {
        customers.add(customerModel);
    }

    public BriefModel getRestaurant() {
        return restaurant;
    }

    public BriefModel getHost() {
        return host;
    }

    public void setHost(BriefModel user) {
        this.host = user;
    }

    public boolean isCheckinPublic() {
        return isPublic;
    }

    public int getShopPk() {
        return Integer.valueOf(restaurant.getPk());
    }

    public String formatBill(Context context) {
        return Utils.formatCurrencyAmount(context, bill);
    }

    public boolean isRequestedCheckout() {
        return isRequestedCheckout;
    }

    public void setRequestedCheckout(boolean requestedCheckout) {
        isRequestedCheckout = requestedCheckout;
    }
}
