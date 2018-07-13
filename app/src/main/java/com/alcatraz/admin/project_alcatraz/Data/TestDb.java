package com.alcatraz.admin.project_alcatraz.Data;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Session.MenuDao;
import com.alcatraz.admin.project_alcatraz.Session.MenuGroup;
import com.alcatraz.admin.project_alcatraz.Session.MenuItem;
import com.alcatraz.admin.project_alcatraz.Social.Chat;
import com.alcatraz.admin.project_alcatraz.Social.ChatDao;
import com.alcatraz.admin.project_alcatraz.Social.Message;
import com.alcatraz.admin.project_alcatraz.Social.MessageDao;
import com.alcatraz.admin.project_alcatraz.User.User;
import com.alcatraz.admin.project_alcatraz.User.UserDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TestDb {
    private static final int DELAY_MILLIS = 1000;
    public static void populateWithTestData(AppRoomDatabase db) {
        populateUsers(db.userModel(), db.chatModel());
        populateMessages(db.messageModel(), db.chatModel());
        populateMenu(db.menuModel());
    }

    private static void populateMenu(MenuDao menuModel) {
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
                ArrayList<Float> costs;
                ArrayList<String> types;
                if (j % 2 == 0) {
                    costs = new ArrayList<>(3);
                    costs.add(300f);
                    costs.add(500f);
                    costs.add(750f);
                    types = new ArrayList<>(3);
                    types.add("Pint");
                    types.add("Can");
                    types.add("Beer");
                } else {
                    costs = new ArrayList<>(1);
                    costs.add(460f);
                    types = new ArrayList<>(1);
                    types.add("Default");
                }
                MenuItem menuItem = new MenuItem("Carlsburg #" + j, types, costs, i, 1);
                if (i % 2 == 0)
                    menuItem.setSubGroupIndex(j % 2);
                menuItems.add(menuItem);
            }
        }
        menuModel.insertGroups(menuGroups.toArray(new MenuGroup[0]));
        menuModel.insertItems(menuItems.toArray(new MenuItem[0]));
    }

    private static void populateUsers(UserDao userModel, ChatDao chatModel) {
        User user0 = new User(0, "You");
//        user0.setImageUrl(R.drawable.profile);
        User user1 = new User(1, "Alex");
//        user1.setImageUrl(R.drawable.dummy_alex);
        User user2 = new User(2, "Alice");
//        user2.setImageUrl(R.drawable.flier);

        userModel.insertAll(user0, user1, user2);

        User user3 = new User(3, "Monica");
//        user3.setImageUrl(R.drawable.dummy_monica);
        User user4 = new User(4, "Jack");
//        user4.setImageUrl(R.drawable.profile);
        userModel.insertAll(user3, user4);
        chatModel.insertAll(new Chat(1), new Chat(2), new Chat(3), new Chat(4));
    }

    private static void populateMessages(MessageDao messageModel, ChatDao chatModel) {
        Message msg1 = new Message("Hiya!", new Date(), 0, 2);
        Message msg2 = new Message("Hello to you too!", new Date(), 2, 0);
        Message msg3 = new Message("There?", new Date(), 3, 0);
        try {
            Thread.sleep(DELAY_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Message msg4 = new Message("Damn!", new Date(), 1, 0);
        Message msg5 = new Message("I'm getting bored.", new Date(), 1, 0);
        messageModel.insertAll(msg1, msg2, msg3, msg4, msg5);
    }
}
