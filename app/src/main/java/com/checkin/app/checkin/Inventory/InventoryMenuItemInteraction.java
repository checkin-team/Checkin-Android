package com.checkin.app.checkin.Inventory;

import com.checkin.app.checkin.Inventory.Model.InventoryItemModel;

public interface InventoryMenuItemInteraction {
    boolean onMenuItemAdded(InventoryItemModel item);
    boolean onMenuItemChanged(InventoryItemModel item, int count);
    void onMenuItemShowInfo(InventoryItemModel item);
    int getItemOrderedCount(InventoryItemModel item);
    void onClickItemAvailability(InventoryItemModel mItem, boolean isChecked);
}
