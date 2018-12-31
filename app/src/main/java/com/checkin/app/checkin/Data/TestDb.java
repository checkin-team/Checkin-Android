package com.checkin.app.checkin.Data;

import android.content.Context;
import android.util.Log;

import com.checkin.app.checkin.Notifications.NotificationModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.objectbox.Box;

public class TestDb {
    private static final int DELAY_MILLIS = 1000;
    public static void populateWithTestData(final Context context) {
        Log.e("TestData", "Populating...");
//        AppDatabase.getMenuModel(context).put(new MenuModel(1, "Menu"));
//        populateMenu(
//                AppDatabase.getMenuItemModel(context),
//                AppDatabase.getMenuGroupModel(context),
//                AppDatabase.getItemCustomizationFieldModel(context),
//                AppDatabase.getItemCustomizationGroupModel(context)
//        );
        //populateNotif(AppDatabase.getNotifModel(context));
    }

    private static void populateNotif(Box<NotificationModel> notifMode){
        List<NotificationModel> notifs = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            notifs.add(new NotificationModel("Message " + i,new Date(System.currentTimeMillis()- TimeUnit.MILLISECONDS.convert(1*7,TimeUnit.HOURS)),"profile url","action url",i%5==0 ,i));
        }
        notifMode.put(notifs);
    }

/*    private static void populateMenu(
            Box<MenuItemModel> menuItemModel,
            Box<MenuGroupModel> menuGroupModel,
            Box<ItemCustomizationFieldModel> itemCustomizationFieldModel,
            Box<ItemCustomizationGroupModel> itemCustomizationGroupModel
    ) {
        List<MenuGroupModel> menuGroups = new ArrayList<>(30);
        List<MenuItemModel> menuItems = new ArrayList<>();
        List<ItemCustomizationGroupModel> itemCustomizationGroups = new ArrayList<>();
        List<ItemCustomizationFieldModel> itemCustomizationFields = new ArrayList<>();

        int c1 = 1, c2 = 1;
        for (int i = 1; i <= 30; i++) {
            ArrayList<String> subGroups = null;
            if (i % 2 == 0) {
                subGroups = new ArrayList<>(2);
                subGroups.add("Veg");
                subGroups.add("Non-Veg");
            }
            MenuGroupModel menuGroup = new MenuGroupModel( "Group #" + i, subGroups, "Category #" + i%6);
            menuGroup.setMenuId(1);
            menuGroups.add(menuGroup);
            for (int j = 1; j <= i/2+1; j++) {
                List<String> typeName = new ArrayList<>();
                List<Double> typeCost = new ArrayList<>();
                if (j % 3 == 0) {
                    typeName.add("Pint");
                    typeCost.add(300.0);
                    typeName.add("Can");
                    typeCost.add(400.0);
                    typeName.add("Beer");
                    typeCost.add(750.0);
                } else if (j % 3 == 1) {
                    typeName.add("Pint");
                    typeCost.add(300.0);
                    typeName.add("Can");
                    typeCost.add(400.0);
                }
                else {
                    typeName.add("Default");
                    typeCost.add(460.0);
                }
                if (j % 2 == 0) {
                    for (int k = 1; k < 6; k++) {
                        ItemCustomizationGroupModel itemCustomizationGroup = new ItemCustomizationGroupModel((k % 2) * 3, k + 2, "Extra #" + k, c1);
                        c2++;
                        itemCustomizationGroups.add(itemCustomizationGroup);
                        for (int l = 1; l <= 5; l++) {
                            ItemCustomizationFieldModel itemCustomizationField = new ItemCustomizationFieldModel((l & 1) == 0, "Extra name #" + l, l * 20, c2);
                            itemCustomizationFields.add(itemCustomizationField);
                        }
                    }
                }
                MenuItemModel menuItem = new MenuItemModel(j%2 == 0 ? "Carlsburg #" + c1 : "White Bread #" + c1, typeName, typeCost, i, 1, j % 3 == 0, j%2==0,true );
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
    }*/
}
