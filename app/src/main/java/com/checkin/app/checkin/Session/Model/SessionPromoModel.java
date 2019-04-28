package com.checkin.app.checkin.Session.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionPromoModel {

    @JsonProperty("pk")
    private long promoPk;

    @JsonProperty("name")
    private String name;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("terms")
    private String terms;

    @JsonProperty("discount_amount")
    private Double discount_amount;

    public SessionPromoModel() {
    }

    public long getPromoPk() {
        return promoPk;
    }

    public void setPromoPk(long promoPk) {
        this.promoPk = promoPk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(Double discount_amount) {
        this.discount_amount = discount_amount;
    }
}
