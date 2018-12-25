package com.checkin.app.checkin.Session;

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
import com.checkin.app.checkin.Shop.RecentCheckin.Model.RecentCheckinModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SessionRepository extends BaseRepository {
    private final WebApiService mWebService;
    private static SessionRepository INSTANCE;

    private SessionRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public LiveData<Resource<GenericDetailModel>> newCustomerSession(final ObjectNode data) {
        return new NetworkBoundResource<GenericDetailModel, GenericDetailModel>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericDetailModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.postNewCustomerSession(data));
            }

            @Override
            protected void saveCallResult(GenericDetailModel data) {}
        }.getAsLiveData();
    }

    public LiveData<Resource<RecentCheckinModel>> getRecentCheckins(final String shopId) {
        return new NetworkBoundResource<RecentCheckinModel, RecentCheckinModel>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RecentCheckinModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getRecentCheckins(shopId));
            }

            @Override
            protected void saveCallResult(RecentCheckinModel data) {

            }
        }.getAsLiveData();
    }

    public static SessionRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (SessionRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SessionRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
