package com.checkin.app.checkin.Manager.Model;

import com.checkin.app.checkin.Menu.Model.MenuItemBriefModel;
import com.checkin.app.checkin.Utility.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ManagerStatsModel {
    @JsonProperty("revenue")
    private StatsDuration revenue;

    @JsonProperty("count_orders")
    private StatsDuration countOrders;

    @JsonProperty("avg_session_time")
    private long avgSessionTime;

    @JsonProperty("avg_serving_time")
    private long avgServingTime;

    @JsonProperty("trending_orders")
    private List<TrendingOrder> trendingOrders;

    public String getDayRevenue() {
        return String.valueOf(revenue.day);
    }

    public String getWeekRevenue() {
        return String.valueOf(revenue.week);
    }

    public String getDayOrdersCount() {
        return String.valueOf(countOrders.day);
    }

    public String getWeekOrdersCount() {
        return String.valueOf(countOrders.week);
    }

    public long getAvgSessionTime() {
        return avgSessionTime;
    }

    public long getAvgServingTime() {
        return avgServingTime;
    }

    public String formatAvgSessionTime() {
        return Utils.formatTimeDuration(avgSessionTime);
    }

    public String formatAvgServingTime() {
        return Utils.formatTimeDuration(avgServingTime);
    }

    public List<TrendingOrder> getTrendingOrders() {
        return trendingOrders;
    }

    public static class TrendingOrder {
        @JsonProperty("item")
        private MenuItemBriefModel item;

        @JsonProperty("revenue_contribution")
        private double revenueContribution;

        public MenuItemBriefModel getItem() {
            return item;
        }

        public double getRevenueContribution() {
            return revenueContribution * 100;
        }
    }

    public static class StatsDuration {
        @JsonProperty("day")
        private double day;

        @JsonProperty("week")
        private double week;
    }
}
