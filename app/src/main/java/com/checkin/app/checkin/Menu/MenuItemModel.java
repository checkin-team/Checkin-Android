package com.checkin.app.checkin.Menu;

import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.AppDatabase;
import com.checkin.app.checkin.Data.Converters;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * Created by shivanshs9 on 6/5/18.
 */

@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MenuItemModel {
    @Id(assignable = true) private long id;
    private String name;
    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("types") private List<String> typeName;
    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("costs") private List<Double> typeCost;
    private String description;
    private boolean vegetarian;
    private boolean breakfast;
    private boolean dinner;
    private boolean lunch;

    @JsonIgnore private ToOne<MenuGroupModel> group;
    private boolean available;
    @JsonProperty("menu_id") private int menuId;
    @JsonProperty("sub_group_index") private int subGroupIndex = 0;
    private String image;
    private String video;
    private String remarks;
    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("available_meals") private List<String> availableMeals;
    private String recipe;
    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    private List<String> ingredients;
    @Backlink(to = "item")
    @JsonIgnore private ToMany<ItemCustomizationGroupModel> customizationGroups;
    @JsonIgnore @Transient private MenuItemAdapter.ItemViewHolder holder;

    MenuItemModel() {
    }

    public MenuItemModel(String name, List<String> typeName,List<Double> typeCost, long groupId, int menuId,boolean breakfast,boolean lunch,boolean dinner) {
        this.name = name;
        this.typeName = typeName;
        this.typeCost = typeCost;
        this.group.setTargetId(groupId);
        this.menuId = menuId;
        this.breakfast=breakfast;
        this.lunch=lunch;
        this.dinner=dinner;

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
        this.vegetarian = item.isVegetarian();
        this.menuId = item.getMenuId();
        this.subGroupIndex = item.getSubGroupIndex();
        this.breakfast=item.isBreakfast();
        this.dinner=item.isDinner();
        this.lunch=item.isLunch();
    }

    public ToMany<ItemCustomizationGroupModel> getCustomizationGroups() {
        return customizationGroups;
    }

    @JsonProperty("customizations")
    public void setCustomizationGroups(List<ItemCustomizationGroupModel> customizationGroups) {
        AppDatabase.getMenuItemModel(null).attach(this);
        AppDatabase.getItemCustomizationGroupModel(null).put(customizationGroups);
        this.customizationGroups.addAll(customizationGroups);
        AppDatabase.getMenuItemModel(null).put(this);
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

    public ToOne<MenuGroupModel> getGroup() {
        return group;
    }

    @JsonProperty("group")
    public void setGroup(long groupId) {
        group.setTargetId(groupId);
    }

    public boolean isVegetarian() {
        return vegetarian;
    }
    public boolean isBreakfast() {
        return breakfast;
    }
    public boolean isLunch() {
        return lunch;
    }

    public boolean isDinner() {
        return dinner;
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

    public String getImage() {
        return image;
    }

    public String getVideo() {
        return video;
    }

    public List<String> getAvailableMeals() {
        return availableMeals;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public boolean getAvailable() {
        return available;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setAvailableMeals(List<String> availableMeals) {
        this.availableMeals = availableMeals;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getRemarks() {
        return remarks;
    }

    public boolean isComplexItem() {
        return !customizationGroups.isEmpty() || typeCost.size() > 1;
    }

    @NonNull
    public OrderedItemModel order(int quantity) {
        return new OrderedItemModel(this, quantity);
    }

    public OrderedItemModel order(int quantity, int type) {
        return new OrderedItemModel(this, quantity, type);
    }

    public MenuItemAdapter.ItemViewHolder getItemHolder() {
        return holder;
    }

    public void setItemHolder(MenuItemAdapter.ItemViewHolder holder) {
        this.holder = holder;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MenuItemModel && this.id == ((MenuItemModel) obj).id;
    }

}
