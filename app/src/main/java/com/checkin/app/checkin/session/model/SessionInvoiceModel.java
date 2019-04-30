package com.checkin.app.checkin.session.model;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionInvoiceModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("ordered_items")
    private List<SessionOrderedItemModel> orderedItems;

    @JsonProperty("bill")
    private SessionBillModel bill;

    @JsonProperty("host")
    private BriefModel host;

    public SessionInvoiceModel() {
    }

    public long getPk() {
        return pk;
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
}
