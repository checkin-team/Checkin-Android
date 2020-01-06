package com.checkin.app.checkin.menu.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.checkin.app.checkin.Data.AppDatabase;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Inventory.Adapter.InventoryItemAdapter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * Created by shivanshs9 on 6/5/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@Entity
public class MenuItemModel implements Serializable {
    @Id(assignable = true)
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("name")
    private String name;

    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("types")
    private List<String> typeNames;

    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("costs")
    private List<Double> typeCosts;

    @JsonProperty("description")
    @Nullable
    private String description;

    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("tags")
    private List<String> tags;

    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("available_meals")
    private List<AVAILABLE_MEAL> availableMeals;

    @JsonProperty("is_vegetarian")
    private boolean isVegetarian;

    @JsonProperty("image")
    private String image;

    private ToMany<ItemCustomizationGroupModel> customizationGroups;

    @JsonIgnore
    private ToOne<MenuGroupModel> group;

    @JsonIgnore
    @Transient
    private InventoryItemAdapter.ItemViewHolder inventoryHolder;

    public MenuItemModel() {
    }

    public MenuItemModel(MenuItemModel item) {
        this.name = item.getName();
        this.typeNames = item.typeNames;
        this.typeCosts = item.typeCosts;
        this.isVegetarian = item.isVegetarian();
    }

    public List<ItemCustomizationGroupModel> getCustomizations() {
        return customizationGroups;
    }

    public boolean hasCustomizations() {
        return customizationGroups != null && !customizationGroups.isEmpty();
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public ToOne<MenuGroupModel> getGroup() {
        return group;
    }

    public void setGroup(ToOne<MenuGroupModel> group) {
        this.group = group;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    public List<String> getTypeNames() {
        return typeNames;
    }

    public void setTypeName(List<String> typeName) {
        this.typeNames = typeName;
    }

    public List<Double> getTypeCosts() {
        return typeCosts;
    }

    public void setTypeCost(List<Double> typeCost) {
        this.typeCosts = typeCost;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean hasAvailableMeal(AVAILABLE_MEAL availableMeal) {
        return availableMeals.contains(availableMeal);
    }

    public ToMany<ItemCustomizationGroupModel> getCustomizationGroups() {
        return customizationGroups;
    }

    @JsonProperty("customizations")
    public void setCustomizationGroups(List<ItemCustomizationGroupModel> customizationGroups) {
        AppDatabase.getMenuItemModel(null).attach(this);
        AppDatabase.getMenuItemCustomizationGroupModel(null).remove(this.customizationGroups);
        AppDatabase.getMenuItemCustomizationGroupModel(null).put(customizationGroups);
        this.customizationGroups.addAll(customizationGroups);
        AppDatabase.getMenuItemModel(null).put(this);
    }

    public List<AVAILABLE_MEAL> getAvailableMeals() {
        return availableMeals;
    }

    public void setAvailableMeals(List<AVAILABLE_MEAL> availableMeals) {
        this.availableMeals = availableMeals;
    }

    @JsonProperty("available_meals")
    public void setAvailableMeals(String[] availableMeals) {
        List<AVAILABLE_MEAL> result = new ArrayList<>();
        for (String meal : availableMeals) {
            AVAILABLE_MEAL availableMeal = AVAILABLE_MEAL.getByTag(meal);
            result.add(availableMeal);
        }
        this.availableMeals = result;
    }

    public boolean isComplexItem() {
        return hasCustomizations() || typeNames.size() > 1;
    }

    @NonNull
    public OrderedItemModel order(int quantity) {
        return new OrderedItemModel(this, quantity);
    }

    public OrderedItemModel order(int quantity, int type) {
        return new OrderedItemModel(this, quantity, type);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MenuItemModel && this.pk == ((MenuItemModel) obj).getPk();
    }

    public enum AVAILABLE_MEAL {
        BREAKFAST("brkfst"), LUNCH("lunch"), DINNER("dinner"),
        NIGHTLIFE("nhtlfe");

        public String tag;

        AVAILABLE_MEAL(String tag) {
            this.tag = tag;
        }

        public static AVAILABLE_MEAL getByTag(String tag) {
            for (AVAILABLE_MEAL meal : AVAILABLE_MEAL.values()) {
                if (meal.tag.equals(tag))
                    return meal;
            }
            return BREAKFAST;
        }
    }

}
