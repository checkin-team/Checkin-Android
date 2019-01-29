package com.checkin.app.checkin.Manager;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class RestaurantStaticsModel {

    @JsonProperty("days_revenue")
    private double daysRevenue;
    @JsonProperty("weeks_revenue")
    private double weeksRevenue;
    @JsonProperty("avg_session_time")
    private long avgSessionTime;
    @JsonProperty("avg_serving_time")
    private long avgServingTime;
    @JsonProperty("trending_orders")
    private List<TrendingOrder> trendingOrders = null;

    public void setDaysRevenue(double daysRevenue) {
        this.daysRevenue = daysRevenue;
    }

    public void setWeeksRevenue(double weeksRevenue) {
        this.weeksRevenue = weeksRevenue;
    }

    public void setAvgSessionTime(long avgSessionTime) {
        this.avgSessionTime = avgSessionTime;
    }

    public void setAvgServingTime(long avgServingTime) {
        this.avgServingTime = avgServingTime;
    }

    public void setTrendingOrders(List<TrendingOrder> trendingOrders) {
        this.trendingOrders = trendingOrders;
    }

    public double getDaysRevenue() {
        return daysRevenue;
    }

    public double getWeeksRevenue() {
        return weeksRevenue;
    }

    public long getAvgSessionTime() {
        return avgSessionTime;
    }

    public long getAvgServingTime() {
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
        private double revenueGenerated;

        public void setItem(RestaurantItemModel item) {
            this.item = item;
        }

        public void setRevenueGenerated(double revenueGenerated) {
            this.revenueGenerated = revenueGenerated;
        }

        public RestaurantItemModel getItem() {
            return item;
        }

        public double getRevenueGenerated() {
            return revenueGenerated;
        }
    }
}
