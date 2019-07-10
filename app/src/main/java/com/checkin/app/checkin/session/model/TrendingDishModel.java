package com.checkin.app.checkin.session.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrendingDishModel {

    @JsonProperty("customizations")
    public List<Integer> customizations;

    @JsonProperty("pk")
    private long pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("types")
    private List<String> typeNames;

    @JsonProperty("costs")
    private List<Double> typeCosts;

    @JsonProperty("description")
    private String description;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("is_available")
    private boolean isAvailable;

    @JsonProperty("available_meals")
    private List<AVAILABLE_MEAL> availableMeals;

    @JsonProperty("is_vegetarian")
    private boolean isVegetarian;

    @JsonProperty("image")
    private String image;

    public TrendingDishModel() {
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

    public List<String> getTypeNames() {
        return typeNames;
    }

    public void setTypeNames(List<String> typeNames) {
        this.typeNames = typeNames;
    }

    public List<Double> getTypeCosts() {
        return typeCosts;
    }

    public void setTypeCosts(List<Double> typeCosts) {
        this.typeCosts = typeCosts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
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
            result.add(AVAILABLE_MEAL.getByTag(meal));
        }
        this.availableMeals = result;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Integer> getCustomizations() {
        return customizations;
    }

    public void setCustomizations(List<Integer> customizations) {
        this.customizations = customizations;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean hasCustomizations() {
        return customizations != null && !customizations.isEmpty();
    }

    public boolean isComplexItem() {
        return hasCustomizations() || typeNames.size() > 1;
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
