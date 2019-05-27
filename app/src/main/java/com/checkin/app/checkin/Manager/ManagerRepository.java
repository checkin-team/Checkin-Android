package com.checkin.app.checkin.Manager;

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
import com.checkin.app.checkin.Manager.Model.ManagerSessionInvoiceModel;
import com.checkin.app.checkin.Manager.Model.ManagerStatsModel;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.session.model.CheckoutStatusModel;
import com.checkin.app.checkin.session.model.QRResultModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ManagerRepository extends BaseRepository {
    private static ManagerRepository INSTANCE;
    private final WebApiService mWebService;

    private ManagerRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public static ManagerRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ManagerRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ManagerRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<ManagerStatsModel>> getManagerStats(long restaurantId) {
        return new NetworkBoundResource<ManagerStatsModel, ManagerStatsModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ManagerStatsModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRestaurantManagerStats(restaurantId));
            }

            @Override
            protected void saveCallResult(ManagerStatsModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<CheckoutStatusModel>> manageSessionCheckout(long sessionId) {
        return new NetworkBoundResource<CheckoutStatusModel, CheckoutStatusModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<CheckoutStatusModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.putSessionCheckout(sessionId));
            }

            @Override
            protected void saveCallResult(CheckoutStatusModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ManagerSessionInvoiceModel>> getManagerSessionInvoice(long sessionId) {
        return new NetworkBoundResource<ManagerSessionInvoiceModel, ManagerSessionInvoiceModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ManagerSessionInvoiceModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getManagerSessionInvoice(sessionId));
            }

            @Override
            protected void saveCallResult(ManagerSessionInvoiceModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericDetailModel>> putManageSessionBill(long sessionId, ObjectNode data) {
        return new NetworkBoundResource<GenericDetailModel, GenericDetailModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericDetailModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.putManageSessionBill(sessionId, data));
            }

            @Override
            protected void saveCallResult(GenericDetailModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<QRResultModel>> managerInitiateSession(ObjectNode requestJson) {
        return new NetworkBoundResource<QRResultModel, QRResultModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<QRResultModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.postManageInitiateSession(requestJson));
            }

            @Override
            protected void saveCallResult(QRResultModel data) {
                //saveCallResult code
            }
        }.getAsLiveData();
    }
}
