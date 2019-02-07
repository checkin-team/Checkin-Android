package com.checkin.app.checkin.Manager.Model;

import com.checkin.app.checkin.Session.Model.SessionInvoiceModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ManagerSessionInvoiceModel extends SessionInvoiceModel {
    @JsonProperty("discount_percent")
    private double discountPercentage;

    public ManagerSessionInvoiceModel() {}

    public double getDiscountPercent() {
        return discountPercentage;
    }

    public String formatDiscountPercent() {
        return String.valueOf(discountPercentage);
    }
}
