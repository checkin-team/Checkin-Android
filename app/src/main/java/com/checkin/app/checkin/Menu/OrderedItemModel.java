package com.checkin.app.checkin.Menu;

import android.util.Log;

import com.checkin.app.checkin.Utility.Constants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class OrderedItemModel implements Cloneable {
    @JsonIgnore private static final String TAG = OrderedItemModel.class.getSimpleName();
    private long id;
    private MenuItemModel item;
    private Date ordered;
    private int quantity;
    @JsonProperty("type_index") private int typeIndex;
    @JsonProperty("customizations") private List<ItemCustomizationFieldModel> selectedFields;
    @JsonProperty("user_id") private int userId;
    @JsonProperty("session_id") private int sessionId;
    private String remarks;
    @JsonIgnore private int changeCount;

    OrderedItemModel() {}

    OrderedItemModel(MenuItemModel menuItem, int quantity, int type) {
        this.item = menuItem;
        this.quantity = quantity;
        this.typeIndex = type;
    }

    OrderedItemModel(MenuItemModel item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.typeIndex = 0;
        this.id = 0;
        this.changeCount = 1;
    }

    @JsonProperty("cost")
    public double getCost() {
        double base = item.getTypeCost().get(typeIndex);
        double extra = 0;
        if (selectedFields != null) {
            for (ItemCustomizationFieldModel field : selectedFields) {
                extra += field.getCost();
            }
        }
        return (base + extra) * quantity;
    }

    public void setCost(double cost) {
        Log.e(TAG, "Cost: " + cost);
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public String getTypeName() {
        return item.getTypeName().size() > 1 ? item.getTypeName().get(typeIndex) : null;
    }

    public Date getOrdered() {
        return ordered;
    }

    public List<ItemCustomizationFieldModel> getSelectedFields() {
        return selectedFields;
    }

    public void addCustomizationField(ItemCustomizationFieldModel field) {
        if (selectedFields == null)
            selectedFields = new ArrayList<>();
        selectedFields.add(field);
    }

    public void removeCustomizationField(ItemCustomizationFieldModel field) {
        if (selectedFields == null)
            Log.e(TAG, "Trying to remove from empty selected fields.");
        selectedFields.remove(field);
    }

    public void setTypeIndex(int typeIndex) {
        this.typeIndex = typeIndex;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        setChangeCount(quantity - this.quantity);
        this.quantity = Math.abs(quantity);
    }

    public MenuItemModel getItem() {
        return item;
    }

    public boolean canCancel() {
        return getRemainingCancelTime() >= 0;
    }

    public long getRemainingCancelTime() {
        return Constants.DEFAULT_ORDER_CANCEL_DURATION - ((new Date()).getTime() - ordered.getTime());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OrderedItemModel)) {
            return false;
        }
        if (item.equals(((OrderedItemModel) obj).getItem()) && this.typeIndex == ((OrderedItemModel) obj).getTypeIndex()) {
            if (this.selectedFields == null && ((OrderedItemModel) obj).getSelectedFields() == null)
                return true;
            if (this.selectedFields != null && ((OrderedItemModel) obj).getSelectedFields() != null)
                return this.selectedFields.equals(((OrderedItemModel) obj).getSelectedFields());
        }
        return false;
    }

    public long getId() {
        return id;
    }

    public boolean isCustomized() {
        return selectedFields != null && selectedFields.size() > 0;
    }

    public boolean canOrder() {
        if (quantity <= 0 || typeIndex >= item.getTypeName().size())
            return false;
        for (ItemCustomizationGroupModel group: item.getCustomizationGroups()) {
            int count = 0;
            if (selectedFields != null) {
                for (ItemCustomizationFieldModel field : group.getCustomizationFields()) {
                    if (selectedFields.contains(field)) count++;
                }
            }
            if (count < group.getMinSelection() || count > group.getMaxSelection())
                return false;
        }
        return true;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRemarks() {
        return remarks;
    }

    public int getChangeCount() {
        return changeCount;
    }

    public void setChangeCount(int changeCount) {
        this.changeCount = changeCount;
    }

    @Override
    protected OrderedItemModel clone() throws CloneNotSupportedException {
        return ((OrderedItemModel) super.clone());
    }
}
