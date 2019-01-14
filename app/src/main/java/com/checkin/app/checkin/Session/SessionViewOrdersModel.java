package com.checkin.app.checkin.Session;

import com.checkin.app.checkin.Menu.Model.ItemCustomizationGroupModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Utility.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class SessionViewOrdersModel {

    @JsonProperty("pk")
    private int pk;

    @JsonProperty("item")
    private MenuItemModel item;

    @JsonProperty("cost")
    private double cost;

    @JsonProperty("item_type")
    private String item_type;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("customizations")
    private List<SessionOrdersCustomizationModel> customizations;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("ordered")
    private Date ordered;

    private SESSIONEVENT status;


    public SessionViewOrdersModel(){}

    public SessionViewOrdersModel(int pk, MenuItemModel item, double cost, String item_type, int quantity, List<SessionOrdersCustomizationModel> customizations, String remarks, Date ordered) {
        this.pk = pk;
        this.item = item;
        this.cost = cost;
        this.item_type = item_type;
        this.quantity = quantity;
        this.customizations = customizations;
        this.remarks = remarks;
        this.ordered = ordered;
    }

    public int getPk() {
        return pk;
    }

    public MenuItemModel getItem() {
        return item;
    }

    public double getCost() {
        return cost;
    }

    public String formatCost() {
        return String.valueOf(cost);
    }

    public String getItem_type() {
        return item_type;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<SessionOrdersCustomizationModel> getCustomizations() {
        return customizations;
    }

    public String getRemarks() {
        return remarks;
    }

    public Date getOrdered() {
        return ordered;
    }

    public long getRemainingCancelTime() {
        return Constants.DEFAULT_ORDER_CANCEL_DURATION - ((new Date()).getTime() - ordered.getTime());
    }

    public boolean canCancel() {
        return getRemainingCancelTime() >= 0;
    }

    @JsonProperty("status")
    public void setStatus(int tag) {
        this.status = SESSIONEVENT.getByTag(tag);
    }


    public SESSIONEVENT getStatus() {
        return status;
    }

    public enum SESSIONEVENT {
        NONE(0), OPEN(1), INPROGRESS(5), CANCELLED(9), DONE(10);

        final int tag;

        SESSIONEVENT(int tag) {
            this.tag = tag;
        }//constructor of enum


        public static SESSIONEVENT getByTag(int id) {
            for (SESSIONEVENT type: SESSIONEVENT.values()) {
                if (type.tag == id)
                    return type;
            }
            return NONE;
        }
        /*public static SESSIONEVENT getByTag(int tag) {
            switch (tag) {
                case 0:
                    return NONE;
                case 1:
                    return OPEN;
                case 5:
                    return INPROGRESS;
                case 9:
                    return CANCELLED;
                case 10:
                    return DONE;
            }
            return NONE;
        }*/
    }

}
