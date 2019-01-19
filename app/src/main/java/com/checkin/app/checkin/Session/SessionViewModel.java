package com.checkin.app.checkin.Session;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Session.Model.SessionInvoiceModel;

public class SessionViewModel extends BaseViewModel {
    private SessionRepository mRepository;
    private MediatorLiveData<Resource<SessionInvoiceModel>> mInvoiceData = new MediatorLiveData<>();

    public SessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = SessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {

    }

    public LiveData<Resource<SessionInvoiceModel>> getInvoiceData() {
        return mInvoiceData;
    }

    public void getSessionInvoice(int sessionId) {
        mInvoiceData.addSource(mRepository.getSessionInvoiceDetail(sessionId), mInvoiceData::setValue);
    }

}
