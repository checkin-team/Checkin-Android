package com.alcatraz.admin.project_alcatraz.Data;

import android.content.Context;

//import com.alcatraz.admin.project_alcatraz.MyObjectBox;
import com.alcatraz.admin.project_alcatraz.Session.ItemCustomizationField;
import com.alcatraz.admin.project_alcatraz.Session.ItemCustomizationGroup;
import com.alcatraz.admin.project_alcatraz.Session.MenuGroupModel;
import com.alcatraz.admin.project_alcatraz.Session.MenuItemModel;
import com.alcatraz.admin.project_alcatraz.Social.Chat;
import com.alcatraz.admin.project_alcatraz.Social.Message;
import com.alcatraz.admin.project_alcatraz.User.UserModel;

import javax.inject.Singleton;

import io.objectbox.Box;
import io.objectbox.BoxStore;

@Singleton
public class AppDatabase {
    private static BoxStore mBoxStore;

    public static BoxStore getBoxStore(final Context context) {
        if (mBoxStore == null) {
            synchronized (AppDatabase.class) {
                if (mBoxStore == null) {
                    mBoxStore = null;//MyObjectBox.builder().androidContext(context).buildDefault();
                }
            }
        }
        return mBoxStore;
    }

    private AppDatabase() {
    }

    public static Box<ItemCustomizationField> getItemCustomizationFieldModel(final Context context) {
        return getBoxStore(context).boxFor(ItemCustomizationField.class);
    }

    public static Box<ItemCustomizationGroup> getItemCustomizationGroupModel(final Context context) {
        return getBoxStore(context).boxFor(ItemCustomizationGroup.class);
    }

    public static Box<MenuItemModel> getMenuItemModel(final Context context) {
        return getBoxStore(context).boxFor(MenuItemModel.class);
    }

    public static Box<MenuGroupModel> getMenuGroupModel(final Context context) {
        return getBoxStore(context).boxFor(MenuGroupModel.class);
    }

    public static Box<UserModel> getUserModel(final Context context) {
        return getBoxStore(context).boxFor(UserModel.class);
    }

    public static Box<Message> getMessageModel(final Context context) {
        return getBoxStore(context).boxFor(Message.class);
    }

    public static Box<Chat> getChatModel(final Context context) {
        return getBoxStore(context).boxFor(Chat.class);
    }
}
