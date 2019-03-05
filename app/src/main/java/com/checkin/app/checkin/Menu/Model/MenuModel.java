package com.checkin.app.checkin.Menu.Model;

import com.checkin.app.checkin.Data.AppDatabase;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
@Entity
public class MenuModel {
    @Id(assignable = true)
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("name")
    private String name;

    @Backlink(to = "menu")
    private ToMany<MenuGroupModel> groups;

    @JsonIgnore
    private long restaurantPk;

    public MenuModel() {}

    public String getName() {
        return name;
    }

    @JsonProperty("groups")
    public void setGroups(List<MenuGroupModel> groups) {
        AppDatabase.getMenuModel(null).attach(this);
        AppDatabase.getMenuGroupModel(null).remove(this.groups);
        AppDatabase.getMenuGroupModel(null).put(groups);
        this.groups.addAll(groups);
        AppDatabase.getMenuModel(null).put(this);
    }

    public List<MenuGroupModel> getGroups() {
        return groups;
    }

    public long getPk() {
        return pk;
    }

    public long getRestaurantPk() {
        return restaurantPk;
    }

    public void setRestaurantPk(long restaurantPk) {
        this.restaurantPk = restaurantPk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }
}
