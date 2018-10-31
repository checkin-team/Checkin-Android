package com.checkin.app.checkin.Data;

import android.content.Context;

import com.checkin.app.checkin.Menu.ItemCustomizationFieldModel;
import com.checkin.app.checkin.Menu.ItemCustomizationGroupModel;
import com.checkin.app.checkin.Menu.MenuGroupModel;
import com.checkin.app.checkin.Menu.MenuItemModel;
import com.checkin.app.checkin.Menu.MenuModel;
import com.checkin.app.checkin.MyObjectBox;
import com.checkin.app.checkin.Notifications.NotificationModel;

import javax.inject.Singleton;

import io.objectbox.Box;
import io.objectbox.BoxStore;

@Singleton
public class AppDatabase {
    private static BoxStore mBoxStore;

    private static BoxStore getBoxStore(final Context context) {
        if (mBoxStore == null) {
            synchronized (AppDatabase.class) {
                if (mBoxStore == null) {
                    mBoxStore = MyObjectBox.builder().androidContext(context).buildDefault();
                }
            }
        }
        return mBoxStore;
    }

    private AppDatabase() {
    }

    public static Box<NotificationModel> getNotifModel(final Context context) {
        return getBoxStore(context).boxFor(NotificationModel.class);
    }

    /*public static Box<UserModel> getUserModel(final Context context) {
        return getBoxStore(context).boxFor(UserModel.class);
    }*/

    /*public static Box<ShopModel> getShopModel(final Context context) {
        return getBoxStore(context).boxFor(ShopModel.class);
    }*/

    public static Box<ItemCustomizationFieldModel> getItemCustomizationFieldModel(final Context context) {
        return getBoxStore(context).boxFor(ItemCustomizationFieldModel.class);
    }

    public static Box<ItemCustomizationGroupModel> getItemCustomizationGroupModel(final Context context) {
        return getBoxStore(context).boxFor(ItemCustomizationGroupModel.class);
    }

    public static Box<MenuItemModel> getMenuItemModel(final Context context) {
        return getBoxStore(context).boxFor(MenuItemModel.class);
    }

    public static Box<MenuGroupModel> getMenuGroupModel(final Context context) {
        return getBoxStore(context).boxFor(MenuGroupModel.class);
    }

    public static Box<MenuModel> getMenuModel(final Context context) {
        return getBoxStore(context).boxFor(MenuModel.class);
    }
}
