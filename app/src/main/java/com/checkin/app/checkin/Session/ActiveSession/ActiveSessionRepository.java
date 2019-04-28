package com.checkin.app.checkin.Session.ActiveSession;

import android.app.Application;
import android.content.Context;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel;
import com.checkin.app.checkin.Session.Model.ActiveSessionModel;
import com.checkin.app.checkin.Session.Model.CheckoutStatusModel;
import com.checkin.app.checkin.Session.Model.SessionInvoiceModel;
import com.checkin.app.checkin.Session.Model.SessionOrderedItemModel;
import com.checkin.app.checkin.Session.Paytm.PaytmModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

/**
 * Created by Bhavik Patel on 04/08/2018.
 */

public class ActiveSessionRepository extends BaseRepository {

    private static ActiveSessionRepository INSTANCE;
    private final WebApiService mWebService;

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
            protected void saveCallResult(ActiveSessionModel data) {
            }

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

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
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SessionOrderedItemModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getActiveSessionOrders());
            }

            @Override
            protected void saveCallResult(List<SessionOrderedItemModel> data) {
            }
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

    public LiveData<Resource<ObjectNode>> removeSessionOrder(long orderId) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.deleteSessionOrder(orderId));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<SessionChatModel>>> getSessionChatDetail() {
        return new NetworkBoundResource<List<SessionChatModel>, List<SessionChatModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SessionChatModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getCustomerSessionChat());
            }

            @Override
            protected void saveCallResult(List<SessionChatModel> data) {
            }
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

    public LiveData<Resource<CheckoutStatusModel>> postRequestCheckout(ObjectNode data) {
        return new NetworkBoundResource<CheckoutStatusModel, CheckoutStatusModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<CheckoutStatusModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.postCustomerRequestCheckout(data));
            }

            @Override
            protected void saveCallResult(CheckoutStatusModel data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<PaytmModel>> postPaytmDetailRequest() {
        return new NetworkBoundResource<PaytmModel, PaytmModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<PaytmModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.postPaytmRequest());
            }

            @Override
            protected void saveCallResult(PaytmModel data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postPaytmResult(ObjectNode data) {
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postPaytmCallback(data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericDetailModel>> acceptSessionMemberRequest(String userId) {
        return new NetworkBoundResource<GenericDetailModel, GenericDetailModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericDetailModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.postActiveSessionCustomerRequest(userId));
            }

            @Override
            protected void saveCallResult(GenericDetailModel data) {
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<GenericDetailModel>> deleteSessionMember(String userId) {
        return new NetworkBoundResource<GenericDetailModel, GenericDetailModel>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GenericDetailModel>> createCall() {
                return new RetrofitLiveData<>(mWebService.deleteActiveSessionCustomer(userId));
            }

            @Override
            protected void saveCallResult(GenericDetailModel data) {
            }
        }.getAsLiveData();
    }
}
