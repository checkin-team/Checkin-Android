package com.alcatraz.admin.project_alcatraz.Session;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Created by Bhavik Patel on 11/08/2018.
 */


@Entity
public class ItemCustomizationField {

    @Id private long id;
    private boolean vegetarian;
    private String name;
    private double price;
    private boolean isSelected = false;

    private ToOne<ItemCustomizationGroup> customizationGroup;

    public ItemCustomizationField() {
    }

    public ItemCustomizationField(boolean vegetarian, String name, double price, long custGroupId) {
        this.vegetarian = vegetarian;
        this.name = name;
        this.price = price;
        customizationGroup.setTargetId(custGroupId);
    }

    public ToOne<ItemCustomizationGroup> getCustomizationGroup() {
        return customizationGroup;
    }

    public long getCustomizationGroupId() {
        return customizationGroup.getTargetId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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
