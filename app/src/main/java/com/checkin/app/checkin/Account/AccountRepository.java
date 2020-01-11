package com.checkin.app.checkin.Account;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.data.network.ApiClient;
import com.checkin.app.checkin.data.network.ApiResponse;
import com.checkin.app.checkin.data.BaseRepository;
import com.checkin.app.checkin.data.resource.NetworkBoundResource;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.data.network.RetrofitLiveData;
import com.checkin.app.checkin.data.network.WebApiService;

import java.util.List;

public class AccountRepository extends BaseRepository {
    private static AccountRepository INSTANCE;
    private WebApiService mWebService;

    private AccountRepository(Context context) {
        mWebService = ApiClient.Companion.getApiService(context);
    }

    public static AccountRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (AccountRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AccountRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<List<AccountModel>>> getSelfAccounts() {
        return new NetworkBoundResource<List<AccountModel>, List<AccountModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<AccountModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getSelfAccounts());
            }

            @Override
            protected void saveCallResult(List<AccountModel> data) {

            }
        }.getAsLiveData();
    }
}
