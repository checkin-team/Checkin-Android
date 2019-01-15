package com.checkin.app.checkin.Session.ActiveSession.ActiveSessionChat;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.Serializable;
import java.util.List;

public class ActiveSessionChatViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;
    private MediatorLiveData<Resource<List<ActiveSessionChatModel>>> mChatData = new MediatorLiveData<>();

    ActiveSessionChatViewModel(@NonNull Application application) {
        super(application);
        mRepository = ActiveSessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        getSessionChat();
    }

    public LiveData<Resource<List<ActiveSessionChatModel>>> getSessionChat() {
        mChatData.addSource(mRepository.getSessionChatDetail(),mChatData::setValue);
        return mChatData;
    }

    public void sendMessage(String msg, Object service) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        ActiveSessionCustomChatDataModel.CHATSERVICETYPES type = (ActiveSessionCustomChatDataModel.CHATSERVICETYPES) service;
        if(service.equals(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_NONE)){
            data.put("message",msg);
            mData.addSource(mRepository.postMessage(data), mData::setValue);
        } else{
            data.put("message",msg);
            data.put("service", type.tag);
//            if(service.equals(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_CALL_WAITER))
//            data.put("service", type.tag);
//            else if(service.equals(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_CLEAN_TABLE))
//            data.put("service", "2");
//            else /*if(service.equals(ActiveSessionCustomChatDataModel.CHATSERVICETYPES.SERVICE_CALL_WAITER))*/
//            data.put("service", "3");
            mData.addSource(mRepository.postServiceMessage(data), mData::setValue);
        }
    }

    public void raiseConcern(int concern, String message,int event_id) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("concern",concern);
        data.put("message",message);
        data.put("event_id",event_id);
        mData.addSource(mRepository.postConcern(data), mData::setValue);
    }
}
