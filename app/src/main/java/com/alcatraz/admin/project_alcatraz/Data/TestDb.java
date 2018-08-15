package com.alcatraz.admin.project_alcatraz.Data;

import android.content.Context;
import android.util.Log;

import com.alcatraz.admin.project_alcatraz.Session.ItemCustomizationField;
import com.alcatraz.admin.project_alcatraz.Session.ItemCustomizationGroup;
import com.alcatraz.admin.project_alcatraz.Session.MenuGroup;
import com.alcatraz.admin.project_alcatraz.Session.MenuItem;
import com.alcatraz.admin.project_alcatraz.User.User;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class TestDb {
    private static final int DELAY_MILLIS = 1000;
    public static void populateWithTestData(final Context context) {
        Log.e("TestData", "Populating...");
        populateUsers(AppDatabase.getUserModel(context));
        populateMenu(AppDatabase.getMenuItemCustFieldModel(context),AppDatabase.getMenuItemCustGroupModel(context),AppDatabase.getMenuItemModel(context), AppDatabase.getMenuGroupModel(context));
    }

    private static void populateMenu(Box<ItemCustomizationField> itemCustomizationFieldModel,Box<ItemCustomizationGroup> itemCustomizationGroupModel,Box<MenuItem> menuItemModel, Box<MenuGroup> menuGroupModel) {
        List<MenuGroup> menuGroups = new ArrayList<>(30);
        List<MenuItem> menuItems = new ArrayList<>();
        List<ItemCustomizationGroup> itemCustomizationGroups = new ArrayList<>();
        List<ItemCustomizationField> itemCustomizationFields = new ArrayList<>();


        /*ArrayList<String> subGroups = new ArrayList<>();
        subGroups.add("Veg");
        subGroups.add("Non-Veg");
        menuGroups.add(new MenuGroup( "Group # 1", subGroups, 1));

        List<String> typeName = new ArrayList<>();
        List<Double> typeCost = new ArrayList<>();
        typeName.add("Pint");
        typeCost.add(300.0);
        typeName.add("Can");
        typeCost.add(400.0);
        typeName.add("Beer");
        typeCost.add(750.0);
        int baseType = 0;
        MenuItem menuItem = new MenuItem("Carlsburg # 1", typeName,typeCost, baseType, 1, 1);
        menuItem.setSubGroupIndex(0);
        menuItems.add(menuItem);
        itemCustomizationGroups.add(new ItemCustomizationGroup(0,3,"Extra ",1));
        itemCustomizationFields.add(new ItemCustomizationField(true,"Extra name 1", 23,1));
        itemCustomizationFields.add(new ItemCustomizationField(true,"Extra name 2", 123,1));
        itemCustomizationFields.add(new ItemCustomizationField(true,"Extra name 3", 223,1));
        itemCustomizationFields.add(new ItemCustomizationField(true,"Extra name 4", 323,1));
        itemCustomizationFields.add(new ItemCustomizationField(true,"Extra name 5", 423,1));
        itemCustomizationFields.add(new ItemCustomizationField(true,"Extra name 6", 523,1));*/
        int c1 = 1, c2 = 1;
        for (int i = 1; i <= 30; i++) {
            ArrayList<String> subGroups = null;
            if (i % 2 == 0) {
                subGroups = new ArrayList<>(2);
                subGroups.add("Veg");
                subGroups.add("Non-Veg");
            }
            menuGroups.add(new MenuGroup( "Group #" + i, subGroups, 1));
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
                MenuItem menuItem = new MenuItem(
                        "Carlsburg #" + j, typeName,typeCost, baseType, i, 1);
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

    private static void populateUsers(Box<User> userModel) {
        User user1 = new User("Alex", "");
        User user2 = new User("Alice", "");

        userModel.put(user1, user2);

        User user3 = new User("Monica", "");
        User user4 = new User("Jack", "");
        userModel.put(user3, user4);
    }
}
