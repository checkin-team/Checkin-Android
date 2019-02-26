package com.checkin.app.checkin.User.Private;

import android.app.Application;
import android.content.Context;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitCallAsyncTask;
import com.checkin.app.checkin.Misc.GenericDetailModel;
import com.checkin.app.checkin.User.ShopCustomerModel;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.User.UserRepository;
import com.checkin.app.checkin.Utility.ProgressRequestBody;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Created by Jogi Miglani on 29-09-2018.
 */

public class UserViewModel extends BaseViewModel {
    private UserRepository mRepository;
    private MediatorLiveData<Resource<UserModel>> mUserData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<ShopCustomerModel>>> mUserRecentCheckinsData = new MediatorLiveData<>();
    private MutableLiveData<Resource<Void>> mImageUploadResult = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        mRepository = UserRepository.getInstance(application);
    }

    @Override
    public void updateResults() {
        fetchUserData();
    }

    public void fetchUserData() {
        mUserData.addSource(mRepository.getUser(0), mUserData::setValue);
    }

    public void fetchUserRecentCheckinsData() {
        mUserRecentCheckinsData.addSource(mRepository.getUserRecentCheckins(), mUserRecentCheckinsData::setValue);
    }

    public LiveData<Resource<UserModel>> getUserData() {
        return mUserData;
    }

    public LiveData<Resource<List<ShopCustomerModel>>> getUserRecentCheckinsData() {
        return mUserRecentCheckinsData;
    }

    public LiveData<Resource<Void>> getImageUploadResult() {
        return mImageUploadResult;
    }

    public void updateProfilePic(File pictureFile, Context context) {
        NotificationCompat.Builder builder = MessageUtils.createUploadNotification(context);
        MessageUtils.NotificationUpdate notificationUpdate = new MessageUtils.NotificationUpdate(context, builder) {
            @Override
            public void onProgressUpdate(int percentage) {
                super.onProgressUpdate(percentage);
                mImageUploadResult.setValue(Resource.loading(null));
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
                mImageUploadResult.setValue(Resource.success(null));
            }

            @Override
            public void onFailure() {
                super.onFailure();
                mImageUploadResult.setValue(Resource.error("Unable to upload image", null));
            }
        };
        doUploadImage(pictureFile, notificationUpdate);
    }

    private void doUploadImage(File pictureFile, ProgressRequestBody.UploadCallbacks listener) {
        new RetrofitCallAsyncTask<GenericDetailModel>(listener).execute(mRepository.postUserProfilePic(pictureFile, listener));
    }

    public void postUserData(String firstName, String lastName) {
        ObjectNode data = Converters.objectMapper.createObjectNode();
        data.put("first_name", firstName);
        data.put("last_name", lastName);
        mData.addSource(mRepository.postUserData(data), mData::setValue);
    }
}
