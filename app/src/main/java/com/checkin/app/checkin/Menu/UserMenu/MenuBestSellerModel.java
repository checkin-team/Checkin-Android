package com.checkin.app.checkin.Menu.UserMenu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
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
