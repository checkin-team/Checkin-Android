package com.checkin.app.checkin.Shop;

import com.checkin.app.checkin.Misc.LocationModel;
import com.checkin.app.checkin.Utility.Util;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Bhavik Patel on 18/08/2018.
 */

//@Entity
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ShopModel {
    @JsonProperty("pk")
//    @Id(assignable = true)
    private String pk;
    private String name;
    private String tagline;
    private String logo;
    private String phone;
    private String email;
    private String website;

    @JsonProperty("has_nonveg")
    private boolean hasNonveg;
    @JsonProperty("has_home_delivery")
    private boolean hasHomeDelivery;
    @JsonProperty("has_alcohol")
    private boolean hasAlcohol;

    @JsonProperty("cuisines")
    private CharSequence[] cuisines;
    @JsonProperty("categories")
    private CharSequence[] categories;

    private PAYMENT_MODE[] paymentModes;


    @JsonProperty("location")
    private LocationModel location;
    @JsonProperty("locality")
    private String locality;

    @JsonProperty("extra_data")
    private String[] extraData;

    private boolean verified;
    private long followers;
    private long checkins;
    private float rating;

    public enum PAYMENT_MODE {
        CASH("csh"), PAYTM("ptm"), CARD("crd");

        String tag;

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

    public ShopModel() {}

    public ShopModel(String pk) {
        this.pk = pk;
    }

    public String getId() {
        return pk;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getTagline() {
        return tagline;
    }

    public String getLogoUrl() {
        return logo;
    }

    public boolean servesNonveg() {
        return hasNonveg;
    }

    public void setHasNonveg(boolean hasNonveg) {
        this.hasNonveg = hasNonveg;
    }

    public boolean hasHomeDelivery() {
        return hasHomeDelivery;
    }

    public void setHasHomeDelivery(boolean hasHomeDelivery) {
        this.hasHomeDelivery = hasHomeDelivery;
    }

    public boolean servesAlcohol() {
        return hasAlcohol;
    }

    public void setHasAlcohol(boolean hasAlcohol) {
        this.hasAlcohol = hasAlcohol;
    }

    public CharSequence[] getCuisines() {
        return cuisines;
    }

    public void setCuisines(CharSequence... cuisines) {
        this.cuisines = cuisines;
    }

    public CharSequence[] getCategories() {
        return categories;
    }

    public void setCategories(CharSequence... categories) {
        this.categories = categories;
    }

    public PAYMENT_MODE[] getPaymentModes() {
        return paymentModes;
    }

    @JsonProperty("payment_mode")
    public String[] serializePaymentModes() {
        String[] modes = new String[paymentModes.length];
        for (int i = 0; i < paymentModes.length; i++) {
            modes[i] = paymentModes[i].tag;
        }
        return modes;
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

    // -------------------------------

    public boolean isVerified() {
        return verified;
    }

    public long getFollowers() {
        return followers;
    }

    public long getCheckins() {
        return checkins;
    }

    public String formatFollowers() {
        return Util.formatCount(followers);
    }

    public String formatCheckins() {
        return Util.formatCount(checkins);
    }

    public float getRating() {
        return rating;
    }

}
