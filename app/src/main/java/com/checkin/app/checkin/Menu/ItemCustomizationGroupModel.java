package com.checkin.app.checkin.Menu;

import com.checkin.app.checkin.Data.AppDatabase;
import com.fasterxml.jackson.annotation.JsonIgnore;
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


@Entity
public class ItemCustomizationGroupModel {
    private static final String TAG = ItemCustomizationGroupModel.class.getSimpleName();
    @Id(assignable = true) @JsonProperty("id") private long id;
    @JsonProperty("min_select") private int minSelection = 0;
    @JsonProperty("max_select") private int maxSelection = 1;
    @JsonProperty("title") private String name;

    @Backlink (to = "group")
    @JsonIgnore private ToMany<ItemCustomizationFieldModel> customizationFields;
    @JsonIgnore private ToOne<MenuItemModel> item;

    public ItemCustomizationGroupModel() {
    }

    public ItemCustomizationGroupModel(int minSelection, int maxSelection, String name, long itemId) {
        this.minSelection = minSelection;
        this.maxSelection = maxSelection;
        this.name = name;
        item.setTargetId(itemId);
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

    public ToMany<ItemCustomizationFieldModel> getCustomizationFields() {
        return customizationFields;
    }

    @JsonProperty("fields")
    public void setCustomizationFields(List<ItemCustomizationFieldModel> customizationFields) {
        AppDatabase.getItemCustomizationGroupModel(null).attach(this);
        AppDatabase.getItemCustomizationFieldModel(null).put(customizationFields);
        this.customizationFields.addAll(customizationFields);
        AppDatabase.getItemCustomizationGroupModel(null).put(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMenuItemId() {
        return item.getTargetId();
    }

    public ToOne<MenuItemModel> getItem() {
        return item;
    }

    public void setItem(long itemId) {
        this.item.setTargetId(itemId);
    }
}
