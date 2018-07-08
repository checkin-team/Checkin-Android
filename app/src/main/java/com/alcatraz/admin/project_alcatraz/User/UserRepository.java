package com.alcatraz.admin.project_alcatraz.User;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.alcatraz.admin.project_alcatraz.Data.ApiClient;
import com.alcatraz.admin.project_alcatraz.Data.AppRoomDatabase;
import com.alcatraz.admin.project_alcatraz.Data.BaseRepository;
import com.alcatraz.admin.project_alcatraz.Data.WebApiService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

public class UserRepository extends BaseRepository {
    private final UserDao mUserModel;
    private final WebApiService mWebService;
    private static UserRepository INSTANCE = null;

    private UserRepository(UserDao userDao) {
        mWebService = ApiClient.getApiService();
        mUserModel = userDao;
    }

    public LiveData<List<User>> getAllUsers() {
        return mUserModel.getAll();
    }

    public void insertUsers(User... users) {
        new insertUserAsyncTask(mUserModel).execute(users);
    }

    private static class insertUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao mAsyncTaskDao;
        insertUserAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(User... users) {
            mAsyncTaskDao.insertAll(users);
            return null;
        }
    }

    public static UserRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserRepository(AppRoomDatabase.getDatabase(context).userModel());
                }
            }
        }
        return INSTANCE;
    }
}
