package com.checkin.app.checkin.Misc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.Locale;

public class GenericDetailModel {
    @JsonProperty("pk")
    private String pk;

    @JsonProperty("detail")
    private String detail;

    @JsonIgnore
    private int identifier;

    @JsonIgnore
    private File image;

    public GenericDetailModel() {}

    public GenericDetailModel(ObjectNode data) {
        this.pk = data.get("pk").asText();
        this.detail = data.get("detail").asText();
    }

    public String getDetail() {
        return detail;
    }

    public String getPk() {
        return pk;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "<%s: %s>", pk, detail);
    }
}
