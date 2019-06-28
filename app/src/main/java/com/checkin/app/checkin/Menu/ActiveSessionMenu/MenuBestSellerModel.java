package com.checkin.app.checkin.Menu.ActiveSessionMenu;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MenuBestSellerModel {

    @JsonProperty("pk")
    private long pk;

    @JsonProperty("name")
    private String name;

    public MenuBestSellerModel() {
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
