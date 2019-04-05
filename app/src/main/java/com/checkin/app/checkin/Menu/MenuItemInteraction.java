package com.checkin.app.checkin.Menu;

import com.checkin.app.checkin.Menu.Model.MenuItemModel;

public interface MenuItemInteraction {
    boolean onMenuItemAdded(MenuItemModel item);

    boolean onMenuItemChanged(MenuItemModel item, int count);

    void onMenuItemShowInfo(MenuItemModel item);

    int getItemOrderedCount(MenuItemModel item);
}