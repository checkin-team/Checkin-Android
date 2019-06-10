package com.checkin.app.checkin.Shop.Private.Insight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RevenueDurationModel {

    @JsonProperty("day")
    private double day;

    @JsonProperty("week")
    private double week;

    @JsonProperty("month")
    private double month;

    public RevenueDurationModel() {
    }

    public double getDay() {
        return day;
    }

    public void setDay(double day) {
        this.day = day;
    }

    public double getWeek() {
        return week;
    }

    public void setWeek(double week) {
        this.week = week;
    }

    public double getMonth() {
        return month;
    }

    public void setMonth(double month) {
        this.month = month;
    }

    public int formatDay(){
        return (int)day;
    }

    public int formatWeek(){
        return (int)week;
    }

    public int formatMonth(){
        return (int)month;
    }
}
