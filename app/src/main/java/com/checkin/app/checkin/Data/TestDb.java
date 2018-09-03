package com.checkin.app.checkin.Data;

import android.content.Context;
import android.util.Log;

import com.checkin.app.checkin.Home.UserProfileEntity;
import com.checkin.app.checkin.Profile.ShopProfile.ReviewsItem;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Session.ItemCustomizationField;
import com.checkin.app.checkin.Session.ItemCustomizationGroup;
import com.checkin.app.checkin.Session.MenuGroupModel;
import com.checkin.app.checkin.Session.MenuItemModel;
import com.checkin.app.checkin.Shop.ShopModel;
import com.checkin.app.checkin.User.UserModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.objectbox.Box;

import static android.support.constraint.Constraints.TAG;

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
                AppDatabase.getShopModel(context)
        );
        populateReviews(AppDatabase.getReviewsModel(context));
        populateUserProfile(AppDatabase.getUserProfileModel(context));

    }

    private static void populateMenu(
            Box<MenuItemModel> menuItemModel,
            Box<MenuGroupModel> menuGroupModel,
            Box<ItemCustomizationField> itemCustomizationFieldModel,
            Box<ItemCustomizationGroup> itemCustomizationGroupModel,
            Box<ShopModel> shopProfileModel
    ) {
        List<MenuGroupModel> menuGroups = new ArrayList<>(30);
        List<MenuItemModel> menuItems = new ArrayList<>();
        List<ItemCustomizationGroup> itemCustomizationGroups = new ArrayList<>();
        List<ItemCustomizationField> itemCustomizationFields = new ArrayList<>();
        List<ShopModel> shopProfiles = new ArrayList<>();

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
        ShopModel shopProfile = new ShopModel(0L,"Lajjat Hotel","Bio",true,true,"address","city","+8687845140","email","website",true,4.7f);
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

    private static void populateReviews(Box<ReviewsItem> users){
        Log.e(TAG, "populateReviews: Populated");
        String review="Hello I am awesome\nSoAwesome\nSo Awesome\nVery Awesome\nSee for yourself";
        users.put(new ReviewsItem(0,"","","","","",1));
        for(int i=0;i<10;i++)
            users.put(new ReviewsItem(R.drawable.water,"Ishu Darshan"+i,"86 Reviews,332 Folowers","5 days ago","5",review,1));
    }
    private static void populateUserProfile(Box<UserProfileEntity> users){
        users.put(new UserProfileEntity("Ishu Darshan","FootBaller","I am an awesome footballer","data:image/jpeg;base64,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","France",155,32,4.9));

    }
}
