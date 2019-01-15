package com.checkin.app.checkin.Session.ActiveSession;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat.ActiveSessionChatModel;
import com.checkin.app.checkin.Session.SessionCustomerModel;
import com.checkin.app.checkin.Session.SessionViewOrdersModel;
import com.checkin.app.checkin.User.UserModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

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


    public LiveData<Resource<ActiveSessionModel>> getActiveSessionDetail() {
        return new NetworkBoundResource<ActiveSessionModel, ActiveSessionModel>() {

            @Override
            protected void saveCallResult(ActiveSessionModel data) {}

            @Override
            protected boolean shouldUseLocalDb() {return false;}

            @NonNull
            @Override
            protected LiveData<ApiResponse<ActiveSessionModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getActiveSession());
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postAddMembers(ObjectNode data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postActiveSessionCustomers(data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<SessionViewOrdersModel>>> getSessionOrdersDetails() {
        return new NetworkBoundResource<List<SessionViewOrdersModel>, List<SessionViewOrdersModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {return false;}

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SessionViewOrdersModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getSessionOrders());
            }

            @Override
            protected void saveCallResult(List<SessionViewOrdersModel> data) {}
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> putSelfPresence(ObjectNode data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.putActiveSessionSelfCustomer(data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> removeSessionOrder(String order_id) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.deleteSessionOrder(order_id));
            }

            @Override
            protected void saveCallResult(ObjectNode data) { }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<ActiveSessionChatModel>>> getSessionChatDetail() {
        return new NetworkBoundResource<List<ActiveSessionChatModel>, List<ActiveSessionChatModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {return false;}

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ActiveSessionChatModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getSessionChat());
            }

            @Override
            protected void saveCallResult(List<ActiveSessionChatModel> data) {}
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postMessage(ObjectNode data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postCustomerMsg(data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postServiceMessage(ObjectNode data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postCustomerServiceMsg(data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postConcern(ObjectNode data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postOrderConcern(data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<SessionCustomerModel>> getSelfPresence() {
        return new NetworkBoundResource<SessionCustomerModel, SessionCustomerModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<SessionCustomerModel>> createCall() {
                    return new RetrofitLiveData<>(mWebService.getSelfPresence());
            }

            @Override
            protected void saveCallResult(SessionCustomerModel data) {
            }
        }.getAsLiveData();
    }
}
