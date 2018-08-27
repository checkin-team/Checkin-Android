package com.checkin.app.checkin.Session;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Bhavik Patel on 04/08/2018.
 */

public class ActiveSessionModel {
    private double bill;
    @JsonProperty("orders") private List<OrderedItemModel> orderedItems;
    @JsonProperty("customers") private List<ActiveSessionMemberModel> members;

    public ActiveSessionModel() {}

    public ActiveSessionModel(int bill, List<OrderedItemModel> orderedItems, List<ActiveSessionMemberModel> members) {
        this.bill = bill;
        this.orderedItems = orderedItems;
        this.members = members;
    }

    public double getBill() {
        return bill;
    }

    public List<OrderedItemModel> getOrderedItems() {
        return orderedItems;
    }

    public List<ActiveSessionMemberModel> getMembers() {
        return members;
    }
}
