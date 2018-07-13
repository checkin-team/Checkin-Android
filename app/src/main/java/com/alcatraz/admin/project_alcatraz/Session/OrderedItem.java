package com.alcatraz.admin.project_alcatraz.Session;

public class OrderedItem extends MenuItem {
    private int id;
    private int quantity;
    private int remainingSeconds;
    private int typeIndex;

    OrderedItem(MenuItem menuItem) {
        super(menuItem.name, menuItem.types, menuItem.costs, menuItem.getGroupId(), menuItem.getMenuId());
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

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public float getPrice() {
        return costs.get(typeIndex) * quantity;
    }

    public String getType() {
        return types.get(typeIndex);
    }

    public int getCount() {
        return quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
