package com.checkin.app.checkin.Shop.Private.Invoice;

import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.Utility.Utils;
import com.checkin.app.checkin.misc.models.BriefModel;
import com.checkin.app.checkin.session.models.SessionBillModel;
import com.checkin.app.checkin.session.models.SessionOrderedItemModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopSessionDetailModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("session_time")
    private long sessionTime;

    @JsonProperty("avg_preparation_time")
    private long avgPreparationTime;

    @JsonProperty("ordered_items")
    private List<SessionOrderedItemModel> orderedItems;

    @JsonProperty("bill")
    private SessionBillModel bill;

    @JsonProperty("host")
    private BriefModel host;

    @JsonProperty("payment_mode")
    private String paymentMode;

    public ShopSessionDetailModel() {
    }

    public long getPk() {
        return pk;
    }

    public long getSessionTime() {
        return sessionTime;
    }

    public long getAvgPreparationTime() {
        return avgPreparationTime;
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

    public String formatPreparationTime() {
        return Utils.formatTimeDuration(avgPreparationTime);
    }

    public ShopModel.PAYMENT_MODE getPaymentMode() {
        return ShopModel.PAYMENT_MODE.getByTag(paymentMode);
    }

    public void setPaymentMode(String paymentModes) {
        this.paymentMode = paymentModes;
    }
}
