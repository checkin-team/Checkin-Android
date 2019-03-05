package com.checkin.app.checkin.Menu.Model;

import com.checkin.app.checkin.Data.AppDatabase;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * Created by Bhavik Patel on 11/08/2018.
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@Entity
public class ItemCustomizationGroupModel {
    @Id(assignable = true)
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("min_select")
    private int minSelection = 0;

    @JsonProperty("max_select")
    private int maxSelection = 1;

    @Backlink(to = "group")
    private ToMany<ItemCustomizationFieldModel> customizationFields;

    private ToOne<MenuItemModel> menuItem;

    public ItemCustomizationGroupModel() {}

    public ItemCustomizationGroupModel(int minSelection, int maxSelection, String name) {
        this.minSelection = minSelection;
        this.maxSelection = maxSelection;
        this.name = name;
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

    public List<ItemCustomizationFieldModel> getCustomizationFields() {
        return customizationFields;
    }

    public void addCustomizationField(String name, double cost) {
        customizationFields.add(new ItemCustomizationFieldModel(true, name, cost));
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    @JsonProperty("fields")
    public void setCustomizationFields(List<ItemCustomizationFieldModel> customizationFields) {
        AppDatabase.getMenuItemCustomizationGroupModel(null).attach(this);
        AppDatabase.getMenuItemCustomizationFieldModel(null).remove(this.customizationFields);
        AppDatabase.getMenuItemCustomizationFieldModel(null).put(customizationFields);
        this.customizationFields.addAll(customizationFields);
        AppDatabase.getMenuItemCustomizationGroupModel(null).put(this);
    }

    public void setCustomizationFields(ToMany<ItemCustomizationFieldModel> customizationFields) {
        this.customizationFields = customizationFields;
    }

    public ToOne<MenuItemModel> getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(ToOne<MenuItemModel> menuItem) {
        this.menuItem = menuItem;
    }
}
