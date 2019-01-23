package com.checkin.app.checkin.Session.ActiveSession.Chat;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionRepository;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class ActiveSessionChatViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;
    private MediatorLiveData<Resource<List<SessionChatModel>>> mChatData = new MediatorLiveData<>();

    ActiveSessionChatViewModel(@NonNull Application application) {
        super(application);
        mRepository = ActiveSessionRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        fetchSessionChat();
    }

    public void fetchSessionChat() {
        mChatData.addSource(mRepository.getSessionChatDetail(), mChatData::setValue);
    }

    public LiveData<Resource<List<SessionChatModel>>> getSessionChat() {
        return mChatData;
    }

    public void sendMessage(String msg, Object service) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        EVENT_REQUEST_SERVICE_TYPE type = (EVENT_REQUEST_SERVICE_TYPE) service;
        if (type == EVENT_REQUEST_SERVICE_TYPE.SERVICE_NONE) {
            data.put("message", msg);
            mData.addSource(mRepository.postMessage(data), mData::setValue);
        } else {
            data.put("message", msg);
            data.put("service", type.tag);
            mData.addSource(mRepository.postServiceMessage(data), mData::setValue);
        }
    }

    public void raiseConcern(int concern, String message, int event_id) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("concern", concern);
        data.put("message", message);
        data.put("event_id", event_id);
        mData.addSource(mRepository.postConcern(data), mData::setValue);
    }
}