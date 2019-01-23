package com.checkin.app.checkin.Review.NewReview;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.Review.ReviewRepository;

import java.io.File;
import java.util.List;

public class NewReviewViewModel extends BaseViewModel {
    private static final String TAG = NewReviewViewModel.class.getSimpleName();

    private ReviewRepository mRepository;
    private MediatorLiveData<Resource<GenericDetailModel>> mImageData = new MediatorLiveData<>();
    private NewReviewModel mReviewData;
    private MediatorLiveData<Resource<List<ReviewImageShowModel>>> mShowData = new MediatorLiveData<>();

    public NewReviewViewModel(@NonNull Application application) {
        super(application);
        mRepository = ReviewRepository.getInstance(application);
    }

    private void addImagePk(String imagePk) {
        if (mReviewData == null)
            mReviewData = new NewReviewModel();
        mReviewData.addImage(imagePk);
    }

    private void removeImagePk(String imagePk) {
        if (mReviewData == null)
            mReviewData = new NewReviewModel();
        mReviewData.removeImage(imagePk);
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

    public void uploadReviewImage(File pictureFile, ReviewImageModel.REVIEW_IMAGE_USE_CASE useCase, final int pos) {
        Log.e(TAG, useCase.tag);
        ReviewImageModel data = new ReviewImageModel(useCase);
        mImageData.addSource(mRepository.postReviewImage(pictureFile, data), resource -> {
            if (resource == null)
                return;
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                GenericDetailModel detailModel = resource.data;
                NewReviewViewModel.this.addImagePk(detailModel.getPk());
                resource.data.setIdentifier(pos);
                resource.data.setImage(pictureFile);
            }
            mImageData.setValue(resource);
        });
    }

    public MediatorLiveData<Resource<GenericDetailModel>> getImageData() {
        return mImageData;
    }

//    public LiveData<Resource<List<ReviewImageShowModel>>> showImagesData() {
//        mShowData.addSource(mRepository.getSessionOrdersDetails(),mShowData::setValue);
//        mShowData.postValue(mImageData.getValue());
//        return mShowData;
//    }

    public void deleteReviewImage(ReviewImageShowModel genericDetailModel){
        mData.addSource(mRepository.deleteSelectedImage(genericDetailModel.getPk()), objectNodeResource -> {
            if (objectNodeResource == null)
                return;
            if (objectNodeResource.status == Resource.Status.SUCCESS && objectNodeResource.data != null) {
                NewReviewViewModel.this.removeImagePk(genericDetailModel.getPk());
            }
            mImageData.setValue(objectNodeResource);
        });
    }



    @Override
    public void updateResults() {

    }
}
