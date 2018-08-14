package com.alcatraz.admin.project_alcatraz.Session;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.alcatraz.admin.project_alcatraz.Data.Resource;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;

/**
 * Created by Bhavik Patel on 05/08/2018.
 */

public class ActiveSessionViewModel extends AndroidViewModel {

    private final ActiveSessionRepository mRepository;
    private LiveData<Resource<ActiveSessionModel>> mData;
    private SharedPreferences mPrefs;

    public ActiveSessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = ActiveSessionRepository.getInstance(application);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
    }

    public LiveData<Resource<ActiveSessionModel>> getActiveSessionModel() {
        return mData = mRepository.getActiveSessionModel(mPrefs.getInt(Constants.SESSION_ID,0));
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
            return (T) new ActiveSessionViewModel(mApplication);
        }
    }
}
