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

    public LiveData<Resource<List<SearchResultModel>>> getSearchResults(String query){
        return new NetworkBoundResource<List<SearchResultModel>, List<SearchResultModel>>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }


            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SearchResultModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getSearchResults(query));
            }

            @Override
            protected void saveCallResult(List<SearchResultModel> data) {

            }
        }.getAsLiveData();
    }
}
