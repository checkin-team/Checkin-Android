package com.checkin.app.checkin.Search;

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

import java.util.List;

/**
 * Created by Jogi Miglani on 29-10-2018.
 */

public class SearchRepository extends BaseRepository {
    private final WebApiService mWebService;

    public SearchRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
    }

    public LiveData<Resource<List<SearchResultPeopleModel>>> getSearchPeopleResults(String query, String friendshipStatus) {
        return new NetworkBoundResource<List<SearchResultPeopleModel>, List<SearchResultPeopleModel>>() {

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }


            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SearchResultPeopleModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getSearchPeopleResults(query, friendshipStatus));
            }

            @Override
            protected void saveCallResult(List<SearchResultPeopleModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<SearchResultShopModel>>> getSearchShopResults(String query, Boolean hasNonVeg, Boolean hasAlcohol) {
        return new NetworkBoundResource<List<SearchResultShopModel>, List<SearchResultShopModel>>() {
            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SearchResultShopModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getSearchShopResults(query, hasNonVeg, hasAlcohol));
            }

            @Override
            protected void saveCallResult(List<SearchResultShopModel> data) {

            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<SearchResultShopModel>>> getSearchShopResults(String query) {
        return getSearchShopResults(query, null, null);
    }
}
