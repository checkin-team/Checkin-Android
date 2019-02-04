package com.checkin.app.checkin.Manager.Model;

import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel.EVENT_CONCERN_TYPE;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ManagerSessionEventModel extends WaiterEventModel {
    @JsonProperty("concern")
    private EVENT_CONCERN_TYPE concern;

    public ManagerSessionEventModel() {}

    public EVENT_CONCERN_TYPE getConcern() {
        return concern;
    }

    @JsonProperty("concern")
    public void setConcern(int concern) {
        this.concern = EVENT_CONCERN_TYPE.getByTag(concern);
    }

    public String formatConcernType() {
        return concern == EVENT_CONCERN_TYPE.CONCERN_QUALITY ? "Quality" : "Delay";
    }
}
