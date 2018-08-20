package com.checkin.app.checkin.Session;

import com.checkin.app.checkin.Utility.Constants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class OrderedItemModel {
    private long id;
    private MenuItemModel item;
    private Date ordered;
    private int quantity;
    @JsonProperty("type_index") private int typeIndex;
    @JsonProperty("customizations") private List<ItemCustomizationField> selectedFields;
    @JsonProperty("user_id") private int userId;
    @JsonProperty("session_id") private int sessionId;
    private double cost;
    private String remarks;

    OrderedItemModel() {}

    OrderedItemModel(MenuItemModel menuItem, int quantity, int type) {
        this.item = menuItem;
        this.quantity = quantity;
        this.typeIndex = type;
    }

    public double getCost() {
        return cost;
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public int getCount() {
        return quantity;
    }

    public MenuItemModel getItem() {
        return item;
    }

    public Date getOrdered() {
        return ordered;
    }

    public boolean canCancel() {
        return getRemainingCancelTime() >= 0;
    }

    public long getRemainingCancelTime() {
        return Constants.DEFAULT_ORDER_CANCEL_DURATION - ((new Date()).getTime() - ordered.getTime());
    }

    public void setOrdered(Date ordered) {
        this.ordered = ordered;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof OrderedItemModel && ((OrderedItemModel) obj).getItem().getId() == this.getItem().getId();
    }

    public long getId() {
        return id;
    }
}
