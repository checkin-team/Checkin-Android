package com.checkin.app.checkin.Shop.Private.Finance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public FinanceModel() {
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public double getCgstPercent() {
        return cgstPercent;
    }

    public void setCgstPercent(double cgstPercent) {
        this.cgstPercent = cgstPercent;
    }

    public double getSgstPercent() {
        return sgstPercent;
    }

    public void setSgstPercent(double sgstPercent) {
        this.sgstPercent = sgstPercent;
    }

    public double getIgstPercent() {
        return igstPercent;
    }

    public void setIgstPercent(double igstPercent) {
        this.igstPercent = igstPercent;
    }

    public double getGstPercent() {
        return gstPercent;
    }

    public void setGstPercent(double gstPercent) {
        this.gstPercent = gstPercent;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }
}
