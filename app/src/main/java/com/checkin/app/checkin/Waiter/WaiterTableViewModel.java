package com.checkin.app.checkin.Waiter;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Session.ActiveSession.Chat.SessionChatModel.CHAT_STATUS_TYPE;
import com.checkin.app.checkin.Session.Model.SessionBriefModel;
import com.checkin.app.checkin.Session.SessionRepository;
import com.checkin.app.checkin.Waiter.Model.WaiterEventModel;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class WaiterTableViewModel extends BaseViewModel {
    private WaiterRepository mWaiterRepository;
    private SessionRepository mSessionRepository;

    private MediatorLiveData<Resource<SessionBriefModel>> mSessionDetail = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<WaiterEventModel>>> mEventData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<GenericDetailModel>> mEventUpdate = new MediatorLiveData<>();

    private long mSessionPk;

    public WaiterTableViewModel(@NonNull Application application) {
        super(application);
        mWaiterRepository = WaiterRepository.getInstance(application);
        mSessionRepository = SessionRepository.getInstance(application);
    }

    public void fetchSessionDetail(long sessionId) {
        mSessionPk = sessionId;
        mSessionDetail.addSource(mSessionRepository.getSessionBriefDetail(sessionId), mSessionDetail::setValue);
    }

    public void fetchTableEvents() {
        mEventData.addSource(mWaiterRepository.getWaiterEventsForTable(mSessionPk), mEventData::setValue);
    }

    public LiveData<Resource<SessionBriefModel>> getSessionDetail() {
        return mSessionDetail;
    }

    public LiveData<Resource<List<WaiterEventModel>>> getActiveTableEvents() {
        return Transformations.map(mEventData, input -> {
            if (input == null || input.data == null)
                return input;
            List<WaiterEventModel> result = new ArrayList<>();
            if (input.status == Resource.Status.SUCCESS) {
                for (WaiterEventModel eventModel: input.data) {
                    if (eventModel.getStatus() == CHAT_STATUS_TYPE.OPEN || eventModel.getStatus() == CHAT_STATUS_TYPE.IN_PROGRESS)
                        result.add(eventModel);
                }
                return Resource.cloneResource(input, result);
            }
            return input;
        });
    }

    public LiveData<Resource<List<WaiterEventModel>>> getDeliveredTableEvents() {
        return Transformations.map(mEventData, input -> {
            if (input == null || input.data == null)
                return input;
            List<WaiterEventModel> result = new ArrayList<>();
            if (input.status == Resource.Status.SUCCESS) {
                for (WaiterEventModel eventModel: input.data) {
                    if (eventModel.getStatus() == CHAT_STATUS_TYPE.DONE)
                        result.add(eventModel);
                }
                return Resource.cloneResource(input, result);
            }
            return input;
        });
    }


    public long getSessionPk() {
        return mSessionPk;
    }

    public void updateOrderStatus(long orderId, CHAT_STATUS_TYPE statusType) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("status", statusType.tag);
        mData.addSource(mWaiterRepository.changeOrderStatus(orderId, data), mData::setValue);
    }

    public void markEventDone(long eventId) {
        mEventUpdate.addSource(mWaiterRepository.markEventDone(eventId), mEventUpdate::setValue);
    }

    public LiveData<Resource<GenericDetailModel>> getEventUpdate() {
        return mEventUpdate;
    }

    @Override
    public void updateResults() {
        fetchTableEvents();
    }
}
