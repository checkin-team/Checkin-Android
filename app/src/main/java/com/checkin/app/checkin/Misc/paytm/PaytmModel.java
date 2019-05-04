package com.checkin.app.checkin.Misc.paytm;

import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.pgsdk.PaytmOrder;

import java.util.HashMap;

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

    public PaytmModel() {
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return custId;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getIndustryTypeId() {
        return industryTypeId;
    }

    public String getWebsite() {
        return website;
    }

    public String getAmount() {
        return amount;
    }

    public String getChecksumHash() {
        return checksumHash;
    }

    public String getCallbackURL() {
        return PAYTM_CALLBACK_URL + getOrderId();
    }

    public PaytmOrder getPaytmOrder() {
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", getMerchantId());
        paramMap.put("ORDER_ID", getOrderId());
        paramMap.put("CUST_ID", getCustomerId());
        if (!TextUtils.isEmpty(phone)) paramMap.put("MOBILE_NO", getPhone());
        if (!TextUtils.isEmpty(phone)) paramMap.put("EMAIL", getEmail());
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("TXN_AMOUNT", getAmount());
        paramMap.put("WEBSITE", getWebsite());
        paramMap.put("INDUSTRY_TYPE_ID", getIndustryTypeId());
        paramMap.put("CALLBACK_URL", getCallbackURL());
        paramMap.put("CHECKSUMHASH", getChecksumHash());

        Log.e("PayTm", paramMap.toString());
        return new PaytmOrder(paramMap);
    }
}
