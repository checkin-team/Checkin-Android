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
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.SessionInvoiceModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
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

    public LiveData<Resource<SessionInvoiceModel>> getActiveSessionInvoice() {
        return new NetworkBoundResource<SessionInvoiceModel, SessionInvoiceModel>() {

            @Override
            protected void saveCallResult(SessionInvoiceModel data) {
            }

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<SessionInvoiceModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.getActiveSessionInvoice());
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

    public LiveData<Resource<List<SessionOrderedItemModel>>> getSessionOrdersDetails() {
        return new NetworkBoundResource<List<SessionOrderedItemModel>, List<SessionOrderedItemModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {return false;}

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SessionOrderedItemModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getActiveSessionOrders());
            }

            @Override
            protected void saveCallResult(List<SessionOrderedItemModel> data) {}
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

    public LiveData<Resource<ObjectNode>> removeSessionOrder(long order_id) {
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

    public LiveData<Resource<List<SessionChatModel>>> getSessionChatDetail() {
        return new NetworkBoundResource<List<SessionChatModel>, List<SessionChatModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {return false;}

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SessionChatModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getCustomerSessionChat());
            }

            @Override
            protected void saveCallResult(List<SessionChatModel> data) {}
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
                return new RetrofitLiveData<>(mWebService.postCustomerMessage(data));
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
                return new RetrofitLiveData<>(mWebService.postCustomerRequestService(data));
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

    public LiveData<Resource<ObjectNode>> postRequestCheckout(ObjectNode data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postCustomerRequestCheckout(data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postSessionMember(String userId) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postSessionMember(userId));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> deleteSessionMember(String userId) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.deleteSessionMember(userId));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }
}
