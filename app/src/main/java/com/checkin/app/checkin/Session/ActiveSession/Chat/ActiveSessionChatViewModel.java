package com.checkin.app.checkin.Session.ActiveSession.Chat;

import android.app.Application;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Session.ActiveSession.ActiveSessionRepository;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class ActiveSessionChatViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;
    private MediatorLiveData<Resource<List<SessionChatModel>>> mChatData = new MediatorLiveData<>();

    public ActiveSessionChatViewModel(@NonNull Application application) {
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

    public void raiseConcern(SessionChatDataModel.EVENT_CONCERN_TYPE concern, int eventId) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("concern", concern.tag);
        data.put("event_id", eventId);
        mData.addSource(mRepository.postConcern(data), mData::setValue);
    }

    public void addNewEvent(SessionChatModel chatModel) {
        Resource<List<SessionChatModel>> listResource = mChatData.getValue();
        if (listResource == null || listResource.data == null)
            return;
        listResource.data.add(0, chatModel);
        mChatData.setValue(Resource.cloneResource(listResource, listResource.data));
    }

    public void updateEventStatus(long eventPk, CHAT_STATUS_TYPE status) {
        Resource<List<SessionChatModel>> listResource = mChatData.getValue();
        if (listResource == null || listResource.data == null)
            return;
        int pos = -1;
        for (int i = 0, count = listResource.data.size(); i < count; i++) {
            if (listResource.data.get(i).getPk() == eventPk) {
                pos = i;
                break;
            }
        }

        if (pos > -1) {
            SessionChatModel chatModel = listResource.data.get(pos);
            chatModel.setStatus(status.tag);
            listResource.data.remove(pos);
            listResource.data.add(0, chatModel);

            mChatData.setValue(Resource.cloneResource(listResource, listResource.data));
        }
    }
}
