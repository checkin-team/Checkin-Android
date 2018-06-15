package com.alcatraz.admin.project_alcatraz.Session;

public class OrderedItem extends MenuItem {
    protected int quantity;
    protected int remainingSeconds;
    protected int typeIndex;

    OrderedItem(MenuItem menuItem) {
        super(menuItem.title, menuItem.types, menuItem.costs);
    }

    static OrderedItem order(MenuItem menuItem, int quantity, int typeIndex) {
        OrderedItem item = new OrderedItem(menuItem);
        item.quantity = quantity;
        item.typeIndex = typeIndex;
        return item;
    }

    public void setRemainingSeconds(int remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }
}
