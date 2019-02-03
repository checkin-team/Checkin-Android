package com.checkin.app.checkin.Menu.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Bhavik Patel on 11/08/2018.
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class ItemCustomizationFieldModel {
    @JsonProperty("pk")
    private String pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("is_vegetarian")
    private boolean isVegetarian;

    @JsonProperty("cost")
    private double cost;

    public ItemCustomizationFieldModel() {}

    public ItemCustomizationFieldModel(boolean isVegetarian, String name, double cost) {
        this.isVegetarian = isVegetarian;
        this.name = name;
        this.cost = cost;
    }

    public String getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public String formatCost() {
        return String.valueOf(cost);
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    @Override
    public String toString() {
        return getName();
    }
}
