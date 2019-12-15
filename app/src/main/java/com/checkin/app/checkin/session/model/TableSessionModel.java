package com.checkin.app.checkin.session.model;

import androidx.annotation.Nullable;

import com.checkin.app.checkin.misc.models.BriefModel;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Calendar;
import java.util.Date;

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

    @JsonProperty("bill")
    private double bill;

    public TableSessionModel() {
    }

    public TableSessionModel(long pk, @Nullable BriefModel host, EventBriefModel event) {
        this.pk = pk;
        this.host = host;
        this.event = event;
    }

    public long getPk() {
        return pk;
    }

    @Nullable
    public BriefModel getHost() {
        return host;
    }

    public void setHost(@Nullable BriefModel host) {
        this.host = host;
    }

    public boolean hasHost() {
        return host != null;
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

    public Date getCreated() {
        return created;
    }

    public String formatTimeDuration() {
        return Utils.formatTimeDuration(Calendar.getInstance().getTime().getTime() - created.getTime());
    }

    public double getBill() {
        return bill;
    }

    public void setBill(double bill) {
        this.bill = bill;
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

    public void setCreated(Date created) {
        this.created = created;
    }
}
