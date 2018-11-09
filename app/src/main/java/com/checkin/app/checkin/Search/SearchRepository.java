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

    public LiveData<Resource<List<SearchModel>>> getSearchResults(String query){
        return new NetworkBoundResource<List<SearchModel>, List<SearchModel>>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return false;
            }


            @NonNull
            @Override
            protected LiveData<ApiResponse<List<SearchModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getSearchResults(query));
            }

            @Override
            protected void saveCallResult(List<SearchModel> data) {

            }
        }.getAsLiveData();
    }
}
