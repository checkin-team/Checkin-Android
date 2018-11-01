package com.checkin.app.checkin.Misc;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.RestaurantActivity.Waiter.EventModel;
import com.checkin.app.checkin.RestaurantActivity.Waiter.EventRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class ItemViewModel extends BaseViewModel {
    private EventRepository mRepository;
    private MediatorLiveData<Resource<List<EventModel>>> mItems=new MediatorLiveData<>();
    private long tableId;

    public ItemViewModel(@NonNull Application application) {
        super(application);
        mRepository= EventRepository.getInstance(application);
    }

    public LiveData<Resource<List<EventModel>>> getItems(){
            mItems.addSource(mRepository.getItems(tableId), mItems::setValue);
        return mItems;
    }

    public void setTablePk(long tableId) {
        this.tableId = tableId;
    }


    @Override
    public void updateResults() {
        getItems();
    }

    public void setItemStatus(long tableId,long itemPk, String message)
    {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("of_table", tableId);
        data.put("item_name", itemPk);
        data.put("status", message);
        mData.addSource(mRepository.postItemCompleted(tableId,data), mData::setValue);
    }


    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ItemViewModel(mApplication);
        }
    }


}
