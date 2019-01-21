package com.checkin.app.checkin.Review.NewReview;

import android.app.Application;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Review.ReviewRepository;

import java.io.File;

public class NewReviewViewModel extends BaseViewModel {
    private ReviewRepository mRepository;
    private MediatorLiveData<Resource<GenericDetailModel>> mImageData = new MediatorLiveData<>();
    private NewReviewModel mReviewData;

    public NewReviewViewModel(@NonNull Application application) {
        super(application);
        mRepository = ReviewRepository.getInstance(application);
    }

    private void addImagePk(String imagePk) {
        if (mReviewData == null)
            mReviewData = new NewReviewModel();
        mReviewData.addImage(imagePk);
    }

    public void updateBody(String body) {
        if (mReviewData == null)
            mReviewData = new NewReviewModel();
        mReviewData.setBody(body);
    }

    public void updateRating(int foodRating, int ambienceRating, int serviceRating) {
        if (mReviewData == null)
            mReviewData = new NewReviewModel();
        mReviewData.setFoodRating(foodRating);
        mReviewData.setAmbienceRating(ambienceRating);
        mReviewData.setHospitalityRating(serviceRating);
    }

    public boolean submitReview() {
        if (mReviewData == null || !mReviewData.isValidData())
            return false;
        mData.addSource(mRepository.postSessionReview("1", mReviewData), mData::setValue);
        return true;
    }

    public void uploadReviewImage(File pictureFile, ReviewImageModel.REVIEW_IMAGE_USE_CASE useCase) {
        ReviewImageModel data = new ReviewImageModel(useCase);
        mImageData.addSource(mRepository.postReviewImage(pictureFile, data), resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                GenericDetailModel detailModel = resource.data;
                addImagePk(detailModel.getPk());
            }
            mImageData.setValue(resource);
        });
    }

    @Override
    public void updateResults() {

    }
}
