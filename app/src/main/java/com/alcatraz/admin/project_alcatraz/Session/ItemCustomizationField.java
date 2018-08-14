package com.alcatraz.admin.project_alcatraz.Session;

import io.objectbox.annotation.Entity;

/**
 * Created by Bhavik Patel on 11/08/2018.
 */


public class ItemCustomizationField {

    private boolean vegetarian;
    private String name;
    private double price;
    private boolean isSelected = false;

    public ItemCustomizationField(boolean vegetarian, String name, double price) {
        this.vegetarian = vegetarian;
        this.name = name;
        this.price = price;
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
}
