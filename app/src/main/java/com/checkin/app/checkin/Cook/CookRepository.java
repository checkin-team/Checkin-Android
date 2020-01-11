package com.checkin.app.checkin.Cook;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.Cook.Model.CookTableModel;
import com.checkin.app.checkin.data.network.ApiClient;
import com.checkin.app.checkin.data.network.ApiResponse;
import com.checkin.app.checkin.data.BaseRepository;
import com.checkin.app.checkin.data.resource.NetworkBoundResource;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.data.network.RetrofitLiveData;
import com.checkin.app.checkin.data.network.WebApiService;

import java.util.List;

/**
 * Created by Shivansh Saini on 24/01/2019.
 */

public class CookRepository extends BaseRepository {
    private static CookRepository INSTANCE;
    private final WebApiService mWebService;

    private CookRepository(Context context) {
        mWebService = ApiClient.Companion.getApiService(context);
    }

    public static CookRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (CookRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CookRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<List<CookTableModel>>> getActiveTables(long shopId) {
        return new NetworkBoundResource<List<CookTableModel>, List<CookTableModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<CookTableModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getCookActiveTables(shopId));
            }

            @Override
            protected void saveCallResult(List<CookTableModel> data) {

            }
        }.getAsLiveData();
    }
}
