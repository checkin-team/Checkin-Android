package com.checkin.app.checkin.Shop.RecentCheckin;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Session.SessionRepository;
import com.checkin.app.checkin.Shop.RecentCheckin.Model.RecentCheckinModel;
import com.checkin.app.checkin.Shop.RecentCheckin.Model.UserCheckinModel;
import com.checkin.app.checkin.User.UserModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public void fetchRecentCheckins(String shopId) {
        mRecentCheckinData.addSource(mRepository.getRecentCheckins(shopId), mRecentCheckinData::setValue);
    }

    public void setDummyData() {
        List<UserCheckinModel> userCheckinModels = new ArrayList<>();
        userCheckinModels.add(new UserCheckinModel(new Date(), UserModel.GENDER.MALE, true, new BriefModel("1", "Alex", null)));
        userCheckinModels.add(new UserCheckinModel(new Date(), UserModel.GENDER.FEMALE, true, new BriefModel("2", "Alice", null)));
        userCheckinModels.add(new UserCheckinModel(new Date(), UserModel.GENDER.MALE, true, new BriefModel("3", "Bob", "https://storage.googleapis.com/checkin-app-18.appspot.com/images/users/amide/profile_a825.jpg")));
        userCheckinModels.add(new UserCheckinModel(new Date(), UserModel.GENDER.MALE, false, new BriefModel("4", "Carlos", null)));
        userCheckinModels.add(new UserCheckinModel(new Date(), UserModel.GENDER.FEMALE, false, new BriefModel("5", "Cath", null)));
        RecentCheckinModel model = new RecentCheckinModel(24,12,12, userCheckinModels);
        mRecentCheckinData.setValue(Resource.success(model));
    }
}