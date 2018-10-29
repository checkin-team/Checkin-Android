package com.checkin.app.checkin.Misc;

import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

public class TableModel {

        private String name;
        private boolean active;
        private long noUsers;
        private long noOrders;

        public TableModel() {}


        public TableModel(String name, boolean active, long noUsers, long noOrders) {
            this.name = name;
            this.active=active;
            this.noUsers=noUsers;
            this.noOrders=noOrders;
        }



        public String getName() {
            return name;
        }


        public boolean isActive() {
            return active;
        }


        public long getNoUsers() {
            return noUsers;
        }

        public long getNoOrder(){
            return noOrders;
    }

}
