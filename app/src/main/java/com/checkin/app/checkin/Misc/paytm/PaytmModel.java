package com.checkin.app.checkin.Misc.paytm;

import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.pgsdk.PaytmOrder;

import java.util.HashMap;

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

    @JsonProperty("callback_url")
    String callbackUrl;

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
        return callbackUrl;
    }

    public PaytmOrder getPaytmOrder() {
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", getMerchantId());
        paramMap.put("ORDER_ID", getOrderId());
        paramMap.put("CUST_ID", getCustomerId());
        if (!TextUtils.isEmpty(phone)) paramMap.put("MOBILE_NO", getPhone());
        if (!TextUtils.isEmpty(email)) paramMap.put("EMAIL", getEmail());
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("TXN_AMOUNT", getAmount());
        paramMap.put("WEBSITE", getWebsite());
        paramMap.put("INDUSTRY_TYPE_ID", getIndustryTypeId());
        paramMap.put("CALLBACK_URL", getCallbackURL());
        paramMap.put("CHECKSUMHASH", getChecksumHash());

        Log.e("PayTm", paramMap.toString());
        return new PaytmOrder(paramMap);
    }

    /*
    ** For Testing Purposes.
    public PaytmOrder getPaytmOrder() {
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", "mUsdOB28426623954609");
        paramMap.put("ORDER_ID", "20");
        paramMap.put("CUST_ID", "1");
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("TXN_AMOUNT", "95.00");
        paramMap.put("WEBSITE", "APPSTAGING");
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap.put("CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=20");
        paramMap.put("CHECKSUMHASH", "NaKZ3o9zBu8lk7WnG3TwE67gam29IxC06/Y+PKnm6MVoo6QmPQtTttEv8/CiLMnJgMBqZT1/rSb1oSowv7hdx+YxuNBGl0oxwVXF4v+2HlA=");

        Log.e("PayTm", paramMap.toString());
        return new PaytmOrder(paramMap);
    }
    */
}
