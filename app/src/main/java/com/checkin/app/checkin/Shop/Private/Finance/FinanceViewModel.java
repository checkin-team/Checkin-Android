package com.checkin.app.checkin.Shop.Private.Finance;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.GenericDetailModel;

public class FinanceViewModel extends BaseViewModel {
    private FinanceRepository financeRepository;

    private long restaurantId;
    private MediatorLiveData<Resource<FinanceModel>> mGetFinanceMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericDetailModel>> mSetFinanceMediatorLiveData = new MediatorLiveData<>();

    public FinanceViewModel(@NonNull Application application) {
        super(application);
        financeRepository = FinanceRepository.getInstance(application);
    }

    public void getRestaurantFinanceById(long restaurantId){
        this.restaurantId = restaurantId;
        LiveData<Resource<FinanceModel>> mLiveData = financeRepository.getRestaurantFinanceById(restaurantId);
        mGetFinanceMediatorLiveData.addSource(mLiveData,mGetFinanceMediatorLiveData::setValue);
    }

    public LiveData<Resource<FinanceModel>> getRestaurantFinanceModel(){
        return mGetFinanceMediatorLiveData;
    }

    public void updateDiscountPercent(double discount) {
        Resource<FinanceModel> resource = mGetFinanceMediatorLiveData.getValue();
        if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
            FinanceModel data = resource.data;
            data.setDiscountPercent(discount);
            setRestaurantFinanceById(data, restaurantId);
        }
    }

    private void setRestaurantFinanceById(FinanceModel financeModel, long restaurantId){
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
