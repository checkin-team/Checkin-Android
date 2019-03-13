package com.checkin.app.checkin.Menu.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Created by Bhavik Patel on 11/08/2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@Entity
public class ItemCustomizationFieldModel {
    @Id(assignable = true)
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("is_vegetarian")
    private boolean isVegetarian;

    @JsonProperty("cost")
    private double cost;

    private ToOne<ItemCustomizationGroupModel> group;

    public ItemCustomizationFieldModel() {}

    public ItemCustomizationFieldModel(boolean isVegetarian, String name, double cost) {
        this.isVegetarian = isVegetarian;
        this.name = name;
        this.cost = cost;
    }

    public long getPk() {
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

    public void setPk(long pk) {
        this.pk = pk;
    }

    public ToOne<ItemCustomizationGroupModel> getGroup() {
        return group;
    }

    public void setGroup(ToOne<ItemCustomizationGroupModel> group) {
        this.group = group;
    }
}
