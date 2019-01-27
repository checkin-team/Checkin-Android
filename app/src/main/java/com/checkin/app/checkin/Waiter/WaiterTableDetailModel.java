package com.checkin.app.checkin.Waiter;

/*@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "pk",
        "type",
        "message",
        "status",
        "data",
        "sender",
        "user",
        "created",
        "modified"
})*/
public class WaiterTableDetailModel {

    public static final int DELIVER = 4;
    public static final int DELAY = 3;
    public static final int ACCEPT = 2;
    public static final int NORMAL = 1;
    public static final int SERVICE = 5;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;

    WaiterTableDetailModel(){
    }

    /*@JsonProperty("pk")
    private Integer pk;
    @JsonProperty("type")
    private Integer type;
    @JsonProperty("message")
    private String message;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("data")
    private Data data;
    @JsonProperty("sender")
    private Integer sender;
    @JsonProperty("user")
    private User user;
    @JsonProperty("created")
    private String created;
    @JsonProperty("modified")
    private String modified;
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

    @JsonProperty("type")
    public Integer getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Integer type) {
        this.type = type;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("data")
    public Data getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(Data data) {
        this.data = data;
    }

    @JsonProperty("sender")
    public Integer getSender() {
        return sender;
    }

    @JsonProperty("sender")
    public void setSender(Integer sender) {
        this.sender = sender;
    }

    @JsonProperty("user")
    public User getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty("created")
    public String getCreated() {
        return created;
    }

    @JsonProperty("created")
    public void setCreated(String created) {
        this.created = created;
    }

    @JsonProperty("modified")
    public String getModified() {
        return modified;
    }

    @JsonProperty("modified")
    public void setModified(String modified) {
        this.modified = modified;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "service"
    })
    public class Data {

        @JsonProperty("service")
        private Integer service;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("service")
        public Integer getService() {
            return service;
        }

        @JsonProperty("service")
        public void setService(Integer service) {
            this.service = service;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "pk",
            "display_name",
            "display_pic_url"
    })
    public class User {

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
    }*/
}
