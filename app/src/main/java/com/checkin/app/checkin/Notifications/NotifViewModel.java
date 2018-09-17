package com.checkin.app.checkin.Notifications;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Utility.Constants;

import java.util.List;

/**
 * Created by Bhavik Patel on 22/08/2018.
 */

public class NotifViewModel extends BaseViewModel {

    private final NotifRepository mRepository;
    private MediatorLiveData<Resource<List<NotificationModel>>> mSessionData = new MediatorLiveData<>();
    private SharedPreferences mPrefs;

    public NotifViewModel(@NonNull Application application) {
        super(application);
        mRepository = NotifRepository.getInstance(application);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
    }

    public LiveData<Resource<List<NotificationModel>>> getNotifModel() {
        mSessionData.addSource(mRepository.getNotifModel(mPrefs.getInt(Constants.LAST_NOTIF_ID, 0)), mSessionData::setValue);
        return mSessionData;
    }

    @Override
    public void updateResults() {
        mSessionData.addSource(mRepository.getNotifModel(mPrefs.getInt(Constants.LAST_NOTIF_ID, 0)), mSessionData::setValue);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new NotifViewModel(mApplication);
        }
    }
}
