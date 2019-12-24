package com.checkin.app.checkin.Waiter;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.Waiter.Model.OrderStatusModel;
import com.checkin.app.checkin.Waiter.Model.SessionContactModel;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.checkin.app.checkin.Waiter.Model.WaiterStatsModel;
import com.checkin.app.checkin.Waiter.Model.WaiterTableModel;
import com.checkin.app.checkin.misc.models.GenericDetailModel;
import com.checkin.app.checkin.session.models.CheckoutStatusModel;
import com.checkin.app.checkin.session.models.QRResultModel;
import com.checkin.app.checkin.session.models.RestaurantTableModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * Created by Shivansh Saini on 24/01/2019.
 */

public class WaiterRepository extends BaseRepository {
    private static WaiterRepository INSTANCE;
    private final WebApiService mWebService;

    private WaiterRepository(Context context) {
        mWebService = ApiClient.Companion.getApiService(context);
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

    public LiveData<Resource<ObjectNode>> postSessionContact(long sessionId, SessionContactModel data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postSessionContact(sessionId, data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<SessionContactModel>>> getSessionContacts(long sessionId) {
        return new NetworkBoundResource<List<SessionContactModel>, List<SessionContactModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SessionContactModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getSessionContactList(sessionId));
            }

            @Override
            protected void saveCallResult(List<SessionContactModel> data) {

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

    public LiveData<Resource<List<RestaurantTableModel>>> getShopTables(long shopId,boolean activeQuery) {
        return new NetworkBoundResource<List<RestaurantTableModel>, List<RestaurantTableModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<RestaurantTableModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantTables(shopId,activeQuery));
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

    public LiveData<Resource<CheckoutStatusModel>> postSessionRequestCheckout(long sessionId, ObjectNode data) {
        return new NetworkBoundResource<CheckoutStatusModel, CheckoutStatusModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<CheckoutStatusModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.postSessionRequestCheckout(sessionId, data));
            }

            @Override
            protected void saveCallResult(CheckoutStatusModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<OrderStatusModel>>> postOrderListStatus(long sessionId, final List<OrderStatusModel> orders) {
        return new NetworkBoundResource<List<OrderStatusModel>, List<OrderStatusModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<OrderStatusModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.postChangeOrderStatusList(sessionId, orders));
            }

            @Override
            protected void saveCallResult(List<OrderStatusModel> data) {

            }
        }.getAsLiveData();
    }
}
