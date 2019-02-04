package com.checkin.app.checkin.Session.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionBillModel {
    @JsonProperty("subtotal")
    private Double subtotal;

    @JsonProperty("tax")
    private Double tax;

    @JsonProperty("tip")
    private Double tip;

    @JsonProperty("discount")
    private Double discount;

    @JsonProperty("offers")
    private Double offers;

    @JsonProperty("total")
    private Double total;

    private Double discountPercentage;

    public SessionBillModel(){}

    public Double getSubtotal() {
        return subtotal;
    }

    public Double getTax() {
        return tax;
    }

    public Double getTip() {
        return tip;
    }

    public Double getDiscount() {
        return discount;
    }

    public Double getOffers() {
        return offers;
    }

    @JsonProperty("offers")
    public void setOffers(Double offers) {
        this.offers = (offers > 0) ? offers : null;
    }

    @JsonProperty("discount")
    public void setDiscount(Double discount) {
        this.discount = (discount > 0) ? discount : null;
    }

    public String formatSubTotal() {
        return String.valueOf(subtotal);
    }

    public String formatTax() {
        return String.valueOf(tax);
    }

    public String formatTip() {
        return String.valueOf(tip);
    }

    public String formatDiscount() {
        return String.valueOf(discount);
    }

    public String formatTotal() {
        return String.valueOf(total);
    }

    public Double getTotal() {
        return total;
    }

    public void calculateDiscount(Double percent) {
        discountPercentage = percent;

        double oldDiscount = discount;
        discount = (subtotal * percent) / 100;
        total -= (discount - oldDiscount);
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}
