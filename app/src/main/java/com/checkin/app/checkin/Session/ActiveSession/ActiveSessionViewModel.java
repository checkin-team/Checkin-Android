package com.checkin.app.checkin.Session.ActiveSession;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Menu.OrderedItemModel;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * Created by Bhavik Patel on 05/08/2018.
 */

public class ActiveSessionViewModel extends BaseViewModel {
    private static final String TAG = ActiveSessionViewModel.class.getSimpleName();
    private final ActiveSessionRepository mRepository;
    private MediatorLiveData<Resource<ActiveSessionModel>> mSessionData = new MediatorLiveData<>();

    ActiveSessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = ActiveSessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        getActiveSessionDetail();
    }

    public LiveData<Resource<ActiveSessionModel>> getActiveSessionDetail() {
        mSessionData.addSource(mRepository.getActiveSessionDetail(), mSessionData::setValue);
        return mSessionData;
    }

    public LiveData<List<OrderedItemModel>> getOrderedItems() {
        return Transformations.map(getActiveSessionDetail(), input -> {
            List<OrderedItemModel> orderedItems = null;
            if (input != null && input.data != null) {
                orderedItems = input.data.getOrderedItems();
            }
            return orderedItems;
        });
    }

    public void cancelOrders(long... ids) { }

    public void checkoutSession() {
        Log.e("Session", "Checked out");
    }

    public void addMembers(List<Long> ids) {
        ArrayNode value = Converters.objectMapper.valueToTree(ids);
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.putArray("users").addAll(value);
        mData.addSource(mRepository.postAddMembers(data), mData::setValue);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ActiveSessionViewModel(mApplication);
        }
    }
}
