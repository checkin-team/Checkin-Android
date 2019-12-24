package com.checkin.app.checkin.session.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class TrendingDishItemCustomizationGroupModel {
    @JsonProperty("pk")
    private long pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("min_select")
    private int minSelection = 0;

    @JsonProperty("max_select")
    private int maxSelection = 1;

    @JsonProperty("fields")
    private List<TrendingDishItemCustomizationFieldModel> fields;

    public TrendingDishItemCustomizationGroupModel() {
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

    public List<TrendingDishItemCustomizationFieldModel> getFields() {
        return fields;
    }

    public void setFields(List<TrendingDishItemCustomizationFieldModel> fields) {
        this.fields = fields;
    }
}
