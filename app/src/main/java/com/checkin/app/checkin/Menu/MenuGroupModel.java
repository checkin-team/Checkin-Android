package com.checkin.app.checkin.Menu;

import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.AppDatabase;
import com.checkin.app.checkin.Data.Converters;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * Created by shivanshs9 on 7/5/18.
 */

@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MenuGroupModel {
    @Id(assignable = true) private long id;
    private String name;
    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("sub_groups")
    private List<String> subGroups;
    @Transient private String mImageUrl;
    private ToOne<MenuModel> menu;
    @Backlink(to = "group")
    @JsonIgnore private ToMany<MenuItemModel> items;
    private String category;

    MenuGroupModel() {
        this.subGroups = new ArrayList<>(1);
        this.subGroups.add("Default");
    }

    public MenuGroupModel(@NonNull final String name, final ArrayList<String> subGroups) {
        this.name = name;
        if (subGroups != null) {
            this.subGroups = subGroups;
        } else {
            this.subGroups = new ArrayList<>(1);
            this.subGroups.add("Default");
        }
    }

    @JsonProperty("items")
    public void setItems(List<MenuItemModel> itemList) {
        AppDatabase.getMenuGroupModel(null).attach(this);
        AppDatabase.getMenuItemModel(null).put(itemList);
        items.addAll(itemList);
        AppDatabase.getMenuGroupModel(null).put(this);
    }

    public List<MenuItemModel> getItems() {
        return items;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public int getSubGroupsCount() {
        return subGroups != null ? subGroups.size() : 0;
    }

    public List<String> getSubGroups() {
        return subGroups;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getCategory() {
        return category;
    }

    public ToOne<MenuModel> getMenu() {
        return menu;
    }

    public void setMenuId(long menuId) {
        menu.setTargetId(menuId);
    }
}
