package com.checkin.app.checkin.Shop;

import androidx.annotation.DrawableRes;

import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Misc.LocationModel;
import com.checkin.app.checkin.R;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ShopModel {
    @JsonProperty("pk")
    protected long pk;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("tagline")
    protected String tagline;

    @JsonProperty("logo")
    protected String logo;

    @JsonProperty("covers")
    protected String[] covers;

    @JsonProperty("phone")
    protected String phone;

    @JsonProperty("email")
    protected String email;

    @JsonProperty("is_email_unconfirmed")
    protected Boolean emailUnconfirmed;

    @JsonProperty("website")
    protected String website;

    @JsonProperty("gstin")
    protected String gstIn;

    protected PAYMENT_MODE[] paymentModes;

    @JsonProperty("location")
    protected LocationModel location;
    @JsonProperty("locality")
    protected String locality;

    @JsonProperty("extra_data")
    protected String[] extraData;

    @JsonProperty("is_verified")
    protected Boolean verified;

    @JsonProperty("is_active")
    protected Boolean active;

    @JsonProperty("non_working_days")
    protected CharSequence[] nonWorkingDays;

    protected long openingHour;
    protected long closingHour;

    public ShopModel() {
    }

    public ShopModel(long pk) {
        this.pk = pk;
    }

    public long getPk() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    @JsonIgnore
    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getLogoUrl() {
        return logo;
    }

    public String[] getCovers() {
        return covers;
    }

    @JsonIgnore
    public Boolean isVerified() {
        return verified;
    }

    public Boolean isActive() {
        return active;
    }

    @JsonIgnore
    public Boolean isEmailUnconfirmed() {
        return emailUnconfirmed;
    }

    public PAYMENT_MODE[] getPaymentModes() {
        return paymentModes;
    }

    @JsonProperty("payment_mode")
    public void setPaymentModes(String... paymentModes) {
        this.paymentModes = new PAYMENT_MODE[paymentModes.length];
        int i = 0;
        for (String mode : paymentModes) {
            this.paymentModes[i] = PAYMENT_MODE.getByTag(mode);
            i++;
        }
    }

    public void setPaymentModes(PAYMENT_MODE... paymentModes) {
        this.paymentModes = paymentModes;
    }

    @JsonProperty("payment_mode")
    public String[] serializePaymentModes() {
        if (paymentModes == null)
            return null;
        String[] modes = new String[paymentModes.length];
        for (int i = 0; i < paymentModes.length; i++) {
            modes[i] = paymentModes[i].tag;
        }
        return modes;
    }

    @JsonIgnore
    public Boolean isValidStatus() {
        return this.isVerified() && this.isActive();
    }

    @JsonIgnore
    public String getShopStatus() {
        String msg = "Shop is properly working.";
        if (!isVerified()) {
            msg = "Shop isn't verified yet.\nContact our support for immediate on-site verification.";
        } else if (!isActive()) {
            msg = "Shop isn't currently serving customers.";
        }
        return msg;
    }

    public LocationModel getLocation() {
        return location;
    }

    public String getLocality() {
        return locality;
    }

    public String[] getExtraData() {
        return extraData;
    }

    public void setExtraData(String... extraData) {
        this.extraData = extraData;
    }

    public CharSequence[] getNonWorkingDays() {
        return nonWorkingDays;
    }

    public void setNonWorkingDays(CharSequence[] nonWorkingDays) {
        this.nonWorkingDays = nonWorkingDays;
    }

    @JsonIgnore
    public long getOpeningHour() {
        return this.openingHour;
    }

    public void setOpeningHour(long openingHour) {
        this.openingHour = openingHour;
    }

    @JsonIgnore
    public long getClosingHour() {
        return this.closingHour;
    }

    public void setClosingHour(long closingHour) {
        this.closingHour = closingHour;
    }

    @JsonProperty("working_timerange")
    public ObjectNode serializeWorkingTimeRange() {
        if (this.openingHour == this.closingHour)
            return null;
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("lower", this.openingHour);
        data.put("upper", this.closingHour);
        return data;
    }

    @JsonProperty("working_timerange")
    public void setWorkingTimerange(ObjectNode data) {
        if (data == null || !data.has("lower") || !data.has("upper")) {
            this.openingHour = 0L;
            this.closingHour = 0L;
            return;
        }
        this.openingHour = data.get("lower").asLong(0);
        this.closingHour = data.get("upper").asLong(0);
    }

    public enum PAYMENT_MODE {
        CASH("csh"), PAYTM("ptm"), CARD("crd");

        public String tag;

        PAYMENT_MODE(String tag) {
            this.tag = tag;
        }

        public static PAYMENT_MODE getByTag(String tag) {
            for (PAYMENT_MODE mode : PAYMENT_MODE.values()) {
                if (mode.tag.contentEquals(tag))
                    return mode;
            }
            return CASH;
        }
    }

    public static String getPaymentMode(PAYMENT_MODE paymentMode) {
        switch (paymentMode) {
            case CASH:
                return "via Cash";
            case PAYTM:
                return "";
            default:
                return "via Cash";
        }
    }

    @DrawableRes
    public static int getPaymentModeIcon(PAYMENT_MODE paymentMode) {
        switch (paymentMode) {
            case CASH:
                return R.drawable.ic_cash_grey;
            case PAYTM:
                return R.drawable.ic_paytm_logo;
            default:
                return R.drawable.ic_cash_grey;
        }
    }
}
