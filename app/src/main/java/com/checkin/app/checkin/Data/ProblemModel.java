package com.checkin.app.checkin.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProblemModel {
    @JsonProperty("type")
    private String type;

    @JsonProperty("status")
    private int status;

    @JsonProperty("title")
    private String title;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("errors")
    private ArrayNode errors;

    public ProblemModel(String type, int status, String title) {
        this.type = type;
        this.status = status;
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public ArrayNode getErrors() {
        return errors;
    }

    public void setErrors(ArrayNode errors) {
        this.errors = errors;
    }
}
