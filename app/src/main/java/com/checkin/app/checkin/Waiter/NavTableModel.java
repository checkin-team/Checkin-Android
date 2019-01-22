package com.checkin.app.checkin.Waiter;

import com.checkin.app.checkin.Misc.BriefModel;

import java.util.Date;

class NavTableModel {

    private String tableNumber;
    private Date occupied;

    public BriefModel getHost() {
        return host;
    }

    public void setHost(BriefModel host) {
        this.host = host;
    }

    private BriefModel host;

    public NavTableModel(){
    }

    public Date getOccupied() {
        return occupied;
    }

    public void setOccupied(Date occupied) {
        this.occupied = occupied;
    }

    public String formatOccupied() {
        return "";
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String table) {
        this.tableNumber = table;
    }

}
