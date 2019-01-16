package com.checkin.app.checkin.Shop.ShopInvoice;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "pk",
        "hash_id",
        "count_orders",
        "count_customers",
        "total",
        "host",
        "table",
        "checked_in",
        "checked_out"
})
public class RestaurantResponseModel {
    @JsonProperty("pk")
    private Integer pk;
    @JsonProperty("hash_id")
    private String hashId;
    @JsonProperty("count_orders")
    private Integer countOrders;
    @JsonProperty("count_customers")
    private Integer countCustomers;
    @JsonProperty("total")
    private String total;
    @JsonProperty("host")
    private Host host;
    @JsonProperty("table")
    private String table;
    @JsonProperty("checked_in")
    private String checkedIn;
    @JsonProperty("checked_out")
    private String checkedOut;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("pk")
    public Integer getPk() {
        return pk;
    }

    @JsonProperty("pk")
    public void setPk(Integer pk) {
        this.pk = pk;
    }

    @JsonProperty("hash_id")
    public String getHashId() {
        return hashId;
    }

    @JsonProperty("hash_id")
    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    @JsonProperty("count_orders")
    public Integer getCountOrders() {
        return countOrders;
    }

    @JsonProperty("count_orders")
    public void setCountOrders(Integer countOrders) {
        this.countOrders = countOrders;
    }

    @JsonProperty("count_customers")
    public Integer getCountCustomers() {
        return countCustomers;
    }

    @JsonProperty("count_customers")
    public void setCountCustomers(Integer countCustomers) {
        this.countCustomers = countCustomers;
    }

    @JsonProperty("total")
    public String getTotal() {
        return total;
    }

    @JsonProperty("total")
    public void setTotal(String total) {
        this.total = total;
    }

    @JsonProperty("host")
    public Host getHost() {
        return host;
    }

    @JsonProperty("host")
    public void setHost(Host host) {
        this.host = host;
    }

    @JsonProperty("table")
    public String getTable() {
        return table;
    }

    @JsonProperty("table")
    public void setTable(String table) {
        this.table = table;
    }

    @JsonProperty("checked_in")
    public String getCheckedIn() {
        return checkedIn;
    }

    @JsonProperty("checked_in")
    public void setCheckedIn(String checkedIn) {
        this.checkedIn = checkedIn;
    }

    @JsonProperty("checked_out")
    public String getCheckedOut() {
        return checkedOut;
    }

    @JsonProperty("checked_out")
    public void setCheckedOut(String checkedOut) {
        this.checkedOut = checkedOut;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonPropertyOrder({
            "pk",
            "display_name",
            "display_pic_url"
    })
    public class Host {

        @JsonProperty("pk")
        private Integer pk;
        @JsonProperty("display_name")
        private String displayName;
        @JsonProperty("display_pic_url")
        private String displayPicUrl;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("pk")
        public Integer getPk() {
            return pk;
        }

        @JsonProperty("pk")
        public void setPk(Integer pk) {
            this.pk = pk;
        }

        @JsonProperty("display_name")
        public String getDisplayName() {
            return displayName;
        }

        @JsonProperty("display_name")
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        @JsonProperty("display_pic_url")
        public String getDisplayPicUrl() {
            return displayPicUrl;
        }

        @JsonProperty("display_pic_url")
        public void setDisplayPicUrl(String displayPicUrl) {
            this.displayPicUrl = displayPicUrl;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }
}
