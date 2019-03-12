package com.checkin.app.checkin.Inventory.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class InventoryItemCustomizationGroupModel {

    @JsonProperty("pk")
    private long pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("min_select")
    private int minSelection = 0;

    @JsonProperty("max_select")
    private int maxSelection = 1;

    @JsonProperty("fields")
    private List<InventoryItemCustomizationFieldModel> fields;

    public InventoryItemCustomizationGroupModel() {
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

    public int getMinSelection() {
        return minSelection;
    }

    public void setMinSelection(int minSelection) {
        this.minSelection = minSelection;
    }

    public int getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(int maxSelection) {
        this.maxSelection = maxSelection;
    }

    public List<InventoryItemCustomizationFieldModel> getFields() {
        return fields;
    }

    public void setFields(List<InventoryItemCustomizationFieldModel> fields) {
        this.fields = fields;
    }
}
