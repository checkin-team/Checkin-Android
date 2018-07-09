package com.alcatraz.admin.project_alcatraz.Data;

import com.alcatraz.admin.project_alcatraz.R;
import com.alcatraz.admin.project_alcatraz.Social.Chat;
import com.alcatraz.admin.project_alcatraz.Social.ChatDao;
import com.alcatraz.admin.project_alcatraz.Social.Message;
import com.alcatraz.admin.project_alcatraz.Social.MessageDao;
import com.alcatraz.admin.project_alcatraz.User.User;
import com.alcatraz.admin.project_alcatraz.User.UserDao;

import java.util.Date;

public class TestDb {
    private static final int DELAY_MILLIS = 1000;
    public static void populateWithTestData(AppRoomDatabase db) {
        populateUsers(db.userModel(), db.chatModel());
        populateMessages(db.messageModel(), db.chatModel());
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
