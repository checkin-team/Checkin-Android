package com.checkin.app.checkin.Waiter;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Session.Model.QRResultModel;
import com.checkin.app.checkin.Session.Model.RestaurantTableModel;
import com.checkin.app.checkin.Waiter.Model.OrderStatusModel;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.checkin.app.checkin.Waiter.Model.WaiterStatsModel;
import com.checkin.app.checkin.Waiter.Model.WaiterTableModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * Created by Shivansh Saini on 24/01/2019.
 */

public class WaiterRepository extends BaseRepository {
    private final WebApiService mWebService;
    private static WaiterRepository INSTANCE;

    private WaiterRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public LiveData<Resource<List<WaiterTableModel>>> getWaiterServedTables() {
        return new NetworkBoundResource<List<WaiterTableModel>, List<WaiterTableModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<WaiterTableModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getWaiterServedTables());
            }

            @Override
            protected void saveCallResult(List<WaiterTableModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<WaiterEventModel>>> getWaiterEventsForTable(long sessionId) {
        return new NetworkBoundResource<List<WaiterEventModel>, List<WaiterEventModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<WaiterEventModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getWaiterSessionEvents(sessionId));
            }

            @Override
            protected void saveCallResult(List<WaiterEventModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<RestaurantTableModel>>> getShopActiveTables(long shopId) {
        return new NetworkBoundResource<List<RestaurantTableModel>, List<RestaurantTableModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<RestaurantTableModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantActiveTables(shopId));
            }

            @Override
            protected void saveCallResult(List<RestaurantTableModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<QRResultModel>> newWaiterSession(ObjectNode requestJson) {
        return new NetworkBoundResource<QRResultModel, QRResultModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<QRResultModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.postNewWaiterSession(requestJson));
            }

            @Override
            protected void saveCallResult(QRResultModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<OrderStatusModel>> changeOrderStatus(long orderId, ObjectNode data) {
        return new NetworkBoundResource<OrderStatusModel, OrderStatusModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<OrderStatusModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.postChangeOrderStatus(orderId, data));
            }

            @Override
            protected void saveCallResult(OrderStatusModel data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericDetailModel>> markEventDone(long eventId) {
        return new NetworkBoundResource<GenericDetailModel, GenericDetailModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericDetailModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.putSessionEventDone(eventId));
            }

            @Override
            protected void saveCallResult(GenericDetailModel data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<WaiterStatsModel>> getWaiterStats(long restaurantId) {
        return new NetworkBoundResource<WaiterStatsModel, WaiterStatsModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<WaiterStatsModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantWaiterStats(restaurantId));
            }

            @Override
            protected void saveCallResult(WaiterStatsModel data) {

            }
        }.getAsLiveData();
    }

    public static WaiterRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (WaiterRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WaiterRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
