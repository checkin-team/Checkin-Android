package com.checkin.app.checkin.ImageGallery;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;

public class ImageGalleryViewModel extends BaseViewModel {
    private ImageGalleryRepository mRepository;
    private MediatorLiveData<Resource<ImageGalleryModel>> mImagesData = new MediatorLiveData<>();

    public ImageGalleryViewModel(@NonNull Application application) {
        super(application);
        mRepository = ImageGalleryRepository.getInstance(application);
    }

    public void getReviewImages(long reviewId){
        mImagesData.addSource(mRepository.getReviewImages(reviewId), mImagesData::setValue);
    }

    LiveData<Resource<ImageGalleryModel>> getImageGalleryModel(){
        return mImagesData;
    }

    @Override
    public void updateResults() {

    }
}
