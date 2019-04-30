package com.checkin.app.checkin.session.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckoutStatusModel {
    @JsonProperty("pk")
    private long sessionPk;

    @JsonProperty("is_checkout")
    private boolean isCheckout;

    @JsonProperty("payment_mode")
    private String paymentMode;

    @JsonProperty("detail")
    private String message;

    public CheckoutStatusModel() {
    }

    public long getSessionPk() {
        return sessionPk;
    }

    public void setSessionPk(long sessionPk) {
        this.sessionPk = sessionPk;
    }

    public boolean isCheckout() {
        return isCheckout;
    }

    public void setCheckout(boolean checkout) {
        isCheckout = checkout;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
