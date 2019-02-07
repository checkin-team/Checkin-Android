package com.checkin.app.checkin.Menu.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shivanshs9 on 7/5/18.
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class MenuGroupModel {
    @JsonProperty("name")
    private String name;

    @JsonProperty("category")
    private String category;

    @JsonProperty("type")
    private String type;

    @JsonProperty("icon")
    private String icon;

    private List<MenuItemModel> items;

    private List<MenuItemModel> vegItems;
    private List<MenuItemModel> nonVegItems;

    public MenuGroupModel() {}

    /*@JsonProperty("items")
    public void setItems(List<MenuItemModel> itemList) {
        AppDatabase.getMenuGroupModel(null).attach(this);
        AppDatabase.getMenuItemModel(null).put(itemList);
        items.addAll(itemList);
        AppDatabase.getMenuGroupModel(null).put(this);
    }*/

    public List<MenuItemModel> getItems() {
        return items;
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

    @JsonProperty("items")
    public void setItems(List<MenuItemModel> items) {
        vegItems = new ArrayList<>();
        nonVegItems = new ArrayList<>();
        this.items = items;
        for (MenuItemModel item: items) {
            if (item.isVegetarian())
                vegItems.add(item);
            else
                nonVegItems.add(item);
        }
    }

    public List<MenuItemModel> getNonVegItems() {
        return nonVegItems;
    }

    public List<MenuItemModel> getVegItems() {
        return vegItems;
    }

    public boolean hasSubGroups() {
        return vegItems.size() > 0 && nonVegItems.size() > 0;
    }
}
