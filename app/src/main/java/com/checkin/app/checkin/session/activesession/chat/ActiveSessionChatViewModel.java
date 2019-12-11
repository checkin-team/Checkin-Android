package com.checkin.app.checkin.session.activesession.chat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Utility.SourceMappedLiveData;
import com.checkin.app.checkin.session.activesession.ActiveSessionRepository;
import com.checkin.app.checkin.session.activesession.chat.SessionChatDataModel.EVENT_REQUEST_SERVICE_TYPE;
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel.CHAT_STATUS_TYPE;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class ActiveSessionChatViewModel extends BaseViewModel {
    private final ActiveSessionRepository mRepository;
    private SourceMappedLiveData<Resource<List<SessionChatModel>>> mChatData = createNetworkLiveData();

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
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode();
        EVENT_REQUEST_SERVICE_TYPE type = (EVENT_REQUEST_SERVICE_TYPE) service;
        if (type == EVENT_REQUEST_SERVICE_TYPE.SERVICE_NONE) {
            data.put("message", msg);
            getMData().addSource(mRepository.postMessage(data), getMData()::setValue);
        } else {
            data.put("message", msg);
            data.put("service", type.tag);
            getMData().addSource(mRepository.postServiceMessage(data), getMData()::setValue);
        }
    }

    public void raiseConcern(SessionChatDataModel.EVENT_CONCERN_TYPE concern, int eventId) {
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode();
        data.put("concern", concern.tag);
        data.put("event_id", eventId);
        getMData().addSource(mRepository.postConcern(data), getMData()::setValue);
    }

    public void addNewEvent(SessionChatModel chatModel) {
        Resource<List<SessionChatModel>> listResource = mChatData.getValue();
        if (listResource == null || listResource.getData() == null)
            return;
        listResource.getData().add(0, chatModel);
        mChatData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
    }

    public void updateEventStatus(long eventPk, CHAT_STATUS_TYPE status) {
        Resource<List<SessionChatModel>> listResource = mChatData.getValue();
        if (listResource == null || listResource.getData() == null)
            return;
        int pos = -1;
        for (int i = 0, count = listResource.getData().size(); i < count; i++) {
            if (listResource.getData().get(i).getPk() == eventPk) {
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            SessionChatModel chatModel = listResource.getData().get(pos);
            chatModel.setStatus(status.tag);
            listResource.getData().remove(pos);
            listResource.getData().add(0, chatModel);
            mChatData.setValue(Resource.Companion.cloneResource(listResource, listResource.getData()));
        }
    }
}
