package com.checkin.app.checkin.Session.Paytm;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ChecksumModel {

    @JsonProperty("CHECKSUMHASH")
    private String checksumHash;

    @JsonProperty("ORDER_ID")
    private String orderId;

    @JsonProperty("payt_STATUS")
    private String paytStatus;

    public ChecksumModel(String checksumHash, String orderId, String paytStatus) {
        this.checksumHash = checksumHash;
        this.orderId = orderId;
        this.paytStatus = paytStatus;
    }

    public String getChecksumHash() {
        return checksumHash;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaytStatus() {
        return paytStatus;
    }
}