package com.alcatraz.admin.project_alcatraz.Session;

import com.alcatraz.admin.project_alcatraz.User.User;

import java.util.List;

/**
 * Created by Bhavik Patel on 04/08/2018.
 */

public class ActiveSessionModel {
    private int bill;
    private List<OrderedItem> listItemsOrdered;
    private List<User> listUsers;

    public ActiveSessionModel(int bill, List<OrderedItem> listItemsOrdered, List<User> listUsers) {
        this.bill = bill;
        this.listItemsOrdered = listItemsOrdered;
        this.listUsers = listUsers;
    }

    public int getBill() {
        return bill;
    }

    public List<OrderedItem> getListItemsOrdered() {
        return listItemsOrdered;
    }

    public List<User> getListUsers() {
        return listUsers;
    }
}
