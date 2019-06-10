package com.checkin.app.checkin.User.bills;

import androidx.annotation.DrawableRes;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserTransactionBriefModel {

    @JsonProperty("hash_id")
    private String hashId;

    @JsonProperty("restaurant")
    private BriefModel restaurant;

    @JsonProperty("total")
    private Double total;

    @JsonProperty("savings")
    private Double savings;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("table")
    private String table;

    @JsonProperty("checked_in")
    private Date checkedIn;

    @JsonProperty("checked_out")
    private Date checkedOut;

    @JsonProperty("payment_mode")
    private String paymentMode;

    public UserTransactionBriefModel() {
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

    @DrawableRes
    public static int getFeedbackEmoji(int rate) {
        switch (rate) {
            case 1:
                return R.drawable.ic_emoji_angry_yellow;
            case 2:
                return R.drawable.ic_emoji_sad_yellow;
            case 3:
                return R.drawable.ic_emoji_confused_yellow;
            case 4:
                return R.drawable.ic_emoji_smiling_yellow;
            case 5:
                return R.drawable.ic_emoji_in_love_yellow;
            default:
                return R.drawable.ic_emoji_in_love_yellow;
        }
    }


    public static String getFeedbackText(int rate) {
        switch (rate) {
            case 1:
                return "Disappointing!";
            case 2:
                return "Unpleasant!";
            case 3:
                return "Satisfactory!";
            case 4:
                return "Pleasant!";
            case 5:
                return "Awesome!";
            default:
                return "Awesome!";
        }
    }

    public static String getFeedbackTextWithoutExclamatory(int rate) {
        switch (rate) {
            case 1:
                return "Disappointing";
            case 2:
                return "Unpleasant";
            case 3:
                return "Satisfactory";
            case 4:
                return "Pleasant";
            case 5:
                return "Awesome";
            default:
                return "Awesome";
        }
    }
}
