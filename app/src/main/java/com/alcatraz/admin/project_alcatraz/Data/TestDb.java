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
        populateMenu(AppDatabase.getMenuItemModel(context), AppDatabase.getMenuGroupModel(context));
    }

    private static void populateMenu(Box<MenuItem> menuItemModel, Box<MenuGroup> menuGroupModel) {
        List<MenuGroup> menuGroups = new ArrayList<>(30);
        List<MenuItem> menuItems = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            ArrayList<String> subGroups = null;
            if (i % 2 == 0) {
                subGroups = new ArrayList<>(2);
                subGroups.add("Veg");
                subGroups.add("Non-Veg");
            }
            menuGroups.add(new MenuGroup( "Group #" + i, subGroups, 1));
            for (int j = 0; j <= i/2; j++) {
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
                }
                MenuItem menuItem = new MenuItem(
                        "Carlsburg #" + j, typeName,typeCost, baseType, i, 1,getItemCustomizationGroup());
                if (i % 2 == 0)
                    menuItem.setSubGroupIndex(j % 2);
                menuItems.add(menuItem);
            }
        }
        menuGroupModel.put(menuGroups);
        menuItemModel.put(menuItems);
    }

    public static List<ItemCustomizationGroup> getItemCustomizationGroup(){
        List<ItemCustomizationGroup> foodExtraList = new ArrayList<>();
        foodExtraList.add(new ItemCustomizationGroup(0,3, getItemCustomizationField(),"Extra 1"));
        foodExtraList.add(new ItemCustomizationGroup(3,5, getItemCustomizationField(),"Extra 2"));
        foodExtraList.add(new ItemCustomizationGroup(0,1, getItemCustomizationField(),"Extra 3"));
        foodExtraList.add(new ItemCustomizationGroup(1,1, getItemCustomizationField(),"Extra 4"));
        foodExtraList.add(new ItemCustomizationGroup(3,3, getItemCustomizationField(),"Extra 5"));
        return foodExtraList;
    }
    public static List<ItemCustomizationField> getItemCustomizationField(){
        List<ItemCustomizationField> extraList = new ArrayList<>();
        extraList.add(new ItemCustomizationField(true,"Extra name 1",45));
        extraList.add(new ItemCustomizationField(true,"Extra name 2",44));
        extraList.add(new ItemCustomizationField(true,"Extra name 3",455));
        extraList.add(new ItemCustomizationField(true,"Extra name 4",456));
        extraList.add(new ItemCustomizationField(true,"Extra name 5",455));
        extraList.add(new ItemCustomizationField(true,"Extra name 6",5));
        extraList.add(new ItemCustomizationField(true,"Extra name 7",345));
        extraList.add(new ItemCustomizationField(true,"Extra name 8",465));
        extraList.add(new ItemCustomizationField(true,"Extra name 9",4524));
        extraList.add(new ItemCustomizationField(true,"Extra name 10",445));
        return extraList;
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
