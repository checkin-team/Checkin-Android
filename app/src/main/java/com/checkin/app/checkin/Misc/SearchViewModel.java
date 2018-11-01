package com.checkin.app.checkin.Misc;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jogi Miglani on 29-10-2018.
 */

public class SearchViewModel extends BaseViewModel {
    private SearchRepository mRepository;
    private MediatorLiveData<Resource<List<SearchRVPojo>>> mResults = new MediatorLiveData<>();
    private LiveData<Resource<List<SearchRVPojo>>> mPrevResults;
    private final Handler mHandler =  new Handler();
    private Runnable mRunnable;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        mRepository=new SearchRepository(application.getApplicationContext());
    }

    @Override
    public void updateResults() {
    }

    public void getSearchResults(String query) {
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

    public LiveData<Resource<List<SearchRVPojo>>> getPeople(){
        return Transformations.map(mResults, input -> {
            List<SearchRVPojo> data=new ArrayList<>();
            if(mResults.getValue().status.equals(Resource.Status.SUCCESS)&&(!mResults.getValue().data.isEmpty()))
            {
                for(int i=0;i<mResults.getValue().data.size();i++)
                {
                    if(mResults.getValue().data.get(i).getType()== SearchRVPojo.RESULT_TYPE.PEOPLE)
                    {
                        data.add(mResults.getValue().data.get(i));
                    }
                }

            }

            Resource<List<SearchRVPojo>> result = Resource.cloneResource(input, data);
            return result;
        });
    }

    public LiveData<Resource<List<SearchRVPojo>>> getRestaurants(){
        return Transformations.map(mResults, input -> {
            List<SearchRVPojo> data=new ArrayList<>();
            if(mResults.getValue().status.equals(Resource.Status.SUCCESS)&&(!mResults.getValue().data.isEmpty()))
            {
                for(int i=0;i<mResults.getValue().data.size();i++)
                {
                    if(mResults.getValue().data.get(i).getType()== SearchRVPojo.RESULT_TYPE.RESTAURANT)
                    {
                        data.add(mResults.getValue().data.get(i));
                    }
                }

            }
            Resource<List<SearchRVPojo>> result = Resource.cloneResource(input, data);
            return result;
        });
    }
    public LiveData<Resource<List<SearchRVPojo>>> getAll(){
        return Transformations.map(mResults, input -> {
            Resource<List<SearchRVPojo>> result = Resource.cloneResource(input, mResults.getValue().data);
            return result;
        });
    }





    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new SearchViewModel(mApplication);
        }
    }
}
