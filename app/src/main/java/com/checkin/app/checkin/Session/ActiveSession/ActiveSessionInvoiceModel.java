package com.checkin.app.checkin.Session.ActiveSession;

import com.checkin.app.checkin.Menu.Model.OrderedItemModel;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Session.SessionBillModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ActiveSessionInvoiceModel {
    @JsonProperty("pk")
    private String pk;

    @JsonProperty("ordered_items")
    private List<OrderedItemModel> ordered_items;

    @JsonProperty("bill")
    private SessionBillModel bill;

    @JsonProperty("host")
    private BriefModel host;

    public ActiveSessionInvoiceModel(){}

    public ActiveSessionInvoiceModel(String pk, List<OrderedItemModel> ordered_items, SessionBillModel bill, BriefModel host) {
        this.pk = pk;
        this.ordered_items = ordered_items;
        this.bill = bill;
        this.host = host;
    }

    public String getPk() {
        return pk;
    }

    public List<OrderedItemModel> getOrdered_items() {
        return ordered_items;
    }

    public SessionBillModel getBill() {
        return bill;
    }

    public BriefModel getHost() {
        return host;
    }
}
