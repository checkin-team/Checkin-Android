package com.checkin.app.checkin.Menu.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class MenuModel {
    @JsonProperty("pk")
    private String pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("groups")
    private List<MenuGroupModel> groups;

    public MenuModel() {}

    public String getName() {
        return name;
    }

    /*@JsonProperty("groups")
    public void setGroups(List<MenuGroupModel> groups) {
        AppDatabase.getMenuModel(null).attach(this);
        AppDatabase.getMenuGroupModel(null).put(groups);
        this.groups.addAll(groups);
        AppDatabase.getMenuModel(null).put(this);
    }*/

    public List<MenuGroupModel> getGroups() {
        return groups;
    }

    public String getPk() {
        return pk;
    }
}
