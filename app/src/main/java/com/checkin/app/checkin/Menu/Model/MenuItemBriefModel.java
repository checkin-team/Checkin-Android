package com.checkin.app.checkin.Menu.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MenuItemBriefModel {

    @JsonProperty("pk")
    private long pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("is_vegetarian")
    private boolean isVegetarian;

    public MenuItemBriefModel() {
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    public long getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public boolean getIsVegetarian() {
        return isVegetarian;
    }
}
