package com.alcatraz.admin.project_alcatraz.Session;

import android.support.annotation.NonNull;
import com.alcatraz.admin.project_alcatraz.Data.Converters;

import java.util.List;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Created by shivanshs9 on 6/5/18.
 */

@Entity
public class MenuItemModel {
    @Id private long id;
    private String name;
    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    private List<String> typeName;
    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    private List<Double> typeCost;
    private int baseTypeIndex;
    private String unit;
    private String description;
    private boolean vegetarian;
    private ToOne<MenuGroup> group;
    private int menuId;
    private int subGroupIndex = 0;
    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    private List<ItemCustomizationGroup> customizationGroups;

    MenuItemModel() {
    }

    public MenuItemModel(String name, List<String> typeName, List<Double> typeCost, int baseType, long groupId, int menuId, List<ItemCustomizationGroup> customizationGroups) {
        this.name = name;
        this.typeName = typeName;
        this.typeCost = typeCost;
        this.baseTypeIndex = baseType;
        this.group.setTargetId(groupId);
        this.menuId = menuId;
        if(customizationGroups !=null)this.customizationGroups = customizationGroups;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MenuItemModel(MenuItemModel item) {
        this.name = item.getName();
        this.typeName = item.typeName;
        this.typeCost = item.typeCost;
        this.baseTypeIndex = item.getBaseTypeIndex();
        this.unit = item.getUnit();
        this.vegetarian = item.isVegetarian();
        this.menuId = item.getMenuId();
        this.subGroupIndex = item.getSubGroupIndex();
        this.customizationGroups = item.customizationGroups;
    }

    public List<ItemCustomizationGroup> getCustomizationGroups() {
        return customizationGroups;
    }

    public void setCustomizationGroups(@NonNull List<ItemCustomizationGroup> customizationGroups) {
        this.customizationGroups = customizationGroups;
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

    public List<String> getTypeName() {
        return typeName;
    }

    public void setTypeName(List<String> typeName) {
        this.typeName = typeName;
    }

    public List<Double> getTypeCost() {
        return typeCost;
    }

    public void setTypeCost(List<Double> typeCost) {
        this.typeCost = typeCost;
    }

    public int getBaseTypeIndex() {
        return baseTypeIndex;
    }

    public void setBaseType(int baseType) {
        this.baseTypeIndex = baseType;
    }

    public OrderedItem order(int quantity, int type) {
        OrderedItem item = new OrderedItem(this, quantity, type);
        return item;
    }
}
