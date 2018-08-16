package com.alcatraz.admin.project_alcatraz.Data;

import android.content.Context;
import android.util.Log;

import com.alcatraz.admin.project_alcatraz.Session.ItemCustomizationField;
import com.alcatraz.admin.project_alcatraz.Session.ItemCustomizationGroup;
import com.alcatraz.admin.project_alcatraz.Session.MenuGroupModel;
import com.alcatraz.admin.project_alcatraz.Session.MenuItemModel;
import com.alcatraz.admin.project_alcatraz.User.UserModel;

import java.util.ArrayList;
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
                AppDatabase.getItemCustomizationGroupModel(context)
        );
    }

    private static void populateMenu(
            Box<MenuItemModel> menuItemModel,
            Box<MenuGroupModel> menuGroupModel,
            Box<ItemCustomizationField> itemCustomizationFieldModel,
            Box<ItemCustomizationGroup> itemCustomizationGroupModel
    ) {
        List<MenuGroupModel> menuGroups = new ArrayList<>(30);
        List<MenuItemModel> menuItems = new ArrayList<>();
        List<ItemCustomizationGroup> itemCustomizationGroups = new ArrayList<>();
        List<ItemCustomizationField> itemCustomizationFields = new ArrayList<>();

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
                    baseType = 0;
                } else {
                    typeName.add("Default");
                    typeCost.add(460.0);
                    baseType = 0;

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
                MenuItemModel menuItem = new MenuItemModel("Carlsburg #" + j, typeName, typeCost, baseType, i, 1);
                menuItem.setDescription("\"It is a 5% abv pilsner beer with a global distribution to 140 mark" +
                        "ets. It was first brewed in 1904, and was created by Carl Jacobsen, son of Carlsberg's founder JC Jacobsen.");
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
    }

    private static void populateUsers(Box<UserModel> userModel) {
        UserModel user1 = new UserModel("Alex", "");
        UserModel user2 = new UserModel("Alice", "");

        userModel.put(user1, user2);

        UserModel user3 = new UserModel("Monica", "");
        UserModel user4 = new UserModel("Jack", "");
        userModel.put(user3, user4);
    }
}
