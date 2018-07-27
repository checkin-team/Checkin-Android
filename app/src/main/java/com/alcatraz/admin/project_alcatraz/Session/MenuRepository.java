package com.alcatraz.admin.project_alcatraz.Session;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import com.alcatraz.admin.project_alcatraz.Data.ApiClient;
import com.alcatraz.admin.project_alcatraz.Data.ApiResponse;
import com.alcatraz.admin.project_alcatraz.Data.AppDatabase;
import com.alcatraz.admin.project_alcatraz.Data.NetworkBoundResource;
import com.alcatraz.admin.project_alcatraz.Data.Resource;
import com.alcatraz.admin.project_alcatraz.Data.WebApiService;

import java.util.List;

import javax.inject.Singleton;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;

@Singleton
public class MenuRepository {
    private static MenuRepository INSTANCE;
    private Box<MenuItem> mMenuItemModel;
    private Box<MenuGroup> mMenuGroupModel;
    private WebApiService mWebService;

    private MenuRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
        mMenuItemModel = AppDatabase.getMenuItemModel(context);
        mMenuGroupModel = AppDatabase.getMenuGroupModel(context);
    }
    public LiveData<List<MenuItem>> getMenuItems(final int menuId) {
        /*return new NetworkBoundResource<List<MenuItem>, List<MenuItem>>() {

            @Override
            protected void saveCallResult(@NonNull List<MenuItem> item) {
                mMenuItemModel.put(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<MenuItem> data) {
                return false;
            }

            @Override
            protected boolean shouldUseLocalDb() {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<MenuItem>> loadFromDb() {
                return new ObjectBoxLiveData<>(mMenuItemModel.query().equal(MenuItem_.menuId, menuId).build());
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<MenuItem>>> createCall() {
                // TODO: Network Fetching!!!!
                return null;
            }
        }.getAsLiveData();*/
        return new ObjectBoxLiveData<>(mMenuItemModel.query().equal(MenuItem_.menuId, menuId).build());
    }

    public LiveData<Resource<List<MenuGroup>>> getMenuGroups(final int menuId) {
        return new NetworkBoundResource<List<MenuGroup>, List<MenuGroup>>() {

            @Override
            protected void saveCallResult(@NonNull List<MenuGroup> item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable List<MenuGroup> data) {
                return false;
            }

            @Override
            protected boolean shouldUseLocalDb() {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<MenuGroup>> loadFromDb() {
                return new ObjectBoxLiveData<>(mMenuGroupModel.query().equal(MenuGroup_.menuId, menuId).build());
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<MenuGroup>>> createCall() {
                return null;
            }
        }.getAsLiveData();
    }

    public static MenuRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (MenuRepository.class) {
                if (INSTANCE == null) {
                    Context context = application.getApplicationContext();
                    INSTANCE = new MenuRepository(context);
                }
            }
        }
        return INSTANCE;
    }
}
