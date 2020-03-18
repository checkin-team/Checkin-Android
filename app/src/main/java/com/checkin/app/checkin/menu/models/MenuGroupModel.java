package com.checkin.app.checkin.menu.models;

import com.checkin.app.checkin.data.db.AppDatabase;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * Created by shivanshs9 on 7/5/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
@Entity
public class MenuGroupModel implements Cloneable {
    @Id(assignable = true)
    private long pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private String category;

    @JsonProperty("type")
    private String type;

    @JsonProperty("icon")
    private String icon;

    @Backlink(to = "group")
    private ToMany<MenuItemModel> items;

    @JsonIgnore
    private ToOne<MenuModel> menu;

    @Transient
    private List<MenuItemModel> vegItems;
    @Transient
    private List<MenuItemModel> nonVegItems;

    public MenuGroupModel() {
    }

    public List<MenuItemModel> getItems() {
        return items;
    }

    @JsonProperty("items")
    public void setItems(List<MenuItemModel> items) {
        vegItems = null;
        nonVegItems = null;
        AppDatabase.getMenuGroupModel().attach(this);
        AppDatabase.getMenuItemModel().remove(this.items);
        AppDatabase.getMenuItemModel().put(items);
        this.items.addAll(items);
        AppDatabase.getMenuGroupModel().put(this);
    }

    public void setCacheItems(List<MenuItemModel> items) {
        vegItems = null;
        nonVegItems = null;
        this.items.addAll(items);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getIcon() {
        return icon;
    }

    public List<MenuItemModel> getNonVegItems() {
        if (nonVegItems == null) {
            nonVegItems = new ArrayList<>();
            for (MenuItemModel item : items) {
                if (item.isVegetarian() != null && !item.isVegetarian())
                    nonVegItems.add(item);
            }
        }
        return nonVegItems;
    }

    public List<MenuItemModel> getVegItems() {
        if (vegItems == null) {
            vegItems = new ArrayList<>();
            for (MenuItemModel item : items) {
                if (item.isVegetarian() == null || item.isVegetarian())
                    vegItems.add(item);
            }
        }
        return vegItems;
    }

    public boolean hasSubGroups() {
        return !getVegItems().isEmpty() && !getNonVegItems().isEmpty();
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public ToOne<MenuModel> getMenu() {
        return menu;
    }

    public void setMenu(ToOne<MenuModel> menu) {
        this.menu = menu;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
