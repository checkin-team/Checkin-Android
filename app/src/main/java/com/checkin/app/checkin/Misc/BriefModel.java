package com.checkin.app.checkin.Misc;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.checkin.app.checkin.R;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class BriefModel implements Serializable {
    @JsonProperty("pk")
    private String pk;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("display_pic_url")
    private String displayPic;

    public BriefModel() {
    }

    public BriefModel(String pk, String displayName, String displayPic) {
        this.pk = pk;
        this.displayName = displayName;
        this.displayPic = displayPic;
    }

    public BriefModel(long pk, String displayName, String displayPic) {
        this.pk = String.valueOf(pk);
        this.displayName = displayName;
        this.displayPic = displayPic;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String formatRestaurantName() {
        return "Live at " + "<font color=#0295aa>" + displayName + "</font>";
    }

    public String getDisplayPic() {
        return displayPic;
    }

    public void setDisplayPic(String displayPic) {
        this.displayPic = displayPic;
    }
}
