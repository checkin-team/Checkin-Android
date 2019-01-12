package com.checkin.app.checkin.Session;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class SessionOrdersCustomizationModel {
    @JsonProperty("pk")
    private int pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("cost")
    private double cost;

    @JsonProperty("group")
    private String group;

    public SessionOrdersCustomizationModel(){}


    public SessionOrdersCustomizationModel(int pk, String name, double cost, String group) {
        this.pk = pk;
        this.name = name;
        this.cost = cost;
        this.group = group;
    }

    public int getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public String getGroup() {
        return group;
    }
}
