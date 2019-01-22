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
import com.checkin.app.checkin.User.UserRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class EventRepository extends BaseRepository {

    private final WebApiService mWebService;
    private static EventRepository INSTANCE = null;

    private EventRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public LiveData<Resource<List<EventModel>>> getItems(long tableId){
        return new NetworkBoundResource<List<EventModel>, List<EventModel>>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<EventModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getItems(tableId));
            }

            @Override
            protected void saveCallResult(List<EventModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<ObjectNode>> postItemCompleted(long tableId,ObjectNode data){
        return new NetworkBoundResource<ObjectNode, ObjectNode>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ObjectNode>> createCall() {
                return new RetrofitLiveData<>(mWebService.postItemCompleted(tableId,data));
            }

            @Override
            protected void saveCallResult(ObjectNode data) {

            }
        }.getAsLiveData();
    }

    public static EventRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EventRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}





