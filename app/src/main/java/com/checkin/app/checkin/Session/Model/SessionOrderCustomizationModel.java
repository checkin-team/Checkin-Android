package com.checkin.app.checkin.Session.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class SessionOrderCustomizationModel {
    @JsonProperty("pk")
    private int pk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("cost")
    private double cost;

    @JsonProperty("group")
    private String group;

    public SessionOrderCustomizationModel() {}

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
