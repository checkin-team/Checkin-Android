package com.alcatraz.admin.project_alcatraz.Data;

import android.content.Context;
import android.util.Log;

import com.alcatraz.admin.project_alcatraz.Session.MenuGroup;
import com.alcatraz.admin.project_alcatraz.Session.MenuItem;
import com.alcatraz.admin.project_alcatraz.User.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                Map<String, Double> typeCost = new HashMap<>();
                String baseType;
                if (j % 2 == 0) {
                    typeCost.put("Pint", 300.0);
                    typeCost.put("Can", 400.0);
                    typeCost.put("Beer", 750.0);
                    baseType = "Pint";
                } else {
                    typeCost.put("Default", 460.0);
                    baseType = "Default";
                }
                MenuItem menuItem = new MenuItem("Carlsburg #" + j, typeCost, baseType, i, 1);
                if (i % 2 == 0)
                    menuItem.setSubGroupIndex(j % 2);
                menuItems.add(menuItem);
            }
        }
        menuGroupModel.put(menuGroups);
        menuItemModel.put(menuItems);
    }

    private static void populateUsers(Box<User> userModel) {
        User user1 = new User("Alex");
        User user2 = new User("Alice");

        userModel.put(user1, user2);

        User user3 = new User("Monica");
        User user4 = new User("Jack");
        userModel.put(user3, user4);
    }
}
