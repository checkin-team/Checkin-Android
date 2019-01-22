package com.checkin.app.checkin.Shop.ShopInvoice;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;

import java.util.List;

public class ShopSessionViewModel extends BaseViewModel {

    private ShopInvoiceRepository mShopInvoiceRepository;
    private MediatorLiveData<Resource<ShopSessionDetailModel>> shopSessionDetailModelMediatorLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<ShopSessionFeedbackModel>>> shopSessionFeedbackModelMediatorLiveData = new MediatorLiveData<>();

    public ShopSessionViewModel(@NonNull Application application) {
        super(application);
        mShopInvoiceRepository = ShopInvoiceRepository.getInstance(application);
    }

    public void getShopSessionFeedbackById(String sessionId){
        LiveData<Resource<List<ShopSessionFeedbackModel>>> shopSessionFeedbackModelLiveData = mShopInvoiceRepository.getShopSessionFeedbackById(sessionId);
        shopSessionFeedbackModelMediatorLiveData.addSource(shopSessionFeedbackModelLiveData,shopSessionFeedbackModelMediatorLiveData::setValue);
    }

    public LiveData<Resource<List<ShopSessionFeedbackModel>>> getShopSessionFeedbackModel(){
        return shopSessionFeedbackModelMediatorLiveData;
    }

    public void getShopSessionDetailById(String sessionId){
        LiveData<Resource<ShopSessionDetailModel>> shopSessionDetailModelLiveData = mShopInvoiceRepository.getShopSessionDetailById(sessionId);
        shopSessionDetailModelMediatorLiveData.addSource(shopSessionDetailModelLiveData,shopSessionDetailModelMediatorLiveData::setValue);
    }

    public LiveData<Resource<ShopSessionDetailModel>> getShopSessioDetailModel(){
        return shopSessionDetailModelMediatorLiveData;
    }

    @Override
    public void updateResults() {
    }
}
