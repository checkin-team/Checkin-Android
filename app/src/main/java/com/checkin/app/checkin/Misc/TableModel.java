package com.checkin.app.checkin.Misc;

import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

public class TableModel {
        private long id;
        private String name;
        private boolean active;
        private long noUsers;
        private long noOrders;
        private String userStandard;

        public TableModel() {}


        public TableModel(String name, String UserStandard, boolean active, long noUsers, long noOrders) {
            this.name = name;
            this.active=active;
            this.noUsers=noUsers;
            this.noOrders=noOrders;
            this.userStandard=UserStandard;
        }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
            return name;
        }


        public boolean isActive() {
            return active;
        }

    public String getUserStandard() {
        return userStandard;
    }

    public long getNoUsers() {
            return noUsers;
        }

        public long getNoOrder(){
            return noOrders;
    }

}
