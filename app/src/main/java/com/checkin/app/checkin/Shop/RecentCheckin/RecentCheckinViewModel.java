package com.checkin.app.checkin.Shop.RecentCheckin;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Session.SessionRepository;
import com.checkin.app.checkin.Shop.RecentCheckin.Model.RecentCheckinModel;

public class RecentCheckinViewModel extends BaseViewModel {
    private SessionRepository mRepository;
    private MediatorLiveData<Resource<RecentCheckinModel>> mRecentCheckinData = new MediatorLiveData<>();

    public RecentCheckinViewModel(@NonNull Application application) {
        super(application);
        mRepository = SessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {

    }

    public LiveData<Resource<RecentCheckinModel>> getRecentCheckinData() {
        return mRecentCheckinData;
    }

    public void fetchRecentCheckins(long shopId) {
        mRecentCheckinData.addSource(mRepository.getRecentCheckins(shopId), mRecentCheckinData::setValue);
    }
}