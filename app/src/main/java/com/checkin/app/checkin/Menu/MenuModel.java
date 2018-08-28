package com.checkin.app.checkin.Menu;

import com.checkin.app.checkin.Data.AppDatabase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class MenuModel {
    @Id(assignable = true) private long id;
    private String name;
    @JsonIgnore
    @Backlink(to = "menu")
    private ToMany<MenuGroupModel> groups;

    public MenuModel() {}

    public MenuModel(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @JsonProperty("groups")
    public void setGroups(List<MenuGroupModel> groups) {
        AppDatabase.getMenuModel(null).attach(this);
        AppDatabase.getMenuGroupModel(null).put(groups);
        this.groups.addAll(groups);
        AppDatabase.getMenuModel(null).put(this);
    }

    public List<MenuGroupModel> getGroups() {
        return groups;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
