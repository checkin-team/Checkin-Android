package com.checkin.app.checkin.Misc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BriefModel {
    @JsonProperty("pk")
    private String pk;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("display_pic_url")
    private String displayPic;

    public BriefModel() {}

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public void setDisplayPic(String displayPic) {
        this.displayPic = displayPic;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayPic() {
        return displayPic;
    }
}
