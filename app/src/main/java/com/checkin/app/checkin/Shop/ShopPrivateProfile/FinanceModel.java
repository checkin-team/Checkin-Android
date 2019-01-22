package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinanceModel {

    @JsonProperty("gstin")
    private String gstin;
    @JsonProperty("cgst_percent")
    private double cgstPercent;
    @JsonProperty("sgst_percent")
    private double sgstPercent;
    @JsonProperty("igst_percent")
    private double igstPercent;
    @JsonProperty("gst_percent")
    private double gstPercent;
    @JsonProperty("discount_percent")
    private double discountPercent;
    @JsonProperty("total_discount")
    private double totalDiscount;


    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public void setCgstPercent(double cgstPercent) {
        this.cgstPercent = cgstPercent;
    }

    public void setSgstPercent(double sgstPercent) {
        this.sgstPercent = sgstPercent;
    }

    public void setIgstPercent(double igstPercent) {
        this.igstPercent = igstPercent;
    }

    public void setGstPercent(double gstPercent) {
        this.gstPercent = gstPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public String getGstin() {
        return gstin;
    }

    public double getCgstPercent() {
        return cgstPercent;
    }

    public double getSgstPercent() {
        return sgstPercent;
    }

    public double getIgstPercent() {
        return igstPercent;
    }

    public double getGstPercent() {
        return gstPercent;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }
}
