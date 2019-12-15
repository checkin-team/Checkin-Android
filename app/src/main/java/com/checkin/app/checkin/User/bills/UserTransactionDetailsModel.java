package com.checkin.app.checkin.User.bills;

import com.checkin.app.checkin.misc.models.BriefModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.session.model.SessionBillModel;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserTransactionDetailsModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("hash_id")
    private String hashId;

    @JsonProperty("session_time")
    private long sessionTime;

    @JsonProperty("ordered_items")
    private List<SessionOrderedItemModel> orderedItems;

    @JsonProperty("bill")
    private SessionBillModel bill;

    @JsonProperty("host")
    private BriefModel host;

    @JsonProperty("payment_mode")
    private String paymentMode;

    @JsonProperty("checked_out")
    private Date checkedOut;

    public UserTransactionDetailsModel() {
    }

    public long getPk() {
        return pk;
    }

    public long getSessionTime() {
        return sessionTime;
    }

    public List<SessionOrderedItemModel> getOrderedItems() {
        return orderedItems;
    }

    public SessionBillModel getBill() {
        return bill;
    }

    public BriefModel getHost() {
        return host;
    }

    public String formatTotalTime() {
        return Utils.formatTimeDuration(sessionTime);
    }

    public ShopModel.PAYMENT_MODE getPaymentMode() {
        return ShopModel.PAYMENT_MODE.getByTag(paymentMode);
    }

    public void setPaymentMode(String paymentModes) {
        this.paymentMode = paymentModes;
    }

    public String getHashId() {
        return hashId;
    }

    public int getCountOrders() {
        return orderedItems.size();
    }

    public String getFormattedDate() {
        return Utils.formatCompleteDate(checkedOut);
    }
}
