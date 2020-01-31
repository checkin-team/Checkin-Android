package com.checkin.app.checkin.session.models;

import com.checkin.app.checkin.misc.models.BriefModel;
import com.checkin.app.checkin.utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionPromoModel {

    @JsonProperty("code")
    private String code;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("name")
    private String name;

    @JsonProperty("by_user")
    private BriefModel byUser;

    @JsonProperty("offer_amount")
    private Double offerAmount;

    public SessionPromoModel() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BriefModel getByUser() {
        return byUser;
    }

    public void setByUser(BriefModel byUser) {
        this.byUser = byUser;
    }

    public Double getOfferAmount() {
        return offerAmount;
    }

    public void setOfferAmount(Double offerAmount) {
        this.offerAmount = offerAmount;
    }

    public String getDetails() {
        return String.format("%s - %s", code, Utils.fromHtml(name));
    }
}
