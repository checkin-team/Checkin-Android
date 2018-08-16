package com.alcatraz.admin.project_alcatraz.Session;

import com.alcatraz.admin.project_alcatraz.User.UserModel;

import java.util.List;

/**
 * Created by Bhavik Patel on 04/08/2018.
 */

public class ActiveSessionModel {
    private int bill;
    private List<OrderedItemModel> orderedItems;
    private List<UserModel> users;

    public ActiveSessionModel(int bill, List<OrderedItemModel> orderedItems, List<UserModel> users) {
        this.bill = bill;
        this.orderedItems = orderedItems;
        this.users = users;
    }

    public int getBill() {
        return bill;
    }

    public List<OrderedItemModel> getOrderedItems() {
        return orderedItems;
    }

    public List<UserModel> getUsers() {
        return users;
    }
}
