package com.checkin.app.checkin.Data;

import android.content.Context;
import android.util.Log;

import com.checkin.app.checkin.Profile.ShopProfile.ShopHomeModel;
import com.checkin.app.checkin.Session.ItemCustomizationField;
import com.checkin.app.checkin.Session.ItemCustomizationGroup;
import com.checkin.app.checkin.Session.MenuGroupModel;
import com.checkin.app.checkin.Session.MenuItemModel;
import com.checkin.app.checkin.User.UserModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.objectbox.Box;

public class TestDb {
    private static final int DELAY_MILLIS = 1000;
    public static void populateWithTestData(final Context context) {
        Log.e("TestData", "Populating...");
        populateUsers(AppDatabase.getUserModel(context));
        populateMenu(
                AppDatabase.getMenuItemModel(context),
                AppDatabase.getMenuGroupModel(context),
                AppDatabase.getItemCustomizationFieldModel(context),
                AppDatabase.getItemCustomizationGroupModel(context),
                AppDatabase.getShopHomeModel(context)
        );
    }

    private static void populateMenu(
            Box<MenuItemModel> menuItemModel,
            Box<MenuGroupModel> menuGroupModel,
            Box<ItemCustomizationField> itemCustomizationFieldModel,
            Box<ItemCustomizationGroup> itemCustomizationGroupModel,
            Box<ShopHomeModel> shopProfileModel
    ) {
        List<MenuGroupModel> menuGroups = new ArrayList<>(30);
        List<MenuItemModel> menuItems = new ArrayList<>();
        List<ItemCustomizationGroup> itemCustomizationGroups = new ArrayList<>();
        List<ItemCustomizationField> itemCustomizationFields = new ArrayList<>();
        List<ShopHomeModel> shopProfiles = new ArrayList<>();

        int c1 = 1, c2 = 1;
        for (int i = 1; i <= 30; i++) {
            ArrayList<String> subGroups = null;
            if (i % 2 == 0) {
                subGroups = new ArrayList<>(2);
                subGroups.add("Veg");
                subGroups.add("Non-Veg");
            }
            menuGroups.add(new MenuGroupModel( "Group #" + i, subGroups, 1));
            for (int j = 1; j <= i/2+1; j++) {
                List<String> typeName = new ArrayList<>();
                List<Double> typeCost = new ArrayList<>();
                int baseType;
                if (j % 2 == 0) {
                    typeName.add("Pint");
                    typeCost.add(300.0);
                    typeName.add("Can");
                    typeCost.add(400.0);
                    typeName.add("Beer");
                    typeCost.add(750.0);
                } else {
                    typeName.add("Default");
                    typeCost.add(460.0);

                    for (int k = 1; k < 6; k++) {
                        ItemCustomizationGroup itemCustomizationGroup = new ItemCustomizationGroup((k%2)*3,k+2,"Extra #" + k, c1);
                        c2++;
                        itemCustomizationGroups.add(itemCustomizationGroup);
                        for(int l = 1; l <= 7; l++){
                            ItemCustomizationField itemCustomizationField = new ItemCustomizationField((l&1) == 0,"Extra name #" + l, l * 20, c2);
                            itemCustomizationFields.add(itemCustomizationField);
                        }
                    }
                }
                MenuItemModel menuItem = new MenuItemModel("Carlsburg #" + j, typeName, typeCost, i, 1);
                menuItem.setDescription("\"It is a 5% abv pilsner beer with a global distribution to 140 mark" +
                        "ets. It was first brewed in 1904, and was created by Carl Jacobsen, son of Carlsberg's founder JC Jacobsen.");
                menuItem.setAvailableMeals(Arrays.asList("brkfst", "lunch", "dinner"));
                menuItem.setIngredients(Arrays.asList("onion", "bread"));
                c1++;

                if (i % 2 == 0)
                    menuItem.setSubGroupIndex(j % 2);
                menuItems.add(menuItem);
            }
        }
        menuGroupModel.put(menuGroups);
        menuItemModel.put(menuItems);
        itemCustomizationGroupModel.put(itemCustomizationGroups);
        itemCustomizationFieldModel.put(itemCustomizationFields);

        //shopProfiles
        ShopHomeModel shopProfile = new ShopHomeModel(0,"Lajjat Hotel","Bio",true,true,"address","city","+8687845140","email","website",true,4.7f);
        shopProfiles.add(shopProfile);
        shopProfileModel.put(shopProfiles);
    }

    private static void populateUsers(Box<UserModel> userModel) {
        UserModel user1 = new UserModel("Alex", "https://78.media.tumblr.com/fc66ed1ceef891684aae8eb2acb152a3/tumblr_oyg8f1Qc1n1vy2tgqo1_400.jpg");
        UserModel user2 = new UserModel("Alice", "https://vignette.wikia.nocookie.net/typemoon/images/c/c1/Archer_EMIYA.jpeg/revision/latest?cb=20150630040614");

        userModel.put(user1, user2);

        UserModel user3 = new UserModel("Monica", "https://vignette.wikia.nocookie.net/typemoon/images/c/c1/Archer_EMIYA.jpeg/revision/latest?cb=20150630040614");
        UserModel user4 = new UserModel("Jack", "https://78.media.tumblr.com/fc66ed1ceef891684aae8eb2acb152a3/tumblr_oyg8f1Qc1n1vy2tgqo1_400.jpg");
        userModel.put(user3, user4);
    }
}
