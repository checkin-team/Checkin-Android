package com.checkin.app.checkin.manager.model;

import com.checkin.app.checkin.session.model.SessionInvoiceModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ManagerSessionInvoiceModel extends SessionInvoiceModel {
    @JsonProperty("discount_percent")
    private double discountPercentage;

    public ManagerSessionInvoiceModel() {
    }

    public double getDiscountPercent() {
        return discountPercentage;
    }

    public String formatDiscountPercent() {
        return String.valueOf(discountPercentage);
    }
}
