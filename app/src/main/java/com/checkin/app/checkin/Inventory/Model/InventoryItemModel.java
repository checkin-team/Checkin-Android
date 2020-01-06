package com.checkin.app.checkin.Inventory.Model;

import com.checkin.app.checkin.menu.models.MenuItemModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryItemModel extends MenuItemModel {

    @JsonProperty("customizations")
    private List<InventoryItemCustomizationGroupModel> customizations;

    @JsonProperty("is_available")
    private boolean isAvailable;

    public InventoryItemModel() {
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @JsonProperty("customizations")
    public void setInventoryCustomizationGroups(List<InventoryItemCustomizationGroupModel> customizationGroups) {
    }
}
