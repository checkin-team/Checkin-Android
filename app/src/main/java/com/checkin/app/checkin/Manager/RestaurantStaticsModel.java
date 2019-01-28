package com.checkin.app.checkin.Manager;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class RestaurantStaticsModel {

    @JsonProperty("days_revenue")
    private String daysRevenue;
    @JsonProperty("weeks_revenue")
    private String weeksRevenue;
    @JsonProperty("avg_session_time")
    private String avgSessionTime;
    @JsonProperty("avg_serving_time")
    private String avgServingTime;
    @JsonProperty("trending_orders")
    private List<TrendingOrder> trendingOrders = null;

    public void setDaysRevenue(String daysRevenue) {
        this.daysRevenue = daysRevenue;
    }

    public void setWeeksRevenue(String weeksRevenue) {
        this.weeksRevenue = weeksRevenue;
    }

    public void setAvgSessionTime(String avgSessionTime) {
        this.avgSessionTime = avgSessionTime;
    }

    public void setAvgServingTime(String avgServingTime) {
        this.avgServingTime = avgServingTime;
    }

    public void setTrendingOrders(List<TrendingOrder> trendingOrders) {
        this.trendingOrders = trendingOrders;
    }

    public String getDaysRevenue() {
        return daysRevenue;
    }

    public String getWeeksRevenue() {
        return weeksRevenue;
    }

    public String getAvgSessionTime() {
        return avgSessionTime;
    }

    public String getAvgServingTime() {
        return avgServingTime;
    }

    public List<TrendingOrder> getTrendingOrders() {
        return trendingOrders;
    }

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    public static class TrendingOrder {

        @JsonProperty("item")
        private RestaurantItemModel item;
        @JsonProperty("revenue_generated")
        private String revenueGenerated;

        public void setItem(RestaurantItemModel item) {
            this.item = item;
        }

        public void setRevenueGenerated(String revenueGenerated) {
            this.revenueGenerated = revenueGenerated;
        }

        public RestaurantItemModel getItem() {
            return item;
        }

        public String getRevenueGenerated() {
            return revenueGenerated;
        }
    }
}
