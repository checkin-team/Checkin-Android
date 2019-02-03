package com.checkin.app.checkin.Session.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionBillModel {
    @JsonProperty("subtotal")
    private String subtotal;

    @JsonProperty("tax")
    private String tax;

    @JsonProperty("tip")
    private String tip;

    @JsonProperty("discount")
    private String discount;

    @JsonProperty("offers")
    private String offers;

    @JsonProperty("total")
    private String total;

    public SessionBillModel(){}

    public SessionBillModel(String subtotal, String tax, String tip, String discount, String offers, String total) {
        this.subtotal = subtotal;
        this.tax = tax;
        this.tip = tip;
        this.discount = discount;
        this.offers = offers;
        this.total = total;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public String getTax() {
        return tax;
    }

    public String getTip() {
        return tip;
    }

    public String getDiscount() {
        return discount;
    }

    public String getOffers() {
        return offers;
    }

    public String getTotal() {
        return total;
    }
}
