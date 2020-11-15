package com.checkin.app.checkin.manager.models;

import com.checkin.app.checkin.Waiter.models.WaiterEventModel;
import com.checkin.app.checkin.session.activesession.chat.SessionChatDataModel.EVENT_CONCERN_TYPE;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ManagerSessionEventModel extends WaiterEventModel implements Serializable {
    @JsonProperty("concern")
    private EVENT_CONCERN_TYPE concern;

    public ManagerSessionEventModel() {
    }

    public EVENT_CONCERN_TYPE getConcern() {
        return concern;
    }

    @JsonProperty("concern")
    public void setConcern(int concern) {
        this.concern = EVENT_CONCERN_TYPE.getByTag(concern);
    }

    public String formatConcernType() {
        switch (concern) {
            case CONCERN_DELAY:
                return "Delay";
            case CONCERN_QUALITY:
                return "Quality";
            case CONCERN_REMARK:
                return "Remark";
        }
        return "";
    }
}
