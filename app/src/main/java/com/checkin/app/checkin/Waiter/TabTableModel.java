package com.checkin.app.checkin.Waiter;

public class TabTableModel {
    private static final String TAB_TITLE = "TABLE";

    public TabTableModel(){
    }

    public static String getTabTitle() {
        return TAB_TITLE;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    private int tableNumber;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    private int active;
}
