package com.checkin.app.checkin.session.model;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

import androidx.annotation.DrawableRes;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionSuccessfulTransactionModel {

    @JsonProperty("hash_id")
    private String hashId;

    @JsonProperty("restaurant")
    private BriefModel restaurant;

    @JsonProperty("total")
    private Double total;

    @JsonProperty("savings")
    private Double savings;

    @JsonProperty("transaction_id")
    public String transactionId;

    @JsonProperty("table")
    private String table;

    @JsonProperty("checked_in")
    private Date checkedIn;

    @JsonProperty("checked_out")
    private Date checkedOut;

    @JsonProperty("payment_mode")
    private String paymentMode;

    public SessionSuccessfulTransactionModel() {
    }

    public String getHashId() {
        return hashId;
    }

    public BriefModel getRestaurant() {
        return restaurant;
    }

    public Double getTotal() {
        return total;
    }

    public Double getSavings() {
        return savings;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getTable() {
        return table;
    }

    public Date getCheckedIn() {
        return checkedIn;
    }

    public Date getCheckedOut() {
        return checkedOut;
    }

    public ShopModel.PAYMENT_MODE getPaymentMode() {
        return ShopModel.PAYMENT_MODE.getByTag(paymentMode);
    }

    public String formatTotal() {
        return String.valueOf(total);
    }

    public String getFormattedDate() {
        return Utils.formatCompleteDate(checkedOut);
    }

    @DrawableRes
    public static int getPaymentModeIcon(ShopModel.PAYMENT_MODE paymentMode) {
        switch (paymentMode) {
            case CASH:
                return R.drawable.ic_cash_white_red;
            case PAYTM:
                return R.drawable.ic_paytm_logo;
            default:
                return R.drawable.ic_cash_white_red;
        }
    }

}
