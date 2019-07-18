package com.checkin.app.checkin.Menu;

import androidx.annotation.NonNull;

import com.checkin.app.checkin.Menu.Model.MenuItemModel;

public interface MenuItemInteraction {
    boolean onMenuItemAdded(@NonNull MenuItemModel item);

    boolean onMenuItemChanged(@NonNull MenuItemModel item, int count);

    void onMenuItemShowInfo(@NonNull MenuItemModel item);

    int getItemOrderedCount(@NonNull MenuItemModel item);
}