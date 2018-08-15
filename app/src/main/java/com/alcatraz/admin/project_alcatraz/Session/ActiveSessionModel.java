package com.alcatraz.admin.project_alcatraz.Session;

import com.alcatraz.admin.project_alcatraz.User.User;

import java.util.List;

/**
 * Created by Bhavik Patel on 04/08/2018.
 */

public class ActiveSessionModel {
    private int bill;
    private List<OrderedItem> orderedItems;
    private List<User> users;

    public ActiveSessionModel(int bill, List<OrderedItem> orderedItems, List<User> users) {
        this.bill = bill;
        this.orderedItems = orderedItems;
        this.users = users;
    }

    public int getBill() {
        return bill;
    }

    public List<OrderedItem> getOrderedItems() {
        return orderedItems;
    }

    public List<User> getUsers() {
        return users;
    }
}
