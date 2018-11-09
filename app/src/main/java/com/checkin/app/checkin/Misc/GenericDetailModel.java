package com.checkin.app.checkin.Misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GenericDetailModel {
    @JsonProperty("pk")
    private String pk;

    @JsonProperty("detail")
    private String detail;

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
}
