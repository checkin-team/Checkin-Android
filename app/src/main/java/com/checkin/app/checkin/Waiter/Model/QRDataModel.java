package com.checkin.app.checkin.Waiter.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QRDataModel {

    @JsonProperty("qr")
    private long qr;

    @JsonProperty("data")
    private String data;

    public long getQr() {
        return qr;
    }

    public void setQr(long qr) {
        this.qr = qr;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
