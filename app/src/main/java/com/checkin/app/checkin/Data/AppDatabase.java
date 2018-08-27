package com.checkin.app.checkin.Data;

import android.content.Context;

import com.checkin.app.checkin.Profile.ShopProfile.ReviewsItem;
import com.checkin.app.checkin.Profile.ShopProfile.ShopHomeModel;
import com.checkin.app.checkin.MyObjectBox;
import com.checkin.app.checkin.Session.ItemCustomizationField;
import com.checkin.app.checkin.Session.ItemCustomizationGroup;
import com.checkin.app.checkin.Session.MenuGroupModel;
import com.checkin.app.checkin.Session.MenuItemModel;
import com.checkin.app.checkin.Social.Chat;
import com.checkin.app.checkin.Social.Message;
import com.checkin.app.checkin.User.UserModel;

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
                    mBoxStore = MyObjectBox.builder().androidContext(context).buildDefault();
                }
            }
        }
        return mBoxStore;
    }

    private AppDatabase() {
    }

    public static Box<ShopHomeModel> getShopHomeModel(final Context context) {
        return getBoxStore(context).boxFor(ShopHomeModel.class);
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

    public static Box<ReviewsItem> getReviewsModel(Context context){
        return getBoxStore(context).boxFor(ReviewsItem.class);
    }
}
