package com.checkin.app.checkin.Session.Model;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import androidx.annotation.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TableSessionModel {
    @JsonProperty("pk")
    private long pk;

    @Nullable
    @JsonProperty("host")
    private BriefModel host;

    @JsonProperty("event")
    private EventBriefModel event;

    @JsonProperty("created")
    private Date created;

    @JsonProperty("is_accepted_checkout")
    private boolean isRequestedCheckout;

    public long getPk() {
        return pk;
    }

    @Nullable
    public BriefModel getHost() {
        return host;
    }

    public EventBriefModel getEvent() {
        return event;
    }

    public void setEvent(EventBriefModel event) {
        this.event = event;
    }

    public boolean isRequestedCheckout() {
        return isRequestedCheckout;
    }

    public void setRequestedCheckout(boolean requestedCheckout) {
        isRequestedCheckout = requestedCheckout;
    }

    public void setHost(@Nullable BriefModel host) {
        this.host = host;
    }

    public Date getCreated() {
        return created;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            TableSessionModel table = ((TableSessionModel) obj);
            return table != null && table.getPk() == this.getPk();
        } catch (ClassCastException ignored) {
            return false;
        }
    }
}
