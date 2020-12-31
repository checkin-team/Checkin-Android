package com.checkin.app.checkin.Shop.Private.Insight;

import android.content.Context;

import com.checkin.app.checkin.manager.models.ManagerStatsModel;
import com.checkin.app.checkin.utility.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopInsightRevenueModel {

    @JsonProperty("revenue")
    private RevenueDurationModel revenue;

    @JsonProperty("count_orders")
    private RevenueDurationModel countOrders;

    @JsonProperty("sales")
    private double sales;

    @JsonProperty("avg_session_time")
    private long avgSessionTime;

    @JsonProperty("avg_serving_time")
    private long avgServingTime;

    @JsonProperty("trending_orders")
    private List<ManagerStatsModel.ItemRevenue> itemRevenues;

    @JsonProperty("floating_cash")
    private double floatingCash;

    @JsonProperty("cancellation_rate")
    private double cancellationRate;

    public ShopInsightRevenueModel() {
    }

    public RevenueDurationModel getRevenue() {
        return revenue;
    }

    public RevenueDurationModel getCountOrders() {
        return countOrders;
    }

    public double getSales() {
        return sales;
    }

    public String formatSales(Context context) {
        return Utils.formatCurrencyAmount(context, sales);
    }

    public long getAvgSessionTime() {
        return avgSessionTime;
    }

    public long getAvgServingTime() {
        return avgServingTime;
    }

    public List<ManagerStatsModel.ItemRevenue> getItemRevenues() {
        return itemRevenues;
    }

    public double getFloatingCash() {
        return floatingCash;
    }

    public String getCancellationRate() {
        return cancellationRate + "%";
    }

    public String formatAvgSessionTime() {
        return Utils.formatTimeDuration(avgSessionTime);
    }

    public String formatAvgServingTime() {
        return Utils.formatTimeDuration(avgServingTime);
    }
}
