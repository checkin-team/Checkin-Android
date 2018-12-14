package com.checkin.app.checkin.Shop.RecentCheckin.Model;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class RecentCheckinModel {

 @JsonProperty("capacity")
private int capacity;

 @JsonProperty("live_male")
private int liveMale;

@JsonProperty("live_female")
private int liveFemale;

@JsonProperty("checkins")
private List<UserCheckinModel> checkins;

    public RecentCheckinModel() {
    }

    public RecentCheckinModel(int capacity, int liveMale, int liveFemale, List<UserCheckinModel> checkin) {
        this.capacity = capacity;
        this.liveMale = liveMale;
        this.liveFemale = liveFemale;
        this.checkins = checkin;
    }

    public int getcapacity() {
        return capacity;
    }

    public int getliveMale() {
        return liveMale;
    }

    public int getliveFemale() {
        return liveFemale;
    }

    public List<UserCheckinModel> getCheckin() {
        return checkins;
    }
}
