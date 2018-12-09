package com.checkin.app.checkin.Session.ActiveSession;

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
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by Bhavik Patel on 04/08/2018.
 */

public class ActiveSessionRepository extends BaseRepository {

    private final WebApiService mWebService;
    private static ActiveSessionRepository INSTANCE;

    private ActiveSessionRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public static ActiveSessionRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ActiveSessionRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ActiveSessionRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }


    public LiveData<Resource<ActiveSessionModel>> getActiveSessionDetail(String userId) {
        return new NetworkBoundResource<ActiveSessionModel, ActiveSessionModel>() {

            @Override
            protected void saveCallResult(ActiveSessionModel data) {}

            @Override
            protected boolean shouldUseLocalDb() {return false;}

            @NonNull
            @Override
            protected LiveData<ApiResponse<ActiveSessionModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getActiveSession(userId));
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postCancelOrders(String sessionId, ObjectNode data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postCancelOrder(sessionId, data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postAddMembers(String sessionId, ObjectNode data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postSessionAddMember(sessionId, data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }
}
