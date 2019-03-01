package com.checkin.app.checkin.Menu.Model;

import android.util.Log;

import com.checkin.app.checkin.Utility.Constants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class OrderedItemModel implements Cloneable {
    private static final String TAG = OrderedItemModel.class.getSimpleName();

    @JsonProperty("pk")
    private long pk;

    @JsonProperty("item")
    private MenuItemModel item;

    private double cost;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("type_index")
    private int typeIndex;

    @JsonProperty("customizations")
    private List<ItemCustomizationFieldModel> selectedFields;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("ordered")
    private Date ordered;

    private int changeCount;

    OrderedItemModel() {
    }

    public OrderedItemModel(MenuItemModel menuItem, int quantity, int type) {
        this.item = menuItem;
        this.quantity = quantity;
        this.typeIndex = type;
    }

    public OrderedItemModel(MenuItemModel item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.typeIndex = 0;
        this.changeCount = 1;
    }

    public double getCost() {
        updateCost();
        return this.cost;
    }

    public void updateCost() {
        double base = item.getTypeCosts().get(typeIndex);
        double extra = 0;
        if (selectedFields != null) {
            for (ItemCustomizationFieldModel field : selectedFields) {
                extra += field.getCost();
            }
        }
        this.cost = (base + extra) * quantity;
    }

    @JsonProperty("cost")
    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public String getTypeName() {
        return item.getTypeNames().size() > 1 ? item.getTypeNames().get(typeIndex) : null;
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
        if (selectedFields == null || field == null)
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

    public MenuItemModel getItemModel() {
        return item;
    }

    @JsonProperty("customizations")
    public List<Long> getCustomizations() {
        if (selectedFields == null)
            return new ArrayList<>();
        List<Long> result = new ArrayList<>();
        for (ItemCustomizationFieldModel field: this.selectedFields)
            result.add(field.getPk());
        return result;
    }

    @JsonProperty("item")
    public long getItem() {
        return item.getPk();
    }

    public boolean canCancel() {
        return getRemainingCancelTime() >= 0;
    }

    public long getRemainingCancelTime() {
        return Constants.DEFAULT_ORDER_CANCEL_DURATION - ((new Date()).getTime() - ordered.getTime());
    }

    public long getPk() {
        return pk;
    }

    public boolean isCustomized() {
        return selectedFields != null && selectedFields.size() > 0;
    }

    public boolean canOrder() {
        if (quantity <= 0 || typeIndex >= item.getTypeNames().size())
            return false;
        for (ItemCustomizationGroupModel group : item.getCustomizations()) {
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
    public boolean equals(Object obj) {
        if (!(obj instanceof OrderedItemModel)) {
            return false;
        }
        if (item.equals(((OrderedItemModel) obj).getItemModel()) && this.typeIndex == ((OrderedItemModel) obj).getTypeIndex()) {
            if (this.selectedFields == null && ((OrderedItemModel) obj).getSelectedFields() == null)
                return true;
            if (this.selectedFields != null && ((OrderedItemModel) obj).getSelectedFields() != null)
                return this.selectedFields.equals(((OrderedItemModel) obj).getSelectedFields());
        }
        return false;
    }

    @Override
    public OrderedItemModel clone() throws CloneNotSupportedException {
        return ((OrderedItemModel) super.clone());
    }
}
