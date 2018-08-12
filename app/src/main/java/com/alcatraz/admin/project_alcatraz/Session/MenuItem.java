package com.alcatraz.admin.project_alcatraz.Session;

import com.alcatraz.admin.project_alcatraz.Data.Converters;

import java.util.Map;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Created by shivanshs9 on 6/5/18.
 */

@Entity
public class MenuItem {
    @Id private long id;
    private String name;
    @Convert(converter = Converters.MapConverter.class, dbType = String.class)
    private Map<String, Double> typeCost;
    private String baseType;
    private String unit;
    private String description;
    private boolean vegetarian;
    private ToOne<MenuGroup> group;
    private int menuId;
    private int subGroupIndex = 0;

    MenuItem() {
    }

    public MenuItem(String name, Map<String, Double> typeCost, String baseType, long groupId, int menuId) {
        this.name = name;
        this.typeCost = typeCost;
        this.baseType = baseType;
        this.group.setTargetId(groupId);
        this.menuId = menuId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MenuItem(MenuItem item) {
        this.name = item.getName();
        this.typeCost = item.getTypeCost();
        this.baseType = item.getBaseType();
        this.unit = item.getUnit();
        this.vegetarian = item.isVegetarian();
        this.menuId = item.getMenuId();
        this.subGroupIndex = item.getSubGroupIndex();
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

    public long getGroupId() {
        return group.getTargetId();
    }

    public ToOne<MenuGroup> getGroup() {
        return group;
    }

    public String getUnit() {
        return unit;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public int getMenuId() {
        return menuId;
    }

    public int getSubGroupIndex() {
        return subGroupIndex;
    }

    public void setSubGroupIndex(int subGroupIndex) {
        this.subGroupIndex = subGroupIndex;
    }

    public Map<String, Double> getTypeCost() {
        return typeCost;
    }

    public String getBaseType() {
        return baseType;
    }

    public OrderedItem order(int quantity, String type) {
        OrderedItem item = new OrderedItem(this, quantity, type);
        return item;
    }
}
