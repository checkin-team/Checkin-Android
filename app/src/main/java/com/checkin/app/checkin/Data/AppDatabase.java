package com.checkin.app.checkin.Data;

import android.content.Context;

import com.checkin.app.checkin.Menu.Model.ItemCustomizationFieldModel;
import com.checkin.app.checkin.Menu.Model.ItemCustomizationGroupModel;
import com.checkin.app.checkin.Menu.Model.MenuGroupModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Menu.Model.MenuModel;
import com.checkin.app.checkin.MyObjectBox;

import javax.inject.Singleton;

import io.objectbox.Box;
import io.objectbox.BoxStore;

@Singleton
public class AppDatabase {
    private static BoxStore mBoxStore;

    private AppDatabase() {
    }

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

    public static Box<ItemCustomizationFieldModel> getMenuItemCustomizationFieldModel(final Context context) {
        return getBoxStore(context).boxFor(ItemCustomizationFieldModel.class);
    }

    public static Box<ItemCustomizationGroupModel> getMenuItemCustomizationGroupModel(final Context context) {
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

    public static void init(final Context context) {
        getBoxStore(context);
    }
}
