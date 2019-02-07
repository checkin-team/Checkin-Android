package com.checkin.app.checkin.Search;

import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResultPeopleModel extends SearchResultModel {
    @JsonProperty("checkins")
    private long checkins;

    public long getCheckins() {
        return checkins;
    }

    public String formatCheckins() {
        return Utils.formatCount(checkins);
    }

    public String formatExtra() {
        return String.format(Locale.ENGLISH, "%s Checkins", formatCheckins());
    }
}
