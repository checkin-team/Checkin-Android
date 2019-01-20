package com.checkin.app.checkin.Shop.ShopInvoice;

import com.checkin.app.checkin.Misc.BriefModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopSessionDetailModel {

    @JsonProperty("pk")
    private Integer pk;
    @JsonProperty("session_time")
    private Integer sessionTime;
    @JsonProperty("avg_preparation_time")
    private Integer avgPreparationTime;
    @JsonProperty("ordered_items")
    private List<OrderedItem> orderedItems = null;
    @JsonProperty("bill")
    private Bill bill;
    @JsonProperty("host")
    private BriefModel host;

    public Integer getPk() {
        return pk;
    }

    public Integer getSessionTime() {
        return sessionTime;
    }

    public Integer getAvgPreparationTime() {
        return avgPreparationTime;
    }

    public List<OrderedItem> getOrderedItems() {
        return orderedItems;
    }

    public Bill getBill() {
        return bill;
    }

    public BriefModel getHost() {
        return host;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class OrderedItem {

        @JsonProperty("item")
        private Item item;
        @JsonProperty("cost")
        private String cost;
        @JsonProperty("quantity")
        private Integer quantity;
        @JsonProperty("is_customized")
        private Boolean isCustomized;

        public Item getItem() {
            return item;
        }

        public String getCost() {
            return cost;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public Boolean getIsCustomized() {
            return isCustomized;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Item {

        @JsonProperty("pk")
        private Integer pk;
        @JsonProperty("name")
        private String name;
        @JsonProperty("is_vegetarian")
        private Boolean isVegetarian;

        public Integer getPk() {
            return pk;
        }

        public String getName() {
            return name;
        }

        public Boolean getIsVegetarian() {
            return isVegetarian;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Bill {

        @JsonProperty("subtotal")
        private String subtotal;
        @JsonProperty("tax")
        private String tax;
        @JsonProperty("tip")
        private String tip;
        @JsonProperty("discount")
        private String discount;
        @JsonProperty("offers")
        private String offers;
        @JsonProperty("total")
        private String total;

        public String getSubtotal() {
            return subtotal;
        }

        public String getTax() {
            return tax;
        }

        public String getTip() {
            return tip;
        }

        public String getDiscount() {
            return discount;
        }

        public String getOffers() {
            return offers;
        }

        public String getTotal() {
            return total;
        }
    }
}
