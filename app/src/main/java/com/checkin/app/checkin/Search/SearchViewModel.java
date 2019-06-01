package com.checkin.app.checkin.Search;

import android.app.Application;
import android.os.Handler;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.Resource.Status;
import com.checkin.app.checkin.Data.SingleSourceMediatorLiveData;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

/**
 * Created by Jogi Miglani on 29-10-2018.
 */

public class SearchViewModel extends BaseViewModel {
    private final Handler mHandler = new Handler();
    private SearchRepository mRepository;
    @Nullable
    private SingleSourceMediatorLiveData<Resource<List<SearchResultPeopleModel>>> mPeopleResults;
    @Nullable
    private SingleSourceMediatorLiveData<Resource<List<SearchResultShopModel>>> mShopResults;
    private MediatorLiveData<Resource<List<SearchResultModel>>> mResults = new MediatorLiveData<>();
    private Runnable mRunnable;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        mRepository = new SearchRepository(application.getApplicationContext());
    }

    @Override
    protected void registerProblemHandlers() {
        mResults = registerProblemHandler(mResults);
    }

    private void combineResults() {
        Resource<List<SearchResultPeopleModel>> resourcePeople = null;
        Resource<List<SearchResultShopModel>> resourceShop = null;
        Resource<List<SearchResultModel>> result = null;
        if (mPeopleResults != null)
            resourcePeople = mPeopleResults.getValue();
        if (mShopResults != null)
            resourceShop = mShopResults.getValue();
        if (resourcePeople != null && resourceShop != null) {
            if (resourcePeople.status == Status.LOADING || resourceShop.status == Status.LOADING)
                result = Resource.loading(null);
            else if (resourcePeople.data != null && resourceShop.data != null) {
                List<SearchResultModel> data = new ArrayList<>();
                data.addAll(resourcePeople.data);
                data.addAll(resourceShop.data);
                result = Resource.success(data);
            }
        } else if (resourcePeople != null && resourcePeople.data != null) {
            List<SearchResultModel> data = new ArrayList<>(resourcePeople.data);
            result = Resource.success(data);
        } else if (resourceShop != null && resourceShop.data != null) {
            List<SearchResultModel> data = new ArrayList<>(resourceShop.data);
            result = Resource.success(data);
        }
        mResults.setValue(result);
    }

    @Override
    public void updateResults() {
    }

    public void getSearchResults(String query) {
        if (query == null || query.isEmpty())
            return;
        if (mRunnable != null)
            mHandler.removeCallbacks(mRunnable);
        mRunnable = () -> {
            if (mPeopleResults != null) {
                mResults.removeSource(mPeopleResults);
                mPeopleResults.observeSource(
                        mRepository.getSearchPeopleResults(query, null), mPeopleResults::setValue);
                mResults.addSource(mPeopleResults, listResource -> this.combineResults());
            }
            if (mShopResults != null) {
                mResults.removeSource(mShopResults);
                mShopResults.observeSource(
                        mRepository.getSearchShopResults(query), mShopResults::setValue);
                mResults.addSource(mShopResults, listResource -> this.combineResults());
            }
        };
        mHandler.postDelayed(mRunnable, 500);
    }

    public void setup(boolean searchPeople, boolean searchShop) {
        if (searchPeople)
            mPeopleResults = new SingleSourceMediatorLiveData<>();
        if (searchShop)
            mShopResults = new SingleSourceMediatorLiveData<>();
    }

    public LiveData<Resource<List<SearchResultPeopleModel>>> getPeopleResults() {
        if (mPeopleResults != null) {
            return Transformations.map(mPeopleResults, input -> {
                if (input != null && input.data != null && input.data.size() == 0)
                    return Resource.errorNotFound("Not found");
                return input;
            });
        }
        return null;
    }

    public LiveData<Resource<List<SearchResultShopModel>>> getRestaurantResults() {
        if (mShopResults != null) {
            return Transformations.map(mShopResults, input -> {
                if (input != null && input.data != null && input.data.size() == 0)
                    return Resource.errorNotFound("Not found");
                return input;
            });
        }
        return null;
    }

    public LiveData<Resource<List<SearchResultModel>>> getAllResults() {
        return Transformations.map(mResults, input -> {
            if (input != null && input.data != null && input.data.size() == 0)
                return Resource.errorNotFound("Not found");
            return input;
        });
    }
}
