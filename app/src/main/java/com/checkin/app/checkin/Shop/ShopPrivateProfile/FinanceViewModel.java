package com.checkin.app.checkin.Shop.ShopPrivateProfile;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.GenericDetailModel;

public class FinanceViewModel extends BaseViewModel {

    private String restaurantId;
    private FinanceRepository financeRepository;
    private MediatorLiveData<Resource<FinanceModel>> mGetFinanceMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericDetailModel>> mSetFinanceMediatorLiveData = new MediatorLiveData<>();

    public FinanceViewModel(@NonNull Application application) {
        super(application);
        financeRepository = FinanceRepository.getInstance(application);
    }

    void getRestaurantFinanceById(String restaurantId){
        this.restaurantId = restaurantId;
        LiveData<Resource<FinanceModel>> mLiveData = financeRepository.getRestaurantFinanceById(restaurantId);
        mGetFinanceMediatorLiveData.addSource(mLiveData,mGetFinanceMediatorLiveData::setValue);
    }

    LiveData<Resource<FinanceModel>> getRestaurantFinanceModel(){
        return mGetFinanceMediatorLiveData;
    }

    void updateDiscountPercent(double discount) {
        Resource<FinanceModel> resource = mGetFinanceMediatorLiveData.getValue();
        if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
            FinanceModel data = resource.data;
            data.setDiscountPercent(discount);
            setRestaurantFinanceById(data, restaurantId);
        }
    }

    private void setRestaurantFinanceById(FinanceModel financeModel, String restaurantId){
        LiveData<Resource<GenericDetailModel>> mLiveData = financeRepository.setRestaurantFinanceById(financeModel,restaurantId);
        mSetFinanceMediatorLiveData.addSource(mLiveData,mSetFinanceMediatorLiveData::setValue);
    }

    LiveData<Resource<GenericDetailModel>> getUpdateFinanceData(){
        return mSetFinanceMediatorLiveData;
    }

    @Override
    public void updateResults() {
    }
}
