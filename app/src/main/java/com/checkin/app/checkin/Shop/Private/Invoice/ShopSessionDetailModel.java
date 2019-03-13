package com.checkin.app.checkin.Shop.Private.Invoice;

import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Session.Model.SessionBillModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Utility.Utils;
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
}
