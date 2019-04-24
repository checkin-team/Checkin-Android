package com.checkin.app.checkin.Session.Paytm;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.checkin.app.checkin.Utility.Constants.PAYTM_CALLBACK_URL;

public class PaytmModel {

    @JsonProperty("merchant_id")
    String merchantId;

    @JsonProperty("order_id")
    String orderId;

    @JsonProperty("customer_id")
    String custId;

    @JsonProperty("phone")
    String phone;

    @JsonProperty("email")
    String email;

    @JsonProperty("industry_type_id")
    String industryTypeId;

    @JsonProperty("website")
    String website;

    @JsonProperty("amount")
    String amount;

    @JsonProperty("checksum_hash")
    String checksumHash;

    String callbackURL;


    public PaytmModel(){};

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIndustryTypeId() {
        return industryTypeId;
    }

    public void setIndustryTypeId(String industryTypeId) {
        this.industryTypeId = industryTypeId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getChecksumHash() {
        return checksumHash;
    }

    public void setChecksumHash(String checksumHash) {
        this.checksumHash = checksumHash;
    }

    public String getCallbackURL() {
        return callbackURL = PAYTM_CALLBACK_URL + getOrderId();
    }

}
