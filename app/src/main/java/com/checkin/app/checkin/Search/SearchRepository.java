package com.checkin.app.checkin.Search;

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

/**
 * Created by Jogi Miglani on 29-10-2018.
 */

public class SearchRepository extends BaseRepository {
    private final WebApiService mWebService;

    public SearchRepository(Context context) {
        mWebService = ApiClient.Companion.getApiService(context);
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
