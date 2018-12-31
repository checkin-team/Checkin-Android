package com.checkin.app.checkin.Menu.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Bhavik Patel on 11/08/2018.
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class ItemCustomizationGroupModel {
    @JsonProperty("name")
    private String name;

    @JsonProperty("min_select")
    private int minSelection = 0;

    @JsonProperty("max_select")
    private int maxSelection = 1;

    @JsonProperty("fields")
    private List<ItemCustomizationFieldModel> customizationFields;

    public ItemCustomizationGroupModel() {}

    public ItemCustomizationGroupModel(int minSelection, int maxSelection, String name) {
        this.minSelection = minSelection;
        this.maxSelection = maxSelection;
        this.name = name;
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

    public List<ItemCustomizationFieldModel> getCustomizationFields() {
        return customizationFields;
    }

    /*@JsonProperty("fields")
    public void setCustomizationFields(List<ItemCustomizationFieldModel> customizationFields) {
        AppDatabase.getItemCustomizationGroupModel(null).attach(this);
        AppDatabase.getItemCustomizationFieldModel(null).put(customizationFields);
        this.customizationFields.addAll(customizationFields);
        AppDatabase.getItemCustomizationGroupModel(null).put(this);
    }*/
}
