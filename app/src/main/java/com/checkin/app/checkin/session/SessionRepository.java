package com.checkin.app.checkin.session;

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
import com.checkin.app.checkin.Manager.Model.ManagerSessionEventModel;
import com.checkin.app.checkin.User.bills.UserTransactionBriefModel;
import com.checkin.app.checkin.User.bills.UserTransactionDetailsModel;
import com.checkin.app.checkin.session.model.QRResultModel;
import com.checkin.app.checkin.session.model.SessionBasicModel;
import com.checkin.app.checkin.session.model.SessionBriefModel;
import com.checkin.app.checkin.session.model.SessionOrderedItemModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class SessionRepository extends BaseRepository {
    private static SessionRepository INSTANCE;
    private final WebApiService mWebService;

    private SessionRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
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

    public LiveData<Resource<SessionBasicModel>> getActiveSessionCheck() {
        return new NetworkBoundResource<SessionBasicModel, SessionBasicModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<SessionBasicModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getActiveSessionCheck());
            }

            @Override
            protected void saveCallResult(SessionBasicModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<QRResultModel>> newCustomerSession(final ObjectNode data) {
        return new NetworkBoundResource<QRResultModel, QRResultModel>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<QRResultModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.postNewCustomerSession(data));
            }

            @Override
            protected void saveCallResult(QRResultModel data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<SessionBriefModel>> getSessionBriefDetail(final long sessionPk) {
        return new NetworkBoundResource<SessionBriefModel, SessionBriefModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<SessionBriefModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getSessionBriefDetail(sessionPk));
            }

            @Override
            protected void saveCallResult(SessionBriefModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<SessionOrderedItemModel>>> getSessionOrders(long sessionId) {
        return new NetworkBoundResource<List<SessionOrderedItemModel>, List<SessionOrderedItemModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SessionOrderedItemModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getSessionOrders(sessionId));
            }

            @Override
            protected void saveCallResult(List<SessionOrderedItemModel> data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<ManagerSessionEventModel>>> getSessionEvents(long sessionId) {
        return new NetworkBoundResource<List<ManagerSessionEventModel>, List<ManagerSessionEventModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ManagerSessionEventModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getManagerSessionEvents(sessionId));
            }

            @Override
            protected void saveCallResult(List<ManagerSessionEventModel> data) {
            }
        }.getAsLiveData();
    }


    public LiveData<Resource<UserTransactionDetailsModel>> getUserSessionDetail(long sessionId) {
        return new NetworkBoundResource<UserTransactionDetailsModel, UserTransactionDetailsModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<UserTransactionDetailsModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getUserSessionDetailById(sessionId));
            }

            @Override
            protected void saveCallResult(UserTransactionDetailsModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<UserTransactionBriefModel>> getUserSessionBriefDetail(final long sessionPk) {
        return new NetworkBoundResource<UserTransactionBriefModel, UserTransactionBriefModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<UserTransactionBriefModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getUserSessionBrief(sessionPk));
            }

            @Override
            protected void saveCallResult(UserTransactionBriefModel data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> removeUserFromWaiting() {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.deleteSessionUserWaiting());
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
                //saveCallResult code
            }
        }.getAsLiveData();
    }
}
