package com.checkin.app.checkin.Session;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;

/**
 * Created by Bhavik Patel on 11/08/2018.
 */


@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ItemCustomizationField {

    @Id(assignable = true) private long id;
    @JsonProperty("is_vegetarian") private boolean vegetarian;
    private String name;
    private double cost;
    @Transient private boolean isSelected = false;

    private ToOne<ItemCustomizationGroup> group;

    public ItemCustomizationField() {
    }

    public ItemCustomizationField(boolean vegetarian, String name, double cost, long custGroupId) {
        this.vegetarian = vegetarian;
        this.name = name;
        this.cost = cost;
        group.setTargetId(custGroupId);
    }

    public ToOne<ItemCustomizationGroup> getGroup() {
        return group;
    }

    public long getGroupId() {
        return group.getTargetId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
