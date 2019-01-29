package com.checkin.app.checkin.RestaurantImage;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Misc.BriefModel;

import java.util.ArrayList;
import java.util.List;

public class ImageGalleryViewModel extends BaseViewModel {

    private ImageGalleryRepository mImageGalleryRepository;
    private MediatorLiveData<Resource<ImageGalleryModel>> mResourceMediatorLiveData = new MediatorLiveData<>();

    public ImageGalleryViewModel(@NonNull Application application) {
        super(application);
        mImageGalleryRepository = ImageGalleryRepository.getInstance(application);
    }

    public void getImageGalleryById(String reviewId){
        mResourceMediatorLiveData.addSource(mImageGalleryRepository.getImageGalleryById(reviewId),mResourceMediatorLiveData::setValue);
    }

    LiveData<Resource<ImageGalleryModel>> getImageGalleryModel(){
        return mResourceMediatorLiveData;
    }

    @Override
    public void updateResults() {

    }

     void dummyData() {
        ImageGalleryModel imageGalleryModel = new ImageGalleryModel();
        BriefModel briefModel = new BriefModel();

        briefModel.setPk("1");
        briefModel.setDisplayName("Ashish Gupta");
        briefModel.setDisplayPic("https://storage.googleapis.com/checkin-app-18.appspot.com/__sized__/images/users/shivanshs9/profile_8183-thumbnail-100x100-70.jpg");

        List<String> mList = new ArrayList<>();

        for (int i = 0; i < 5; i++){
            mList.add("https://storage.googleapis.com/checkin-app-18.appspot.com/__sized__/images/users/shivanshs9/profile_8183-thumbnail-100x100-70.jpg");
        }

        imageGalleryModel.setTitle("Cheesy Butter Ji");
        imageGalleryModel.setUploader(briefModel);
        imageGalleryModel.setImages(mList);

        mResourceMediatorLiveData.setValue(Resource.success(imageGalleryModel));
    }
}
