package com.checkin.app.checkin.Shop.ShopReview;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BriefModel;
import com.checkin.app.checkin.Session.SessionRepository;

import java.util.ArrayList;
import java.util.List;

public class ShopReviewViewModel extends BaseViewModel {
    private SessionRepository mRepositry;
    private MediatorLiveData<Resource<List<ShopReviewModel>>> mShopReviewData = new MediatorLiveData<>();

    public ShopReviewViewModel(@NonNull Application application) {
        super(application);
        mRepositry = SessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {

    }

    public LiveData<Resource<List<ShopReviewModel>>> getReviewData()
   {
       return mShopReviewData;
   }
    public void setDummyData()
    {
        List<ShopReviewModel> list_userReview = new ArrayList<>();
        BriefModel briefModel_one = new BriefModel("1","Shubhanshu",null);
        ShopReviewModel model = new ShopReviewModel("Lorem Ipsum is simply dummy text of the printing",23,24,4,5, briefModel_one,3);
        ShopReviewModel model_one = new ShopReviewModel("Lorem Ipsum is simply dummy text of the printing",23,24,4,5, briefModel_one,3);
        ShopReviewModel model_two = new ShopReviewModel("Lorem Ipsum is simply dummy text of the printing",23,24,4,5, briefModel_one,3);

        list_userReview.add(model);
        list_userReview.add(model_one);
        list_userReview.add(model_two);
        mShopReviewData.setValue(Resource.success(list_userReview));
    }
}
