package com.checkin.app.checkin.Session;

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
    private long shop;
    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("types") private List<String> typeName;
    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("costs") private List<Double> typeCost;
    private String description;
    private boolean vegetarian;
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
    @JsonIgnore private ToMany<ItemCustomizationGroup> customizationGroups;

    MenuItemModel() {
    }

    public MenuItemModel(String name, List<String> typeName,List<Double> typeCost, long groupId, int menuId) {
        this.name = name;
        this.typeName = typeName;
        this.typeCost = typeCost;
        this.group.setTargetId(groupId);
        this.menuId = menuId;
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
    }

    public ToMany<ItemCustomizationGroup> getCustomizationGroups() {
        return customizationGroups;
    }

    @JsonProperty("customizations")
    public void setCustomizationGroups(List<ItemCustomizationGroup> customizationGroups) {
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

    public long getShop() {
        return shop;
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

    public OrderedItemModel order(int quantity, int type) {
        OrderedItemModel item = new OrderedItemModel(this, quantity, type);
        return item;
    }
}
