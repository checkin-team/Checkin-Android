package com.checkin.app.checkin.Data;

import android.webkit.URLUtil;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    private ERROR_CODE errorCode;

    public ProblemModel() {

    }

    public ProblemModel(String type, int status, String title) {
        this.type = type;
        this.status = status;
        this.title = title;
    }

    @Nullable
    public static ProblemModel fromResource(Resource<?> resource) {
        if (resource.hasErrorBody()) {
            try {
                return Converters.objectMapper.treeToValue(resource.getErrorBody(), ProblemModel.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
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

    @Nullable
    public ERROR_CODE getErrorCode() {
        if (this.errorCode == null && URLUtil.isHttpsUrl(type)) {
            String[] arr = type.split("/");
            this.errorCode = ERROR_CODE.findByTag(arr[arr.length - 1]);
        }
        return this.errorCode;
    }

    public enum ERROR_CODE {
        HTTP_ERROR(""), INVALID_VERSION("invalid_version"), DEPRECATED_VERSION("deprecated_version"),
        SESSION_USER_PENDING_MEMBER("session__user_pending_member"), INVALID_PAYMENT_MODE_PROMO_AVAILED("invalid");

        String tag;

        ERROR_CODE(String tag) {
            this.tag = tag;
        }

        static ERROR_CODE findByTag(String tag) {
            for (ERROR_CODE code : ERROR_CODE.values()) {
                if (code.tag.equals(tag))
                    return code;
            }
            return HTTP_ERROR;
        }
    }
}
