package com.alcatraz.admin.project_alcatraz.Session;

import com.alcatraz.admin.project_alcatraz.Data.Converters;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.annotation.Convert;

/**
 * Created by Bhavik Patel on 11/08/2018.
 */

public class ItemCustomizationGroup {

    private int minSelection = 0;
    private int maxSelection = 1;
    private String name;
    @Convert(converter = Converters.ListConverter.class, dbType = String.class)
    private List<ItemCustomizationField> itemCustomizationFieldList;

    public ItemCustomizationGroup(int minSelection, int maxSelection,  List<ItemCustomizationField> itemCustomizationFieldList,String name) {
        this.minSelection = minSelection;
        this.maxSelection = maxSelection;
        this.name = name;
        this.itemCustomizationFieldList = itemCustomizationFieldList;
    }

    public int getMinSelection() {
        return minSelection;
    }

    public void setMinSelection(int minSelection) {
        this.minSelection = minSelection;
    }

    public int getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(int maxSelection) {
        this.maxSelection = maxSelection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ItemCustomizationField> getItemCustomizationFieldList() {
        return itemCustomizationFieldList;
    }

    public void setItemCustomizationFieldList(List<ItemCustomizationField> itemCustomizationFieldList) {
        this.itemCustomizationFieldList = itemCustomizationFieldList;
    }
}
