package com.checkin.app.checkin.Session.Paytm;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.checkin.app.checkin.Utility.Constants.PAYTM_CALLBACK_URL;
import static com.checkin.app.checkin.Utility.Constants.PAYTM_CHANNEL_ID;
import static com.checkin.app.checkin.Utility.Constants.PAYTM_INDUSTRY_TYPE_ID;
import static com.checkin.app.checkin.Utility.Constants.PAYTM_M_ID;
import static com.checkin.app.checkin.Utility.Constants.PAYTM_WEBSITE;

public class PaytmModel {

    @JsonProperty("MID")
    String mId;

    @JsonProperty("ORDER_ID")
    String orderId;

    @JsonProperty("CUST_ID")
    String custId;

    @JsonProperty("CHANNEL_ID")
    String channelId;

    @JsonProperty("TXN_AMOUNT")
    String txnAmount;

    @JsonProperty("WEBSITE")
    String website;

    @JsonProperty("CALLBACK_URL")
    String callBackUrl;

    @JsonProperty("INDUSTRY_TYPE_ID")
    String industryTypeId;

    public PaytmModel(){};

    public PaytmModel(String mId, String orderId, String custId ,String channelId, String txnAmount, String website, String callBackUrl, String industryTypeId) {
        this.mId = mId;
        this.orderId = orderId;
        this.custId = custId;
        this.channelId = channelId;
        this.txnAmount = txnAmount;
        this.website = website;
        this.callBackUrl = callBackUrl;
        this.industryTypeId = industryTypeId;

    }

    public String getmId() {
        return mId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustId() {
        return custId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getTxnAmount() {
        return txnAmount;
    }

    public String getWebsite() {
        return website;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public String getIndustryTypeId() {
        return industryTypeId;
    }

    public void setmId() {
        this.mId = PAYTM_M_ID;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public void setChannelId() {
        this.channelId = PAYTM_CHANNEL_ID;
    }

    public void setTxnAmount(String txnAmount) {
        this.txnAmount = txnAmount;
    }

    public void setWebsite() {
        this.website = PAYTM_WEBSITE;
    }

    public void setCallBackUrl() {
        this.callBackUrl = PAYTM_CALLBACK_URL;
    }

    public void setIndustryTypeId() {
        this.industryTypeId = PAYTM_INDUSTRY_TYPE_ID;
    }
}
