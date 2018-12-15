package com.checkin.app.checkin.Shop.RecentCheckin;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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



        Date d1 = new Date();

        List<UserCheckinModel> list_userdetail = new ArrayList<>();

        BriefModel briefModel = new BriefModel("12","abc", null);

        BriefModel briefModel_two = new BriefModel("1","Ale", null);
        UserCheckinModel userdetail = new UserCheckinModel(d1,UserModel.GENDER.MALE,true,briefModel);
        UserCheckinModel userdetail_one = new UserCheckinModel(d1,UserModel.GENDER.FEMALE,false,briefModel_two);
         list_userdetail.add(userdetail);
         list_userdetail.add(userdetail_one);


        RecentCheckinModel model = new RecentCheckinModel(24,12,12,list_userdetail);

        mRecentCheckinData.setValue(Resource.success(model));


    }
}