package com.checkin.app.checkin.Search;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.Resource.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jogi Miglani on 29-10-2018.
 */

public class SearchViewModel extends BaseViewModel {
    private SearchRepository mRepository;
    private MediatorLiveData<Resource<List<SearchResultModel>>> mResults = new MediatorLiveData<>();
    private LiveData<Resource<List<SearchResultModel>>> mPrevResults;

    private final Handler mHandler =  new Handler();
    private Runnable mRunnable;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        mRepository=new SearchRepository(application.getApplicationContext());
    }

    @Override
    public void updateResults() { }

    public void getSearchResults(String query) {
        if (query == null || query.isEmpty())
            return;
        if (mRunnable != null)
            mHandler.removeCallbacks(mRunnable);
        mRunnable = () -> {
            if (mPrevResults != null)
                mResults.removeSource(mPrevResults);
            mPrevResults = mRepository.getSearchResults(query);
            mResults.addSource(mPrevResults, mResults::setValue);
        };
        mHandler.postDelayed(mRunnable, 500);
    }

    public LiveData<Resource<List<SearchResultModel>>> getPeopleResults(){
        return Transformations.map(mResults, resource -> {
            List<SearchResultModel> resultData = new ArrayList<>();
            if (resource.status == Status.SUCCESS && resource.data != null) {
                for (SearchResultModel inputResult: resource.data) {
                    if (inputResult.isTypePeople())
                        resultData.add(inputResult);
                }
            }
            return Resource.cloneResource(resource, resultData);
        });
    }

    public LiveData<Resource<List<SearchResultModel>>> getRestaurantResults(){
        return Transformations.map(mResults, resource -> {
            List<SearchResultModel> resultData = new ArrayList<>();
            if (resource.status == Status.SUCCESS && resource.data != null) {
                for (SearchResultModel inputResult: resource.data) {
                    if (inputResult.isTypeRestaurant())
                        resultData.add(inputResult);
                }
            }
            return Resource.cloneResource(resource, resultData);
        });
    }

    public LiveData<Resource<List<SearchResultModel>>> getAllResults(){
        return mResults;
    }
}
