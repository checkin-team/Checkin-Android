package com.checkin.app.checkin.Shop.RecentCheckin;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Shop.RecentCheckin.Model.RecentCheckinModel;
import com.checkin.app.checkin.Shop.RecentCheckin.Model.UserCheckinModel;

import java.util.List;

public class RecentViewModel extends BaseViewModel {
    private MediatorLiveData<Resource<RecentCheckinModel>> mRecentCheckinData = new MediatorLiveData<>();

    public RecentViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void updateResults() {

    }
}
