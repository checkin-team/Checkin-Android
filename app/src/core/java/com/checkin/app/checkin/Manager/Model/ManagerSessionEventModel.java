package com.checkin.app.checkin.Manager.Model;

import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel.EVENT_CONCERN_TYPE;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.io.Serializable;

public class ManagerSessionEventModel extends WaiterEventModel implements Serializable {
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

    public static class ManagerSessionEventModelDeserializer extends JsonDeserializer<ManagerSessionEventModel> {
        @Override
        public ManagerSessionEventModel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return Converters.objectMapper.readValue(jsonParser.getText(), ManagerSessionEventModel.class);
        }
    }
}
