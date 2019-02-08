package com.checkin.app.checkin.Menu.Model;

import androidx.annotation.NonNull;

import com.checkin.app.checkin.Menu.Adapter.MenuItemAdapter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.annotation.Transient;

/**
 * Created by shivanshs9 on 6/5/18.
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class MenuItemModel {
    @JsonProperty("pk")
    private String pk;

    @JsonProperty("name")
    private String name;

//    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("types")
    private List<String> types;

//    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("costs")
    private List<Double> costs;

    @JsonProperty("description")
    private String description;

//    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("tags")
    private List<String> tags;

//    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    @JsonProperty("available_meals")
    private List<AVAILABLE_MEAL> availableMeals;

    @JsonProperty("is_vegetarian")
    private boolean isVegetarian;

    @JsonProperty("image")
    private String image;

    @JsonProperty("customizations")
    private List<ItemCustomizationGroupModel> customizationGroups;

    @JsonIgnore
    @Transient
    private MenuItemAdapter.ItemViewHolder holder;

    public enum AVAILABLE_MEAL {
        BREAKFAST("brkfst"), LUNCH("lunch"), DINNER("dinner"),
        NIGHTLIFE("nhtlfe");

        public String tag;
        AVAILABLE_MEAL(String tag) {
            this.tag = tag;
        }

        public static AVAILABLE_MEAL getByTag(String tag) {
            for (AVAILABLE_MEAL meal: AVAILABLE_MEAL.values()) {
                if (meal.tag.equals(tag))
                    return meal;
            }
            return BREAKFAST;
        }
    }

    public MenuItemModel() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MenuItemModel(MenuItemModel item) {
        this.name = item.getName();
        this.types = item.types;
        this.costs = item.costs;
        this.isVegetarian = item.isVegetarian();
    }

    public List<ItemCustomizationGroupModel> getCustomizations() {
        return customizationGroups;
    }

    public boolean hasCustomizations() {
        return customizationGroups != null && !customizationGroups.isEmpty();
    }

    /*@JsonProperty("customizations")
    public void setCustomizationGroups(List<ItemCustomizationGroupModel> customizationGroups) {
        AppDatabase.getMenuItemModel(null).attach(this);
        AppDatabase.getItemCustomizationGroupModel(null).put(customizationGroups);
        this.customizationGroups.addAll(customizationGroups);
        AppDatabase.getMenuItemModel(null).put(this);
    }*/

    public String getPk() {
        return pk;
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

    public List<String> getTypeNames() {
        return types;
    }

    public void setTypeName(List<String> typeName) {
        this.types = typeName;
    }

    public List<Double> getTypeCosts() {
        return costs;
    }

    public void setTypeCost(List<Double> typeCost) {
        this.costs = typeCost;
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

    public List<AVAILABLE_MEAL> getAvailableMeals() {
        return availableMeals;
    }

    public void setAvailableMeals(List<AVAILABLE_MEAL> availableMeals) {
        this.availableMeals = availableMeals;
    }

    @JsonProperty("available_meals")
    public void setAvailableMeals(String[] availableMeals) {
        List<AVAILABLE_MEAL> result = new ArrayList<>();
        for (String meal: availableMeals) {
            result.add(AVAILABLE_MEAL.getByTag(meal));
        }
        this.availableMeals = result;
    }

    public boolean isComplexItem() {
        return hasCustomizations() || types.size() > 1;
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

    @JsonIgnore
    public void setItemHolder(MenuItemAdapter.ItemViewHolder holder) {
        this.holder = holder;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MenuItemModel && this.pk.equals(((MenuItemModel) obj).getPk());
    }

}
