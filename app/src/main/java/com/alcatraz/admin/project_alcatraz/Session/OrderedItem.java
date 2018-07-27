package com.alcatraz.admin.project_alcatraz.Session;

import android.util.Log;

public class OrderedItem {
    private MenuItem item;
    private int quantity;
    private int remainingSeconds;
    private String type;

    OrderedItem(MenuItem menuItem, int quantity, String type) {
        this.item = menuItem;
        this.quantity = quantity;
        this.type = type;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public double getPrice() {
        double price = 0;
        if (item.getTypeCost().containsKey(type)) {
            price = item.getTypeCost().get(type);
        } else {
            Log.e("OrderedItem", "Key invalid");
        }
        return price * quantity;
    }

    public String getType() {
        return type;
    }

    public int getCount() {
        return quantity;
    }

    public MenuItem getItem() {
        return item;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof OrderedItem && ((OrderedItem) obj).getItem().getId() == this.getItem().getId();
    }
}
