package com.checkin.app.checkin.Session.Model;

import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Menu.Model.ItemCustomizationGroupModel;
import com.checkin.app.checkin.Menu.Model.MenuItemModel;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SessionOrderedItemModel implements Serializable {
    @JsonProperty("pk")
    private int pk;

    @JsonProperty("item")
    private MenuItemModel item;

    @JsonProperty("cost")
    private double cost;

    @JsonProperty("item_type")
    private String itemType;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("customizations")
    private List<ItemCustomizationGroupModel> customizations;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("ordered")
    private Date ordered;

    @JsonProperty("is_customized")
    private boolean isCustomized;

    @JsonProperty("status")
    private CHAT_STATUS_TYPE status;

    public SessionOrderedItemModel() {
    }

    public int getPk() {
        return pk;
    }

    public MenuItemModel getItem() {
        return item;
    }

    public String formatItemDetail() {
        return String.format(Locale.ENGLISH, "%s x %d", item.getName(), quantity);
    }

    public double getCost() {
        return cost;
    }

    public String formatCost() {
        return String.valueOf(cost);
    }

    public String getItemType() {
        return itemType;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<ItemCustomizationGroupModel> getCustomizations() {
        return customizations;
    }

    public String getRemarks() {
        return remarks;
    }

    public Date getOrdered() {
        return ordered;
    }

    public boolean isCustomized() {
        return isCustomized;
    }

    public long getRemainingCancelTime() {
        return Constants.DEFAULT_ORDER_CANCEL_DURATION - ((new Date()).getTime() - ordered.getTime());
    }

    public boolean canCancel() {
        return (getRemainingCancelTime() >= 0) && (status == CHAT_STATUS_TYPE.IN_PROGRESS || status == CHAT_STATUS_TYPE.OPEN);
    }

    @JsonProperty("status")
    public void setStatus(int tag) {
        this.status = CHAT_STATUS_TYPE.getByTag(tag);
    }

    @JsonProperty("customizations")
    public void setCustomizations(List<SessionOrderCustomizationModel> customizations) {
        List<ItemCustomizationGroupModel> groupModels = new ArrayList<>();
        for (SessionOrderCustomizationModel customizationModel: customizations) {
            int index = -1;
            for (int i = 0; i < groupModels.size(); i++) {
                if (groupModels.get(i).getName().equalsIgnoreCase(customizationModel.getGroup())) {
                    index = i;
                    break;
                }
            }
            if (index > -1)
                groupModels.get(index).addCustomizationField(customizationModel.getName(), customizationModel.getCost());
            else {
                ItemCustomizationGroupModel group = new ItemCustomizationGroupModel(0, 0, customizationModel.getGroup());
                group.addCustomizationField(customizationModel.getName(), customizationModel.getCost());
                groupModels.add(group);
            }
        }
        this.customizations = groupModels;
    }

    public CHAT_STATUS_TYPE getStatus() {
        return status;
    }

    public String formatElapsedTime() {
        return Utils.formatElapsedTime(ordered);
    }

    public String formatQuantityType() {
        return String.format(Locale.ENGLISH, "QTY: %d %s", quantity, itemType);
    }

    public String formatQuantityItemType() {
        return String.format(Locale.ENGLISH, "%d %s", quantity, itemType);
    }

    public static class SessionOrderedItemDeserializer extends JsonDeserializer<SessionOrderedItemModel> {
        @Override
        public SessionOrderedItemModel deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return Converters.objectMapper.readValue(jsonParser.getText(), SessionOrderedItemModel.class);
        }
    }
}
