package com.checkin.app.checkin.Shop.RecentCheckin.Model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class RecentCheckinModel {
    @JsonProperty("count_male")
    private int liveMale;

    @JsonProperty("count_female")
    private int liveFemale;

    @JsonProperty("checkins")
    private List<UserCheckinModel> checkins;

    public RecentCheckinModel() {
    }

    public RecentCheckinModel(int liveMale, int liveFemale, List<UserCheckinModel> checkin) {
        this.liveMale = liveMale;
        this.liveFemale = liveFemale;
        this.checkins = checkin;
    }

    public String formatLiveMale()
    {
        return String.valueOf(liveMale);
    }

    public String formatLiveFemale() {
        return String.valueOf(liveFemale);
    }

    public String formatLiveCount() {
        return String.valueOf(liveMale + liveFemale);
    }

    public int getliveMale() {
        return liveMale;
    }

    public int getliveFemale() {
        return liveFemale;
    }

    public List<UserCheckinModel> getCheckins() {
        return checkins;
    }

    public void setLiveMale(int liveMale) {
        this.liveMale = liveMale;
    }

    public void setLiveFemale(int liveFemale) {
        this.liveFemale = liveFemale;
    }

    public void setCheckins(List<UserCheckinModel> checkins) {
        this.checkins = checkins;
    }
}
