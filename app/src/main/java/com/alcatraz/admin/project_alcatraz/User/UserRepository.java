package com.alcatraz.admin.project_alcatraz.User;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.alcatraz.admin.project_alcatraz.Data.ApiClient;
import com.alcatraz.admin.project_alcatraz.Data.AppDatabase;
import com.alcatraz.admin.project_alcatraz.Data.BaseRepository;
import com.alcatraz.admin.project_alcatraz.Data.WebApiService;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;

public class UserRepository extends BaseRepository {
    private final WebApiService mWebService;
    private static UserRepository INSTANCE = null;
    private Box<UserModel> mUserModel;

    private UserRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
        mUserModel = AppDatabase.getUserModel(context);
    }

    public LiveData<List<UserModel>> getAllUsers() {
        return new ObjectBoxLiveData<>(mUserModel.query().build());
    }

    public static UserRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
}
