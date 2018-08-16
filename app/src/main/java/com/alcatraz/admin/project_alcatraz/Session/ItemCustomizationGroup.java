package com.alcatraz.admin.project_alcatraz.Session;

import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * Created by Bhavik Patel on 11/08/2018.
 */


@Entity
public class ItemCustomizationGroup {

    @Id private long id;
    private int minSelection = 0;
    private int maxSelection = 1;
    private String name;

    @Backlink (to = "customizationGroup")
    private ToMany<ItemCustomizationField> itemCustomizationFieldList;
    private ToOne<MenuItemModel> menuItem;

    public ItemCustomizationGroup() {
    }

    public ItemCustomizationGroup(int minSelection, int maxSelection, String name, long menuId) {
        this.minSelection = minSelection;
        this.maxSelection = maxSelection;
        this.name = name;
        menuItem.setTargetId(menuId);
    }

    public int getMinSelection() {
        return minSelection;
    }

    public void setMinSelection(int minSelection) {
        this.minSelection = minSelection;
    }

    public int getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(int maxSelection) {
        this.maxSelection = maxSelection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ToMany<ItemCustomizationField> getItemCustomizationFieldList() {
        return itemCustomizationFieldList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /*public void setItemCustomizationFieldList(ToMany<ItemCustomizationField> itemCustomizationFieldList) {
        this.itemCustomizationFieldList = itemCustomizationFieldList;
    }*/

    public long getMenuItemId() {
        return menuItem.getTargetId();
    }

    public ToOne<MenuItemModel> getMenuItem() {
        return menuItem;
    }
}
