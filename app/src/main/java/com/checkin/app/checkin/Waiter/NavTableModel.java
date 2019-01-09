package com.checkin.app.checkin.Waiter;

import java.util.Date;

class NavTableModel {

    private String table;
    private Host host;
    private Date occupied;

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

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    static class Host{

        public Host(){
        }

        private String tableNumber;

        public String getTableNumber() {
            return tableNumber;
        }

        public void setTableNumber(String tableNumber) {
            this.tableNumber = tableNumber;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        private String customerName;
    }
}
