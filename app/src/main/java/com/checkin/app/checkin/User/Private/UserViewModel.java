package com.checkin.app.checkin.User.Private;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Converters;
import com.checkin.app.checkin.Data.Message.MessageUtils;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitCallAsyncTask;
import com.checkin.app.checkin.misc.models.GenericDetailModel;
import com.checkin.app.checkin.User.ShopCustomerModel;
import com.checkin.app.checkin.User.UserModel;
import com.checkin.app.checkin.User.UserRepository;
import com.checkin.app.checkin.Utility.ProgressRequestBody;
import com.checkin.app.checkin.Utility.SourceMappedLiveData;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.List;

/**
 * Created by Jogi Miglani on 29-09-2018.
 */

public class UserViewModel extends BaseViewModel {
    private UserRepository mRepository;

    private SourceMappedLiveData<Resource<UserModel>> mUserData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<List<ShopCustomerModel>>> mUserRecentCheckinsData = createNetworkLiveData();
    private SourceMappedLiveData<Resource<Void>> mImageUploadResult = createNetworkLiveData();

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
                mImageUploadResult.postValue(Resource.Companion.loading(null));
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
                mImageUploadResult.postValue(Resource.Companion.success(null));
            }

            @Override
            public void onFailure() {
                super.onFailure();
                mImageUploadResult.postValue(Resource.Companion.error("Unable to upload image", null));
            }
        };
        doUploadImage(pictureFile, notificationUpdate);
    }

    private void doUploadImage(File pictureFile, ProgressRequestBody.UploadCallbacks listener) {
        new RetrofitCallAsyncTask<GenericDetailModel>(listener).execute(mRepository.postUserProfilePic(pictureFile, listener));
    }

    public void postUserData(String firstName, String lastName, String phone_token, String bio) {
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode();
        data.put("first_name", firstName);
        data.put("last_name", lastName);
        data.put("bio", bio);
        data.put("is_public", true);

        if (!phone_token.isEmpty())
            data.put("phone_token", phone_token);

        mUserData.addSource(mRepository.postUserData(data), mUserData::setValue);
    }

    public void patchUserPhone(@NonNull String phoneToken) {
        ObjectNode data = Converters.INSTANCE.getObjectMapper().createObjectNode()
                .put("phone_token", phoneToken);
        mUserData.addSource(mRepository.postUserData(data), mUserData::setValue);
    }
}
