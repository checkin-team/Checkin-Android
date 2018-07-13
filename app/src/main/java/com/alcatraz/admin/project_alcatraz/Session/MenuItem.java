package com.alcatraz.admin.project_alcatraz.Session;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

/**
 * Created by shivanshs9 on 6/5/18.
 */

@Entity(tableName = "menu_items", indices = {
        @Index("group_id")
}, foreignKeys = {
        @ForeignKey(entity = MenuGroup.class, parentColumns = "id", childColumns = "group_id")
})
public class MenuItem {
    @PrimaryKey(autoGenerate = true) private int id;
    protected String name;
    protected ArrayList<Float> costs;
    protected ArrayList<String> types;
    protected String unit;
    protected ArrayList<String> tags;
    protected boolean vegetarian;
//    protected Bitmap image;
//    protected MediaStore.Video video;
//    protected ArrayList<MenuItem> items;
    @ColumnInfo(name = "group_id") private int groupId;
    @ColumnInfo(name = "menu_id") private int menuId;
    @ColumnInfo(name = "sub_group_index") private int subGroupIndex;

    MenuItem() {
    }

    @Ignore
    public MenuItem(String name, ArrayList<String> types, ArrayList<Float> costs, int groupId, int menuId) {
        this.name = name;
        this.types = types;
        this.costs = costs;
        this.groupId = groupId;
        this.menuId = menuId;
        this.subGroupIndex = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public ArrayList<Float> getCosts() {
        return costs;
    }

    public void setCosts(ArrayList<Float> costs) {
        this.costs = costs;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getSubGroupIndex() {
        return subGroupIndex;
    }

    public void setSubGroupIndex(int subGroupIndex) {
        this.subGroupIndex = subGroupIndex;
    }
}
