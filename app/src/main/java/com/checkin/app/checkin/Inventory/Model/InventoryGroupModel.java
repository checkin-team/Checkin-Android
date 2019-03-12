package com.checkin.app.checkin.Inventory.Model;

import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.annotation.Transient;

public class InventoryGroupModel {

    @JsonProperty("pk")
    private long pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private String category;

    @JsonProperty("type")
    private String type;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("items")
    private List<InventoryItemModel> items;

    private List<InventoryItemModel> vegItems;
    private List<InventoryItemModel> nonVegItems;
    private List<InventoryItemModel> unvailableItems;

    public InventoryGroupModel() {
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<InventoryItemModel> getItems() {
        return items;
    }

    public void setItems(List<InventoryItemModel> items) {
        this.items = items;
    }

    public List<InventoryItemModel> getNonVegItems() {
        if (nonVegItems != null)
            return nonVegItems;
        nonVegItems = new ArrayList<>();
        for (InventoryItemModel item: items) {
            if (!item.isVegetarian())
                nonVegItems.add(item);
        }
        return nonVegItems;
    }

    public List<InventoryItemModel> getVegItems() {
        if (vegItems != null)
            return vegItems;
        vegItems = new ArrayList<>();
        for (InventoryItemModel item: items) {
            if (item.isVegetarian())
                vegItems.add(item);
        }
        return vegItems;
    }

    public List<InventoryItemModel> getUnavailableItems(){
        unvailableItems = new ArrayList<>();
        for (InventoryItemModel item: items) {
            if (!item.isAvailable())
                unvailableItems.add(item);
        }
        if (unvailableItems != null)
            return unvailableItems;
       return unvailableItems;
    }

    public boolean hasSubGroups() {
        return getVegItems().size() > 0 && getNonVegItems().size() > 0;
    }

    public boolean hasUnavailable(){
        return getUnavailableItems().size()>0;
    }

    public boolean groupIsAvailable(){
            for (InventoryItemModel item : items) {
                if (item.isAvailable())
                    return true;
            }
        return false;
    }
}
