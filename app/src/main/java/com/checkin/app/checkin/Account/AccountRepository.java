package com.checkin.app.checkin.Account;

import android.app.Application;
import android.content.Context;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class AccountRepository extends BaseRepository {
    private static AccountRepository INSTANCE;
    private WebApiService mWebService;

    private AccountRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
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
}
