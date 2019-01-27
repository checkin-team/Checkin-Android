package com.checkin.app.checkin.ManagerOrders;

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
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class ManagerOrdersRepository extends BaseRepository {

    private final WebApiService mWebService;
    private static ManagerOrdersRepository INSTANCE;

    private ManagerOrdersRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public static ManagerOrdersRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ManagerOrdersRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ManagerOrdersRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<List<SessionOrderedItemModel>>> getManagerOrdersDetails(int sessionId) {
        return new NetworkBoundResource<List<SessionOrderedItemModel>, List<SessionOrderedItemModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {return false;}

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SessionOrderedItemModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getManagerOrders(sessionId));
            }

            @Override
            protected void saveCallResult(List<SessionOrderedItemModel> data) {}
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> changeManagerOrderStatus(int orderId, ObjectNode data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postChangeOrderStatus(orderId, data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) { }
        }.getAsLiveData();
    }

    public LiveData<Resource<ActiveSessionModel>> getManagerOrdersBrief(int sessionId) {
        return new NetworkBoundResource<ActiveSessionModel, ActiveSessionModel>() {

            @Override
            protected void saveCallResult(ActiveSessionModel data) {}

            @Override
            protected boolean shouldUseLocalDb() {return false;}

            @NonNull
            @Override
            protected LiveData<ApiResponse<ActiveSessionModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getManagerOrderBrief(sessionId));
            }
        }.getAsLiveData();
    }
}
