package com.checkin.app.checkin.session.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PromoDetailModel {

    @JsonProperty("pk")
    private long promoPk;

    @JsonProperty("code")
    private String code;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("name")
    private String name;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("terms")
    private String terms;

    @JsonProperty("expires")
    private String expires;

    @JsonProperty("discount_amount")
    private Double discount_amount;

    public PromoDetailModel() {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }
}
