package com.checkin.app.checkin.Inventory.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class InventoryModel {

    @JsonProperty("pk")
    private long pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("groups")
    private List<InventoryGroupModel> groups;

    public InventoryModel() {
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

    public List<InventoryGroupModel> getGroups() {
        return groups;
    }



    public void setGroups(List<InventoryGroupModel> groups) {
        this.groups = groups;
    }
}
