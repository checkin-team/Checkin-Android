package com.checkin.app.checkin.manager.models;

import com.checkin.app.checkin.menu.models.MenuItemBriefModel;
import com.checkin.app.checkin.utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonProperty("poor_orders")
    private List<PoorOrders> poorOrders;

    @JsonProperty("trending_groups")
    private List<TrendingGroups> trendingGroups;


    public double getDayRevenue() {
        return revenue.day;
    }

    public double getWeekRevenue() {
        return revenue.week;
    }

    public String getDayOrdersCount() {
        return String.format(Locale.ENGLISH, "%.0f", countOrders.day);
    }

    public String getWeekOrdersCount() {
        return String.format(Locale.ENGLISH, "%.0f", countOrders.week);
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
    public List<TrendingGroups> getTrendingGroups() {
        return trendingGroups;
    }
    public List<PoorOrders> getPoorOrders() { return poorOrders;
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

    public static class PoorOrders {
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

    public static class TrendingGroups {
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
