package com.checkin.app.checkin.User.bills;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.data.BaseViewModel;
import com.checkin.app.checkin.data.resource.Resource;
import com.checkin.app.checkin.User.ReviewRepository;
import com.checkin.app.checkin.Utility.SourceMappedLiveData;
import com.checkin.app.checkin.session.SessionRepository;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserTransactionsViewModel extends BaseViewModel {
    private final SessionRepository mRepository;
    private final ReviewRepository mReviewRepository;

    private SourceMappedLiveData<Resource<UserTransactionBriefModel>> mBriefData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<UserTransactionDetailsModel>> mDetailData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<ObjectNode>> mReviewData = createNetworkLiveData();
    private long mSessionId;


    public UserTransactionsViewModel(@NonNull Application application) {
        super(application);
        mRepository = SessionRepository.Companion.getInstance(application);
        mReviewRepository = ReviewRepository.getInstance(application);
    }

    public void fetchSessionSuccessfulTransaction(long sessionId) {
        mSessionId = sessionId;
        mBriefData.addSource(mRepository.getUserSessionBriefDetail(sessionId), mBriefData::setValue);
    }

    public void fetchUserSessionDetail(long sessionId) {
        mDetailData.addSource(mRepository.getUserSessionDetail(sessionId), mDetailData::setValue);
    }

    public LiveData<Resource<UserTransactionBriefModel>> getUserSessionBriefData() {
        return mBriefData;
    }

    public LiveData<Resource<UserTransactionDetailsModel>> getUserSessionDetail() {
        return mDetailData;
    }

    public void submitReview(int rating) {
        NewReviewModel reviewData = new NewReviewModel();
//        BriefModel userData = new BriefModel();
//        userData.setPk();
//        reviewData.setUser(userData);
        reviewData.setBody(String.valueOf(rating));
        reviewData.setFoodRating(rating);
        reviewData.setAmbienceRating(rating);
        reviewData.setHospitalityRating(rating);
        mReviewData.addSource(mReviewRepository.postSessionReview(mSessionId, reviewData), mReviewData::setValue);
    }

    public LiveData<Resource<ObjectNode>> getUserReviewDetail() {
        return mReviewData;
    }

    @Override
    public void updateResults() {

    }
}
